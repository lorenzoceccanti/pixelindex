package it.unipi.largescale.pixelindex.exceptions;

public class ConnectionException extends Exception{
    public ConnectionException(Exception ex){
        super(ex);
    }
    public ConnectionException(String message){
        super(message);
    }
}
