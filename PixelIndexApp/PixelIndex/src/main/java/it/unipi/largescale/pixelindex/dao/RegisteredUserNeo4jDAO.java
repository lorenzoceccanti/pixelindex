package it.unipi.largescale.pixelindex.dao;

import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.model.RegisteredUser;

public interface RegisteredUserNeo4jDAO {
    void register(String username) throws DAOException;
    void follow(String usernameSrc, String usernameDst) throws DAOException;
}
