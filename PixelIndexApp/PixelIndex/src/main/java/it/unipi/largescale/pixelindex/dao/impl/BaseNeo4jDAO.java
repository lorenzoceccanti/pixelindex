package it.unipi.largescale.pixelindex.dao.impl;

import it.unipi.largescale.pixelindex.utils.Utils;
import org.neo4j.driver.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class BaseNeo4jDAO {
    private static final String ENV_FILE = ".env";
    private static Driver neoDriver;

    public static Driver beginConnection(){
        String[] var = new String[2];
        ArrayList<String> params = new ArrayList<>();
        String envPayload = Utils.retrieveEnv();
        StringTokenizer tokens = new StringTokenizer(envPayload, "\n");
        while(tokens.hasMoreTokens())
        {
            var = tokens.nextToken().split("=", 2);
            params.add(var[1]);
        }
        String SERVER_ADDRESS = params.get(4).trim();
        int NEO_PORT = Integer.parseInt(params.get(5).trim());
        String NEO_USER = params.get(6).trim();
        String NEO_PASS = params.get(7).trim();

        // Create a custom SSL context.
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, null, null);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException("Failed to create SSL context", e);
        }

        // Disable hostname verification and certificate chain validation.
        SSLParameters sslParameters = sslContext.getDefaultSSLParameters();
        sslParameters.setEndpointIdentificationAlgorithm(null);

        String uri = String.format("bolt+s://%s:%d", SERVER_ADDRESS, NEO_PORT);
        neoDriver = GraphDatabase.driver(uri, AuthTokens.basic(NEO_USER, NEO_PASS),
                Config.builder().withTrustStrategy(Config.TrustStrategy.trustSystemCertificates()).build()
        );
        return neoDriver;
    }

}
