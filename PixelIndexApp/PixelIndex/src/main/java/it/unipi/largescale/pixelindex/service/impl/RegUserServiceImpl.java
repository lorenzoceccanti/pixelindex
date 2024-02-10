package it.unipi.largescale.pixelindex.service.impl;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import it.unipi.largescale.pixelindex.controller.ConsistencyThread;
import it.unipi.largescale.pixelindex.dao.mongo.BaseMongoDAO;
import it.unipi.largescale.pixelindex.dao.mongo.RegisteredUserMongoDAO;
import it.unipi.largescale.pixelindex.dao.neo4j.RegisteredUserNeo4jDAO;
import it.unipi.largescale.pixelindex.dto.AuthUserDTO;
import it.unipi.largescale.pixelindex.dto.UserRegistrationDTO;
import it.unipi.largescale.pixelindex.dto.UserSearchDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.exceptions.UserNotFoundException;
import it.unipi.largescale.pixelindex.exceptions.WrongPasswordException;
import it.unipi.largescale.pixelindex.model.RegisteredUser;
import it.unipi.largescale.pixelindex.security.Crypto;
import it.unipi.largescale.pixelindex.service.RegisteredUserService;

import java.util.ArrayList;
import java.util.Map;

public class RegUserServiceImpl implements RegisteredUserService {
    private RegisteredUserMongoDAO registeredUserMongo;
    private RegisteredUserNeo4jDAO registeredUserNeo;
    UserRegistrationDTO registrationDTO = new UserRegistrationDTO();


    public RegUserServiceImpl() {
        this.registeredUserMongo = new RegisteredUserMongoDAO();
        this.registeredUserNeo = new RegisteredUserNeo4jDAO();
    }

    @Override
    public AuthUserDTO makeLogin(String username, String password) throws WrongPasswordException, UserNotFoundException, ConnectionException {
        RegisteredUser registeredUser = null;
        try {
            registeredUser = registeredUserMongo.makeLogin(username, password);
        } catch (DAOException ex) {
            throw new ConnectionException(ex);
        }
        AuthUserDTO authUserDTO = new AuthUserDTO();
        authUserDTO.setId(registeredUser.getId());
        authUserDTO.setUsername(registeredUser.getUsername());
        authUserDTO.setName(registeredUser.getName());
        authUserDTO.setSurname(registeredUser.getSurname());
        authUserDTO.setEmail(registeredUser.getEmail());
        authUserDTO.setDateOfBirth(registeredUser.getDateOfBirth());
        authUserDTO.setRole(registeredUser.getRole());

        return authUserDTO;
    }

    @Override
    public AuthUserDTO register(UserRegistrationDTO userRegistrationDTO, String preferredLanguage) throws ConnectionException {
        RegisteredUser registeringUser = new RegisteredUser(preferredLanguage);
        registeringUser.setUsername(userRegistrationDTO.getUsername());
        registeringUser.setName(userRegistrationDTO.getName());
        registeringUser.setSurname(userRegistrationDTO.getSurname());
        registeringUser.setHashedPassword(Crypto.hashPassword(userRegistrationDTO.getPassword()));
        registeringUser.setDateOfBirth(userRegistrationDTO.getDateOfBirth());
        registeringUser.setEmail(userRegistrationDTO.getEmail());


        RegisteredUser registeredUser;

        // Starting a MongoDAO transaction
        MongoDatabase db;
        try (MongoClient mongoClient = BaseMongoDAO.beginConnection(true)) {
            try (ClientSession clientSession = mongoClient.startSession()) {
                clientSession.startTransaction();
                try {
                    // User registration, collection users MongoDB
                    registeredUser = registeredUserMongo.register(mongoClient, registeringUser, clientSession);
                    // Adding node to Neo4J
                    registeredUserNeo.register(userRegistrationDTO.getUsername());
                    clientSession.commitTransaction();
                } catch (DAOException ex) {
                    clientSession.abortTransaction();
                    throw new ConnectionException(ex);
                }
            }
        }

        AuthUserDTO authUserDTO = new AuthUserDTO();
        authUserDTO.setName(registeredUser.getName());
        authUserDTO.setSurname(registeredUser.getSurname());
        authUserDTO.setDateOfBirth(registeredUser.getDateOfBirth());
        authUserDTO.setEmail(registeredUser.getEmail());

        return authUserDTO;
    }

    @Override
    public ArrayList<UserSearchDTO> searchUser(String username, int page) throws ConnectionException {
        ArrayList<UserSearchDTO> authUserDTOs;
        try {
             authUserDTOs = registeredUserMongo.searchUser(username, page);
        } catch (DAOException ex) {
            throw new ConnectionException(ex);
        }
        return authUserDTOs;
    }

    @Override
    public String followUser(String usernameSrc, String usernameDst, ConsistencyThread consistencyThread) throws ConnectionException {
        try {
            String outcome = registeredUserNeo.followUser(usernameSrc, usernameDst);
            consistencyThread.addTask(() -> {
                try{
                    Map<String, Integer> folCount = registeredUserNeo.getFollowsCount(usernameSrc, usernameDst);
                    registeredUserMongo.updateFollowers(usernameSrc, usernameDst, folCount.get("followingSrc"), folCount.get("followerDst"));
                }catch(DAOException e){
                    e.printStackTrace();
                }
            });
            return outcome;
        } catch (DAOException ex) {
            throw new ConnectionException(ex);
        }
    }

    @Override
    public void reportUser(String usernameReporting, String usernameReported) throws ConnectionException {
        try {
            registeredUserMongo.reportUser(usernameReporting, usernameReported);
        } catch (DAOException ex) {
            throw new ConnectionException(ex);
        }
    }
}
