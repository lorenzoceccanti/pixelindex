package it.unipi.largescale.pixelindex.dao;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import it.unipi.largescale.pixelindex.model.RegisteredUser;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Filters.eq;

public class UserMongoDAO extends BaseMongoDAO implements UserDAO{
    @Override
    public RegisteredUser makeLogin(String username, String password)
    {
        // Questa è una semplice prova
        MongoClient mongoClient = beginConnection();

        MongoDatabase db = mongoClient.getDatabase("pixelindex");
        MongoCollection<Document> usersCollection = db.getCollection("users");

        Bson projectionFields = Projections.fields(
                Projections.include("username"),
                Projections.excludeId()
        );
        Bson myMatch = (eq("username", "maskedgingerjock"));
        List<Document> results = usersCollection.find(myMatch).into(new ArrayList<>());
        for(int i = 0; i<results.size(); i++)
            System.out.println(results.get(i));

        RegisteredUser ru = new RegisteredUser();
        return ru;
    }

    @Override
    public RegisteredUser register(RegisteredUser ru){
        return ru;
    }
}
