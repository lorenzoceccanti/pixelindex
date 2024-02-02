package it.unipi.largescale.pixelindex.dao;

import it.unipi.largescale.pixelindex.dto.ReviewPreviewDTO;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewNeo4jDAO extends BaseNeo4jDAO {

    public void insertReview(String reviewId, String gameId, String author, String rating, String text, String timestamp) throws DAOException {
        try (Driver neoDriver = beginConnection()) {
            String query = """
                    MATCH (author:User {username: $author})
                    MATCH (game:Game {mongoId: $gameId})
                    CREATE (
                        review:Review {
                            mongoId: $reviewId,
                            excerpt: $text,
                            recommended: $rating,
                            postedDate: $timestamp
                            }
                        )
                    CREATE (author)-[:WRITES]->(review)
                    CREATE (review)-[:BELONGS]->(game)
                    """;
            Map<String, Object> params = new HashMap<>();
            params.put("reviewId", reviewId);
            params.put("gameId", gameId);
            params.put("author", author);
            params.put("rating", rating);
            params.put("text", text);
            params.put("timestamp", timestamp);

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


}
