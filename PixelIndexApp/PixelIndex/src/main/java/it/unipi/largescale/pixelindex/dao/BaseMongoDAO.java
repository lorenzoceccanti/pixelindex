package it.unipi.largescale.pixelindex.dao;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public abstract class BaseMongoDAO {

    private static final String ENV_FILE = ".env";
    private static String envPayload;
    private static MongoClient mongoclient;


    private static void retrieveEnv(){
        try(BufferedReader reader = new BufferedReader(new FileReader(ENV_FILE))){
            char[] buf = new char[1024];
            reader.read(buf);
            envPayload = String.valueOf(buf);
            // System.out.println(envPayload);
        } catch(IOException ex){
            System.out.println("retrieveEnv(): Error in opening the .env file");
        }
    }

    public static MongoClient beginConnection(){
        String[] var = new String[2];
        retrieveEnv();
        StringTokenizer tokens = new StringTokenizer(envPayload, "\n");
        var = tokens.nextToken().split("=", 2);
        String SERVER_ADDRESS = var[1];
        var = tokens.nextToken().split("=", 2);
        int MONGO_PORT = Integer.parseInt(var[1].trim());
        var = tokens.nextToken().split("=", 2);
        String MONGO_USER = var[1];
        var = tokens.nextToken().split("=", 2);
        String MONGO_PASS = var[1];

        // Does not connect due to TLS
        String connectionString = String.format("mongodb://%s:%s@%s:%d/?tls=true&tlsCertificateKeyFile=", MONGO_USER,
                MONGO_PASS, SERVER_ADDRESS, MONGO_PORT);
        mongoclient = MongoClients.create(
                MongoClientSettings.builder()
                        .applyConnectionString(new ConnectionString(connectionString))
                        .build());
        return mongoclient;
    }
}
