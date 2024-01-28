package it.unipi.largescale.pixelindex.dao;

import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.exceptions.UserNotFoundException;
import it.unipi.largescale.pixelindex.exceptions.WrongPasswordException;
import it.unipi.largescale.pixelindex.model.RegisteredUser;

public interface RegisteredUserMongoDAO {
    RegisteredUser makeLogin(String username, String password) throws WrongPasswordException, UserNotFoundException, DAOException;
    RegisteredUser register(RegisteredUser u) throws DAOException;

}
