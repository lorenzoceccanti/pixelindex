package it.unipi.largescale.pixelindex.exceptions;

public class DAOException extends Exception {
    public DAOException(Exception ex){
        super(ex);
    }
    public DAOException(String message){
        super(message);
    }
}
