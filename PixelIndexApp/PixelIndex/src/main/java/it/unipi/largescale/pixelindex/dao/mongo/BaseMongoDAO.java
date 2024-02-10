package it.unipi.largescale.pixelindex.dao.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import it.unipi.largescale.pixelindex.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

public abstract class BaseMongoDAO {

    private static final String ENV_FILE = ".env";
    private static final String PEM_FILE = "mongodb.pem";
    private static MongoClient mongoclient;

    public static MongoClient beginConnectionWithoutReplica() {
        String[] var = new String[2];
        ArrayList<String> params = new ArrayList<>();
        String envPayload = Utils.retrieveEnv();
        StringTokenizer tokens = new StringTokenizer(envPayload, "\n");
        while (tokens.hasMoreTokens()) {
            var = tokens.nextToken().split("=", 2);
            params.add(var.length == 1 ? "" : var[1]);
        }
        String SERVER_ADDRESS = params.get(0).trim();
        int MONGO_PORT = Integer.parseInt(params.get(1).trim());
        String MONGO_USER = params.get(2).trim();
        String MONGO_PASS = params.get(3).trim();

        String connectionString = String.format("mongodb://%s:%s@%s:%d/", MONGO_USER, MONGO_PASS, SERVER_ADDRESS,
                MONGO_PORT);

        mongoclient = MongoClients.create(
                MongoClientSettings.builder()
                        .applyConnectionString(new ConnectionString(connectionString))
                        .build());
        return mongoclient;
    }

    public static MongoClient beginConnection(boolean primaryPref) {
        HashMap<String, String> params = new HashMap<>();
        String envPayload = Utils.retrieveEnv();
        StringTokenizer tokens = new StringTokenizer(envPayload, "\n");
        while (tokens.hasMoreTokens()) {
            String[] var = tokens.nextToken().split("=", 2);
            if (var.length == 2) {
                params.put(var[0].trim(), var[1].trim());
            }
        }

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
