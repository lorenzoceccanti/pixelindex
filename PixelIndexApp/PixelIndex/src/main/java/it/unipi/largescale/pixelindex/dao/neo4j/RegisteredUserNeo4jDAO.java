package it.unipi.largescale.pixelindex.dao.neo4j;

import it.unipi.largescale.pixelindex.dto.UserSearchDTO;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.exceptions.ServiceUnavailableException;

import java.util.ArrayList;

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
     * This method returns the list of the users matching the username
     * passed as parameter along with the numberOfFollowers and the
     * numberOfFollowing
     *
     * @param param The query putted by the user
     */
    public ArrayList<UserSearchDTO> searchUser(String param) throws DAOException {
        ArrayList<UserSearchDTO> returnObject;
        // I need first of all to put all in lower case the query parameter
        String lowerCasePar = param.toLowerCase();
        try (Driver neoDriver = BaseNeo4jDAO.beginConnection();
             Session session = neoDriver.session()) {
            returnObject = session.executeRead(tx -> {
                Result result = tx.run("MATCH (matchingUser:User) WHERE toLower(matchingUser.username) CONTAINS $lowerCasePar " +
                                "WITH matchingUser AS listedUser " +
                                "ORDER BY listedUser.username ASC " +
                                "LIMIT 10 " +
                                "OPTIONAL MATCH (follower:User)-[:FOLLOWS]->(listedUser) " +
                                "WITH listedUser, COUNT(follower) AS numberOfFollowers " +
                                "OPTIONAL MATCH (listedUser)-[:FOLLOWS]->(followed:User) " +
                                "RETURN listedUser.username AS user, numberOfFollowers, COUNT(followed) AS numberOfFollowed",
                        parameters("lowerCasePar", lowerCasePar));
                ArrayList<UserSearchDTO> userSearchDTOArrayList = new ArrayList<>();
                while (result.hasNext()) {
                    Record r = result.next();
                    UserSearchDTO userSearchDTO = new UserSearchDTO();
                    userSearchDTO.setUsername(r.get("user").asString());
                    userSearchDTO.setCountFollower(r.get("numberOfFollowers").asInt());
                    userSearchDTO.setCountFollowed(r.get("numberOfFollowed").asInt());
                    userSearchDTOArrayList.add(userSearchDTO);
                }
                return userSearchDTOArrayList;
            });
        } catch (ServiceUnavailableException ex) {
            throw new DAOException("Cannot reach Neo4j Server");
        }
        return returnObject;
    }

    /**
     * This method will trigger a FOLLOWS relationship
     *
     * @param usernameSrc The username that press on follow
     * @param usernameDst The username followed
     * @throws DAOException
     */
    public void followUser(String usernameSrc, String usernameDst) throws DAOException {
        /* It does not matter to worry about the user existence,
        since this method is called after the search*/
        try (Driver neoDriver = BaseNeo4jDAO.beginConnection();
             Session session = neoDriver.session()) {
            session.executeWrite(tx -> {
                tx.run("MATCH (src: User) WHERE src.username = $usernameSrc " +
                                "MATCH (dst: User) WHERE dst.username = $usernameDst " +
                                "MERGE (src)-[:FOLLOWS]->(dst);",
                        parameters("usernameSrc", usernameSrc, "usernameDst", usernameDst));
                return null;
            });
        } catch (ServiceUnavailableException ex) {
            throw new DAOException("Cannot reach Neo4j Server");
        }
    }

    public void unfollowUser(String usernameSrc, String usernameDst) throws DAOException {
        try (Driver neoDriver = BaseNeo4jDAO.beginConnection();
             Session session = neoDriver.session()) {
            session.executeWrite(tx -> {
                tx.run("MATCH (src:User{username:$usernameSrc})-[r:FOLLOWS]->(dst:User{username:$usernameDst}) " +
                        "DELETE r;", parameters("usernameSrc", usernameSrc, "usernameDst", usernameDst));
                return null;
            });
        } catch (ServiceUnavailableException ex) {
            throw new DAOException("Cannot reach Neo4j Server");
        }
    }
}