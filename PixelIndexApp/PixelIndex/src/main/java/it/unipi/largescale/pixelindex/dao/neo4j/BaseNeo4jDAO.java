package it.unipi.largescale.pixelindex.dao.neo4j;

import it.unipi.largescale.pixelindex.utils.Utils;
import org.neo4j.driver.*;

import java.util.HashMap;

public class BaseNeo4jDAO {
    public static Driver beginConnection() {
        HashMap<String, String> params = Utils.parseEnv();

        String envSuffix = params.get("ENVIRONMENT").equals("PRODUCTION") ? "_PROD" : "_TEST";
        String NEO_ADDRESS = params.get("NEO_ADDRESS" + envSuffix);
        int NEO_PORT = Integer.parseInt(params.get("NEO_PORT" + envSuffix));
        String NEO_USER = params.get("NEO_USER" + envSuffix);
        String NEO_PASS = params.get("NEO_PASS" + envSuffix);
        String uri = String.format("bolt://%s:%d", NEO_ADDRESS, NEO_PORT);
        return GraphDatabase.driver(uri, AuthTokens.basic(NEO_USER, NEO_PASS));
    }
}
