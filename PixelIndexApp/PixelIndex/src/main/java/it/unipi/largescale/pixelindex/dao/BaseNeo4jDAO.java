package it.unipi.largescale.pixelindex.dao;

import it.unipi.largescale.pixelindex.utils.Utils;
import org.neo4j.driver.*;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class BaseNeo4jDAO {
    private static Driver neoDriver;

    public static Driver beginConnection(){
        String[] var = new String[2];
        ArrayList<String> params = new ArrayList<>();
        String envPayload = Utils.retrieveEnv();
        StringTokenizer tokens = new StringTokenizer(envPayload, "\n");
        while(tokens.hasMoreTokens())
        {
            var = tokens.nextToken().split("=", 2);
            params.add(var.length == 1 ? "" : var[1]);
        }
        String SERVER_ADDRESS = params.get(4).trim();
        int NEO_PORT = Integer.parseInt(params.get(5).trim());
        String NEO_USER = params.get(6).trim();
        String NEO_PASS = params.get(7).trim();

        String uri = String.format("bolt://%s:%d", SERVER_ADDRESS, NEO_PORT);
        neoDriver = GraphDatabase.driver(uri, AuthTokens.basic(NEO_USER, NEO_PASS));
        return neoDriver;
    }
}
