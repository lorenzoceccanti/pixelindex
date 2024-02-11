package it.unipi.largescale.pixelindex.dao.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import it.unipi.largescale.pixelindex.utils.Utils;

import java.util.*;

public abstract class BaseMongoDAO {

    private static final Map<Boolean, MongoClient> mongoClientMap = new HashMap<>();

    // Synchronized function to guarantee thread safety
    public static synchronized MongoClient beginConnection(boolean primaryPref) {
        if (mongoClientMap.containsKey(primaryPref) && mongoClientMap.get(primaryPref) != null) {
            return mongoClientMap.get(primaryPref);
        }

        MongoClient client = createNewMongoClient(primaryPref);
        mongoClientMap.put(primaryPref, client);
        return client;
    }

    private static MongoClient createNewMongoClient(boolean primaryPref) {
        HashMap<String, String> params = Utils.parseEnv();

        String envSuffix = params.get("ENVIRONMENT").equals("PRODUCTION") ? "_PROD" : "_TEST";
        String MONGO_USER = params.getOrDefault("MONGO_USER" + envSuffix, "");
        String MONGO_PASS = params.getOrDefault("MONGO_PASS" + envSuffix, "");

        List<String> replicas = new ArrayList<>();
        replicas.add(String.format("%s:%d", params.get("MONGO_PRIMARY"+envSuffix),
                Integer.parseInt(params.get("MONGO_PRIMARY_PORT"+envSuffix))));

        for (int i = 1; params.containsKey("MONGO_REPLICA_" + i + envSuffix); i++) {
            String address = params.get("MONGO_REPLICA_" + i + envSuffix);
            int port = Integer.parseInt(params.get("MONGO_REPLICA_" + i + "_PORT" + envSuffix));
            replicas.add(String.format("%s:%d", address, port));
        }

        String authPart = (!MONGO_USER.isEmpty() && !MONGO_PASS.isEmpty()) ? MONGO_USER + ":" + MONGO_PASS + "@" : "";
        String connectionString = String.format("mongodb://%s%s/?connectTimeoutMS=5000",
                authPart, String.join(",", replicas));

        MongoClientSettings mcs = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .readPreference(primaryPref ? ReadPreference.primaryPreferred() : ReadPreference.nearest())
                .writeConcern(WriteConcern.W2)
                .build();

        return MongoClients.create(mcs);
    }
}

