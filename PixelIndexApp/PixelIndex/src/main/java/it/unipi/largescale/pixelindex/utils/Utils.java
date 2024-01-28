package it.unipi.largescale.pixelindex.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class Utils {
    private static final String ENV_FILE = ".env";
    private static String envPayload;
    public static LocalDate convertDateToLocalDate(Date date) {
        Instant instant = date.toInstant();
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static String retrieveEnv(){
        String envPayload = null;
        try(BufferedReader reader = new BufferedReader(new FileReader(ENV_FILE))){
            char[] buf = new char[1024];
            reader.read(buf);
            envPayload = String.valueOf(buf);
        } catch(IOException ex){
            System.out.println("retrieveEnv(): Error in opening the .env file");
            System.exit(1); // The application is un-usable without databases
        }
        return envPayload;
    }

}
