package it.unipi.largescale.pixelindex.dao;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.exceptions.UserNotFoundException;
import it.unipi.largescale.pixelindex.exceptions.WrongPasswordException;
import it.unipi.largescale.pixelindex.model.RegisteredUser;

public interface RegisteredUserMongoDAO {
    RegisteredUser makeLogin(String username, String password) throws WrongPasswordException, UserNotFoundException, DAOException;
    RegisteredUser register(MongoClient mc, RegisteredUser u, ClientSession clientSession) throws DAOException;
}
