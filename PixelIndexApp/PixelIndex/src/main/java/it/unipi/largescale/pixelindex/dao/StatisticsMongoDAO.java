package it.unipi.largescale.pixelindex.dao;

import com.mongodb.MongoSocketException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import it.unipi.largescale.pixelindex.dto.UserReportsDTO;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;

import static it.unipi.largescale.pixelindex.dao.BaseMongoDAO.beginConnection;

public class StatisticsMongoDAO {

    public ArrayList<UserReportsDTO> topNReportedUser(int n) throws DAOException
    {
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
            for(int i=0; i< results.size(); i++)
            {
                UserReportsDTO userReport = new UserReportsDTO();
                userReport.setUsername(results.get(i).getString("username"));
                userReport.setNumberReports(results.get(i).getInteger("numberReports"));
                userReportsDTOs.add(userReport);
            }
        }catch(MongoSocketException ex)
        {
            throw new DAOException("Error in connecting to MongoDB");
        }
        return userReportsDTOs;
    }
}
