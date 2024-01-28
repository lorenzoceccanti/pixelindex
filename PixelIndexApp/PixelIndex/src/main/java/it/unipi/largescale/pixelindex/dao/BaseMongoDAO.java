package it.unipi.largescale.pixelindex.dao;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import it.unipi.largescale.pixelindex.utils.Utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public abstract class BaseMongoDAO {

    private static final String ENV_FILE = ".env";
    private static final String PEM_FILE = "mongodb.pem";
    private static MongoClient mongoclient;

    public static MongoClient beginConnection(){

        String[] var = new String[2];
        ArrayList<String> params = new ArrayList<>();
        String envPayload = Utils.retrieveEnv();
        StringTokenizer tokens = new StringTokenizer(envPayload, "\n");
        while(tokens.hasMoreTokens())
        {
            var = tokens.nextToken().split("=", 2);
            params.add(var[1]);
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
}
