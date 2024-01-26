package it.unipi.largescale.pixelindex.dao;

import it.unipi.largescale.pixelindex.exceptions.WrongPasswordException;
import it.unipi.largescale.pixelindex.model.RegisteredUser;

public interface UserDAO {
    RegisteredUser register(RegisteredUser ru);
    RegisteredUser makeLogin(String username, String password) throws WrongPasswordException;
}
