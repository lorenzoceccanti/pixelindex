package it.unipi.largescale.pixelindex.model;

import it.unipi.largescale.pixelindex.utils.Utils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class UnregisteredUser extends User{
    public UnregisteredUser(){
        /* Here we would put generic configuration valid for any kind of user*/
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.OFF);
        mongoLogger.setUseParentHandlers(false);
        super.setRole(role);
    }
}
