package it.unipi.largescale.pixelindex.dao.neo4j;

import it.unipi.largescale.pixelindex.dto.UserSearchDTO;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.exceptions.ServiceUnavailableException;
import org.w3c.dom.DOMException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.neo4j.driver.Values.parameters;

public class RegisteredUserNeo4jDAO {
    public void register(String username) throws DAOException {
        try (Driver neoDriver = BaseNeo4jDAO.beginConnection();
             Session session = neoDriver.session()) {
            session.executeWrite(tx -> {
                tx.run("MERGE (u:User {username: $username})",
                        parameters("username", username));
                return null;
            });
        } catch (ServiceUnavailableException ex) {
            throw new DAOException("Cannot reach Neo4j Server");
        }
    }


    /**
     * This method will trigger a FOLLOWS relationship
     *
     * @param usernameSrc The username that press on follow
     * @param usernameDst The username followed
     * @throws DAOException
     */
    public String followUser(String usernameSrc, String usernameDst) throws DAOException {
        // Simplified and cleaner approach using try-with-resources for automatic resource management
        try (Driver neoDriver = BaseNeo4jDAO.beginConnection();
             Session session = neoDriver.session()) {
            return session.executeWrite(tx -> {
                Result result = tx.run("MATCH (user1:User {username: $usernameSrc}), " +
                                "(user2:User {username: $usernameDst}) " +
                                "OPTIONAL MATCH (user1)-[f:FOLLOWS]->(user2) " +
                                "CALL apoc.do.when(f IS NOT NULL, " +
                                "    'DELETE f RETURN \"deleted\"', " +
                                "    'CREATE (user1)-[:FOLLOWS]->(user2) RETURN \"created\"', " +
                                "    {user1: user1, user2: user2, f: f}) " +
                                "YIELD value " +
                                "RETURN value as action",
                        parameters("usernameSrc", usernameSrc, "usernameDst", usernameDst));
                if (result.hasNext()) {
                    Value value = result.single().get("action");
                    if (!value.isNull()) {
                        String str = value.asMap().toString();
                        String[] parts = str.split("=");
                        return parts[1].substring(0,parts[1].length()-1);
                    }
                }
                return "No result"; // Returning a default message when no action is taken
            }); // Returning the result of the transaction
        } catch (ServiceUnavailableException ex) {
            throw new DAOException("Cannot reach Neo4j Server");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DAOException("Error processing followUser operation");
        }
    }

    /** This method is called by the Consistency Thread eventually
     *  to align the consistency between Neo4j and MongoDb
     */
    public Map<String,Integer> getFollowsCount(String src, String dst) throws DAOException{
        try(Driver neoDriver = BaseNeo4jDAO.beginConnection();
            Session session = neoDriver.session()){
            return session.executeRead(tx->{
                Result result = tx.run("OPTIONAL MATCH (u:User{username:$src})-[f1:FOLLOWS]->(m:User) "
                + "WITH COUNT(f1) AS followingSrc "
                + "OPTIONAL MATCH (u1:User{username:$dst})<-[f2:FOLLOWS]-(n:User) "
                + "RETURN followingSrc, COUNT(f2) AS followerDst;",
                        parameters("src",src,"dst",dst));

                if(result.hasNext())
                {
                    Record record = result.single();
                    int followingSrc = record.get("followingSrc").asInt();
                    int followerDst = record.get("followerDst").asInt();

                    Map<String, Integer> counts = new HashMap<>();
                    counts.put("followingSrc",followingSrc);
                    counts.put("followerDst",followerDst);

                    return counts;
                } else {
                    throw new RuntimeException("No record founds");
                }
            });
        }catch(ServiceUnavailableException ex)
        {
            throw new DAOException("Cannot reach Neo4j Server");
        }
    }


    /** Drops an user with all the involved relationships from the graph
     *
     * @param username The username to be removed
     * @throws DAOException
     */
    public void deleteUser(String username) throws DAOException{
        try(Driver neoDriver = BaseNeo4jDAO.beginConnection();
            Session session = neoDriver.session()){
            session.executeWrite(tx -> {
                tx.run("MATCH (u:User {username: $username})-[w:WRITES]->(re:Review) " +
                        "DETACH DELETE u, re;", parameters("username", username));
                return null;
            });
        }catch (ServiceUnavailableException ex) {
            throw new DAOException("Cannot reach Neo4j Server");
        }
    }
}