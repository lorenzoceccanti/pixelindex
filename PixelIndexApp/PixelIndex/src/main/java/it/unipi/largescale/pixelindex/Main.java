package it.unipi.largescale.pixelindex;

import it.unipi.largescale.pixelindex.controller.ApplicationController;
import it.unipi.largescale.pixelindex.utils.Utils;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {

        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.OFF);
        mongoLogger.setUseParentHandlers(false);

        Utils.clearConsole();
        ApplicationController applicationController = new ApplicationController();
    }
}
