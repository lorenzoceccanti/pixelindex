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
import it.unipi.largescale.pixelindex.dto.MostActiveUserDTO;
import it.unipi.largescale.pixelindex.dto.RegistrationStatsDTO;
import it.unipi.largescale.pixelindex.dto.UserReportsDTO;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import static com.mongodb.client.model.Accumulators.*;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Indexes.descending;
import static com.mongodb.client.model.Projections.*;
import static it.unipi.largescale.pixelindex.dao.mongo.BaseMongoDAO.beginConnection;

public class StatisticsMongoDAO {

    public ArrayList<UserReportsDTO> topNReportedUser(int n) throws DAOException {
        MongoDatabase db;
        ArrayList<Document> results;
        ArrayList<UserReportsDTO> userReportsDTOs = new ArrayList<>();
        try (MongoClient mongoClient = beginConnection(false)) {

            db = mongoClient.getDatabase("pixelindex");
            MongoCollection<Document> usersCollection = db.getCollection("users");
            // Aggregation Pipeline
            results = usersCollection.aggregate(
                    Arrays.asList(
                            Aggregates.match(Filters.exists("isBanned", false)),
                            Aggregates.match(Filters.exists("reported_by")),
                            Aggregates.project(Projections.fields(
                                    Projections.excludeId(),
                                    Projections.include("username"),
                                    Projections.computed("numberReports", new Document("$size", "$reported_by"))
                            )),
                            sort(Sorts.descending("numberReports")),
                            limit(n)
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

    public ArrayList<GameRatingDTO> topGamesByPositiveRatingRatio(int n) throws DAOException {
        ArrayList<GameRatingDTO> gameRatingDTOs = new ArrayList<>();

        try (MongoClient mongoClient = beginConnection(false)) {
            MongoDatabase database = mongoClient.getDatabase("pixelindex");
            MongoCollection<Document> collection = database.getCollection("reviews");

            List<Bson> aggregationPipeline = Arrays.asList(
                    group("$gameId",
                            first("gameName", "$gameName"),
                            first("gameReleaseYear", "$gameReleaseYear"),
                            sum("positiveReviews",
                                    new Document("$cond", Arrays.asList(
                                            new Document("$eq", Arrays.asList("$recommended", true)), 1, 0))),
                            sum("negativeReviews",
                                    new Document("$cond", Arrays.asList(
                                            new Document("$eq", Arrays.asList("$recommended", false)), 1, 0))),
                            sum("totalReviews", 1)),
                    match(gte("totalReviews", 15)),
                    project(fields(
                            include("gameName", "gameReleaseYear"),
                            computed("positiveRatingRatio",
                                    new Document("$multiply", Arrays.asList(
                                            new Document("$divide", Arrays.asList("$positiveReviews",
                                                    new Document("$add", Arrays.asList("$positiveReviews", "$negativeReviews")))), 100)))
                    )),
                    sort(descending("positiveRatingRatio")),
                    limit(n)
            );

            AggregateIterable<Document> results = collection.aggregate(aggregationPipeline);
            int rank = 1;
            for (Document doc : results) {
                GameRatingDTO dto = new GameRatingDTO();
                dto.setRank(rank);
                dto.setName(doc.getString("gameName"));
                if (doc.get("gameReleaseYear") != null) {
                    dto.setReleaseYear(doc.getInteger("gameReleaseYear"));
                }
                dto.setPositiveRatingRatio(doc.getDouble("positiveRatingRatio"));
                gameRatingDTOs.add(dto);
                rank++;
            }
        } catch (Exception e) {
            throw new DAOException("Error retrieving games: " + e.getMessage());
        }
        return gameRatingDTOs;
    }

    public ArrayList<MostActiveUserDTO> findTopReviewersByReviewsCountLastMonth(int n) throws DAOException {

        try (MongoClient mongoClient = beginConnection(false)) {
            MongoDatabase database = mongoClient.getDatabase("pixelindex");
            MongoCollection<Document> collection = database.getCollection("reviews");

            // For the sake of testing, we set the month to November 2023
            ZonedDateTime now = ZonedDateTime.of(2023, 11, 1, 0, 0, 0, 0, ZoneId.of("UTC"));
            ZonedDateTime firstDayLastMonth =
                    now.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate().atStartOfDay(ZoneId.of("UTC"));
            ZonedDateTime lastDayLastMonth =
                    now.with(TemporalAdjusters.lastDayOfMonth()).toLocalDate().atStartOfDay(ZoneId.of("UTC")).plusDays(1).minusSeconds(1);

            Bson matchStage = match(
                    and(gte("postedDate", firstDayLastMonth.toInstant()),
                            lte("postedDate", lastDayLastMonth.toInstant())));
            Bson groupStage = group("$author", sum("count", 1));
            Bson sortStage = sort(new Document("count", -1));
            Bson limitStage = limit(n);

            AggregateIterable<Document> result = collection.aggregate(Arrays.asList(matchStage, groupStage, sortStage, limitStage));
            ArrayList<MostActiveUserDTO> userDTOs = new ArrayList<>();
            int count = 1;
            for (Document user : result) {
                MostActiveUserDTO userDTO = new MostActiveUserDTO();
                userDTO.setRank(count);
                userDTO.setUsername(user.getString("_id"));
                userDTO.setNumOfReviews(user.getInteger("count", 0));
                userDTOs.add(userDTO);
                count++;
            }
            return userDTOs;
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    public ArrayList<RegistrationStatsDTO> numberOfRegistrationsByMonth(long year) throws DAOException {
        try (MongoClient mongoClient = beginConnection(false)) {
            MongoDatabase database = mongoClient.getDatabase("pixelindex");
            MongoCollection<Document> collection = database.getCollection("users");

            AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$match",
                            new Document("$expr",
                                    new Document("$eq", Arrays.asList(new Document("$year", "$registrationDate"), year)))),
                    new Document("$project",
                            new Document("month",
                                    new Document("$month", "$registrationDate"))
                                    .append("age",
                                            new Document("$subtract", Arrays.asList(2024L,
                                                    new Document("$year", "$dateOfBirth"))))),
                    new Document("$group",
                            new Document("_id",
                                    new Document("month", "$month")
                                            .append("ageGroup",
                                                    new Document("$switch",
                                                            new Document("branches", Arrays.asList(new Document("case",
                                                                            new Document("$lt", Arrays.asList("$age", 18L)))
                                                                            .append("then", "< 18 y.o."),
                                                                    new Document("case",
                                                                            new Document("$and", Arrays.asList(new Document("$gte", Arrays.asList("$age", 18L)),
                                                                                    new Document("$lte", Arrays.asList("$age", 30L)))))
                                                                            .append("then", "18-30 y.o."),
                                                                    new Document("case",
                                                                            new Document("$and", Arrays.asList(new Document("$gt", Arrays.asList("$age", 30L)),
                                                                                    new Document("$lte", Arrays.asList("$age", 50L)))))
                                                                            .append("then", "30-50 y.o."),
                                                                    new Document("case",
                                                                            new Document("$gt", Arrays.asList("$age", 50L)))
                                                                            .append("then", "50+ y.o")))
                                                                    .append("default", "Other"))))
                                    .append("count",
                                            new Document("$sum", 1L))),
                    new Document("$group",
                            new Document("_id",
                                    new Document("month", "$_id.month"))
                                    .append("properties",
                                            new Document("$push",
                                                    new Document("ageGroup", "$_id.ageGroup")
                                                            .append("count", "$count")))),
                    new Document("$project",
                            new Document("month", "$_id.month")
                                    .append("properties", 1L)
                                    .append("_id", 0L)),
                    new Document("$sort",
                            new Document("month", 1L))));

            HashMap<Integer, List<Document>> hashMap = new HashMap<>();
            ArrayList<RegistrationStatsDTO> registrationStatsDTOs = new ArrayList<>();
            List<Document> properties = new ArrayList<>();
            for (Document doc : result) {
                hashMap.put(doc.getInteger("month"), doc.getList("properties", Document.class));
            }

            for (Map.Entry<Integer, List<Document>> entry : hashMap.entrySet()) {
                RegistrationStatsDTO registrationStatsDTO = new RegistrationStatsDTO();
                registrationStatsDTO.setMonth(entry.getKey());

                HashMap<String, Long> hashMap1 = new HashMap<>();
                for (int i = 0; i < entry.getValue().size(); i++) {
                    hashMap1.put(entry.getValue().get(i).getString("ageGroup"), entry.getValue().get(i).getLong("count"));
                }
                registrationStatsDTO.setHashMap(hashMap1);
                registrationStatsDTOs.add(registrationStatsDTO);
            }
            return registrationStatsDTOs;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DAOException(e);
        }
    }
}
