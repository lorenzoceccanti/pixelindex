package it.unipi.largescale.pixelindex.dao.neo4j;

import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.model.Reaction;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

import java.util.HashMap;
import java.util.Map;

public class ReviewNeo4jDAO extends BaseNeo4jDAO {

    public void insertReview(String reviewId, String gameId, String author) throws DAOException {
        try (Driver neoDriver = beginConnection()) {
            String query = """
                    MATCH (author:User {username: $author})
                    MATCH (game:Game {mongoId: $gameId})
                    CREATE (
                        review:Review {
                            mongoId: $reviewId
                            }
                        )
                    CREATE (author)-[:WRITES]->(review)
                    CREATE (review)-[:BELONGS]->(game)
                    """;
            Map<String, Object> params = new HashMap<>();
            params.put("reviewId", reviewId);
            params.put("gameId", gameId);
            params.put("author", author);

            try (Session session = neoDriver.session()) {
                session.executeWrite(tx -> {
                    tx.run(query, params);
                    return null;
                });
            }
        } catch (Exception ex) {
            throw new DAOException(ex);
        }
    }

    public void removeReview(String reviewId) throws DAOException {
        try (Driver neoDriver = beginConnection()) {
            String query = """
                    MATCH (review:Review {mongoId: $reviewId})
                    DETACH DELETE review
                    """;
            Map<String, Object> params = new HashMap<>();
            params.put("reviewId", reviewId);

            try (Session session = neoDriver.session()) {
                session.executeWrite(tx -> {
                    tx.run(query, params);
                    return null;
                });
            }
        } catch (Exception ex) {
            throw new DAOException(ex);
        }
    }

    public void addReaction(String reviewId, String username, Reaction reaction) throws DAOException {
        // TODO: QUERY DA RIFARE / FINIRE
        try (Driver neoDriver = beginConnection()) {
            String query = """
                    MATCH (review:Review {mongoId: $reviewId}), (user:User {username: $username})
                    WHERE NOT EXISTS {(user)-[:LIKES {value: $value}]->(review)}
                    """;
            Map<String, Object> params = new HashMap<>();
            params.put("reviewId", reviewId);
            params.put("username", username);
            params.put("value", reaction == Reaction.LIKE);

            try (Session session = neoDriver.session()) {
                session.executeWrite(tx -> {
                    tx.run(query, params);
                    return null;
                });
            }
        } catch (Exception ex) {
            throw new DAOException(ex);
        }
    }

    // TODO: test della funzione
    public Map<String, Integer> getReactionsCount(String reviewId) throws DAOException {
        try (Driver neoDriver = beginConnection()) {
            String query = """
                    MATCH (r:Review {mongoId: $reviewId})<-[l:LIKES]-(:User)
                    RETURN
                        SUM(CASE l.value WHEN true THEN 1 ELSE 0 END) AS likes,
                        SUM(CASE l.value WHEN false THEN 1 ELSE 0 END) AS dislikes
                    """;
            Map<String, Object> params = new HashMap<>();
            params.put("reviewId", reviewId);

            try (Session session = neoDriver.session()) {

                return session.executeWrite(tx -> {
                    Result result = tx.run(query, params);
                    int likes = result.single().get("likes").asInt();
                    int dislikes = result.single().get("dislikes").asInt();

                    Map<String, Integer> reactions = new HashMap<>();
                    reactions.put("likes", likes);
                    reactions.put("dislikes", dislikes);

                    return reactions;
                });
            }
        } catch (Exception ex) {
            throw new DAOException(ex);
        }
    }


}
