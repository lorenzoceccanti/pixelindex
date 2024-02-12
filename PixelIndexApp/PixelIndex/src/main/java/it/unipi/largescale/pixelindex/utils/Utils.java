package it.unipi.largescale.pixelindex.utils;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyListener;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Period;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Utils {
    private static final String ENV_FILE = ".env";
    private static final Map<String, Integer> PEGI_RATINGS = new HashMap<>();

    static {
        PEGI_RATINGS.put("Three", 3);
        PEGI_RATINGS.put("Seven", 7);
        PEGI_RATINGS.put("Twelve", 12);
        PEGI_RATINGS.put("Sixteen", 16);
        PEGI_RATINGS.put("Eighteen", 18);
    }

    public static void clearConsole() {
        // System.out.println(ansi().eraseScreen());

        System.out.print("\033[H\033[2J");
        System.out.flush();
        String welcomeMessage =
                """
                        ██████╗ ██╗██╗  ██╗███████╗██╗     ██╗███╗   ██╗██████╗ ███████╗██╗  ██╗
                        ██╔══██╗██║╚██╗██╔╝██╔════╝██║     ██║████╗  ██║██╔══██╗██╔════╝╚██╗██╔╝
                        ██████╔╝██║ ╚███╔╝ █████╗  ██║     ██║██╔██╗ ██║██║  ██║█████╗   ╚███╔╝\s
                        ██╔═══╝ ██║ ██╔██╗ ██╔══╝  ██║     ██║██║╚██╗██║██║  ██║██╔══╝   ██╔██╗\s
                        ██║     ██║██╔╝ ██╗███████╗███████╗██║██║ ╚████║██████╔╝███████╗██╔╝ ██╗
                        ╚═╝     ╚═╝╚═╝  ╚═╝╚══════╝╚══════╝╚═╝╚═╝  ╚═══╝╚═════╝ ╚══════╝╚═╝  ╚═╝
                """;
        System.out.println(welcomeMessage);

    }
    public static LocalDate convertDateToLocalDate(Date date) {
        Instant instant = date.toInstant();
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDateTime convertDateToLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDate convertStringToLocalDate(String dateExpr){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(dateExpr,formatter);
    }

    public static String retrieveEnv() {
        String envPayload = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(ENV_FILE))) {
            char[] buf = new char[1024];
            reader.read(buf);
            envPayload = String.valueOf(buf);
        } catch (IOException ex) {
            System.out.println("retrieveEnv(): Error in opening the .env file");
            System.exit(1); // The application is un-usable without databases
        }
        return envPayload;
    }

    public static HashMap<String, String> parseEnv() {
        HashMap<String, String> params = new HashMap<>();
        String envPayload = Utils.retrieveEnv();
        StringTokenizer tokens = new StringTokenizer(envPayload, "\n");
        while (tokens.hasMoreTokens()) {
            String[] var = tokens.nextToken().split("=", 2);
            if (var.length == 2) {
                params.put(var[0].trim(), var[1].trim());
            }
        }
        return params;
    }

    public static Map<String, String> parseSearchString(String search) {
        Map<String, String> params = new HashMap<>();

        String normalizedSearch = search.replaceAll("\\s+", " ");
        String[] parts = normalizedSearch.split("-");

        for (String part : parts) {
            if (part.startsWith("c ")) {
                params.put("company", part.substring(2).trim());
            } else if (part.startsWith("p ")) {
                params.put("platform", part.substring(2).trim());
            } else if (part.startsWith("y ")) {
                params.put("year", part.substring(2).trim());
            } else {
                params.put("name", part.trim());
            }
        }

        return params;
    }

    public static boolean isAgeRestricted(LocalDate dateOfBirth, String pegiRating) {
        Integer thresholdAge = PEGI_RATINGS.get(pegiRating);
        int age = Period.between(dateOfBirth, LocalDate.now()).getYears();
        return age < thresholdAge;
    }

    public static void startTrackingKeyboard(Object o){
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);
        try {
            /* Register jNativeHook */
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener((NativeKeyListener) o);
        } catch (NativeHookException ex) {
            ex.printStackTrace();
        }
    }

    public static void stopTrackingKeyboard(Object o){
        try{
            GlobalScreen.removeNativeKeyListener((NativeKeyListener) o);
            GlobalScreen.unregisterNativeHook();
        }catch(NativeHookException e)
        {
            e.printStackTrace();
        }
    }
}
