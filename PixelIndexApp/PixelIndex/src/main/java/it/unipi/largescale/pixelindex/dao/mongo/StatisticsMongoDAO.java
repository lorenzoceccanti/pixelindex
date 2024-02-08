package it.unipi.largescale.pixelindex.dao.mongo;

import com.mongodb.MongoSocketException;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import it.unipi.largescale.pixelindex.dto.GameRatingDTO;
import it.unipi.largescale.pixelindex.dto.UserReportsDTO;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.utils.Utils;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static it.unipi.largescale.pixelindex.dao.mongo.BaseMongoDAO.beginConnection;
import static it.unipi.largescale.pixelindex.dao.mongo.BaseMongoDAO.beginConnectionWithoutReplica;

public class StatisticsMongoDAO {

    public ArrayList<UserReportsDTO> topNReportedUser(int n) throws DAOException {
        MongoDatabase db;
        ArrayList<Document> results = null;
        ArrayList<UserReportsDTO> userReportsDTOs = new ArrayList<>();
        try (MongoClient mongoClient = beginConnection()) {

            db = mongoClient.getDatabase("pixelindex");
            MongoCollection<Document> usersCollection = db.getCollection("users");
            // Aggregation Pipeline
            results = usersCollection.aggregate(
                    Arrays.asList(
                            Aggregates.match(Filters.exists("reported_by")),
                            Aggregates.project(Projections.fields(
                                    Projections.excludeId(),
                                    Projections.include("username"),
                                    Projections.computed("numberReports", new Document("$size", "$reported_by"))
                            )),
                            Aggregates.sort(Sorts.descending("numberReports")),
                            Aggregates.limit(n)
                    )
            ).into(new ArrayList<>());
            for (Document result : results) {
                UserReportsDTO userReport = new UserReportsDTO();
                userReport.setUsername(result.getString("username"));
                userReport.setNumberReports(result.getInteger("numberReports"));
                userReportsDTOs.add(userReport);
            }
        } catch (MongoSocketException ex) {
            throw new DAOException("Error in connecting to MongoDB");
        }
        return userReportsDTOs;
    }

    public ArrayList<GameRatingDTO> topGamesByPositiveRatingRatio() throws DAOException {
        ArrayList<GameRatingDTO> gameByRatingDTOS = new ArrayList<>();

        try (MongoClient mongoClient = beginConnectionWithoutReplica()) {
            MongoDatabase database = mongoClient.getDatabase("pixelindex");
            MongoCollection<Document> collection = database.getCollection("reviews");

            List<Bson> aggregationPipeline = Arrays.asList(
                    new Document("$group",
                            new Document("_id", "$gameId")
                                    .append("positiveReviews",
                                            new Document("$sum",
                                                    new Document("$cond", Arrays.asList("$recommended", 1L, 0L))))
                                    .append("negativeReviews",
                                            new Document("$sum",
                                                    new Document("$cond", Arrays.asList("$recommended", 0L, 1L))))
                                    .append("totalReviews",
                                            new Document("$sum", 1L))),
                    new Document("$project",
                            new Document("positiveReviews", 1L)
                                    .append("negativeReviews", 1L)
                                    .append("totalReviews", 1L)
                                    .append("ratio",
                                            new Document("$divide", Arrays.asList("$positiveReviews",
                                                    new Document("$cond", Arrays.asList(new Document("$eq",
                                                            Arrays.asList("$negativeReviews", 0L)), 1L, "$negativeReviews")))))),
                    new Document("$addFields",
                            new Document("weightedRatio",
                                    new Document("$divide", Arrays.asList("$ratio",
                                            new Document("$add", Arrays.asList(1L,
                                                    new Document("$divide", Arrays.asList("$totalReviews", 10L)))))))),
                    new Document("$sort",
                            new Document("weightedRatio", -1L)),
                    new Document("$limit", 10),
                    new Document("$lookup",
                            new Document("from", "games")
                                    .append("localField", "_id")
                                    .append("foreignField", "_id")
                                    .append("as", "gameDetails")),
                    new Document("$unwind", "$gameDetails"),
                    new Document("$project",
                            new Document("_id", 1L)
                                    .append("positiveReviews", 1L)
                                    .append("negativeReviews", 1L)
                                    .append("gameName", "$gameDetails.name")
                                    .append("firstReleaseDate", "$gameDetails.first_release_date")));

            AggregateIterable<Document> results = collection.aggregate(aggregationPipeline);
            for (Document doc : results) {
                GameRatingDTO dto = new GameRatingDTO();
                Document gameDetails = (Document) doc.get("gameDetails");

                dto.setName(gameDetails.getString("name"));
                dto.setReleaseYear(Utils.convertDateToLocalDate(gameDetails.getDate("first_release_date")).getYear());
                double positiveRatio = doc.getDouble("positiveReviews") / (doc.getDouble("positiveReviews") +
                        doc.getDouble("negativeReviews")) * 100;
                dto.setPositiveRatingRatio(positiveRatio);

                gameByRatingDTOS.add(dto);
            }
        } catch (Exception e) {
            throw new DAOException("Error retrieving games: " + e.getMessage());
        }
        return gameByRatingDTOS;
    }

}
