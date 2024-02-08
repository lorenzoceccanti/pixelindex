package it.unipi.largescale.pixelindex.service;

import it.unipi.largescale.pixelindex.controller.ConsistencyThread;
import it.unipi.largescale.pixelindex.dto.AuthUserDTO;
import it.unipi.largescale.pixelindex.dto.UserRegistrationDTO;
import it.unipi.largescale.pixelindex.dto.UserSearchDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.exceptions.UserNotFoundException;
import it.unipi.largescale.pixelindex.exceptions.WrongPasswordException;

import java.util.ArrayList;

public interface RegisteredUserService {
    AuthUserDTO makeLogin(String username, String password) throws WrongPasswordException, UserNotFoundException, ConnectionException;
    AuthUserDTO register(UserRegistrationDTO userRegistrationDTO, String preferredLanguage) throws ConnectionException;
    ArrayList<UserSearchDTO> searchUser(String username, int page) throws ConnectionException;
    String followUser(String usernameSrc, String usernameDst, ConsistencyThread consistencyThread) throws ConnectionException;
    void reportUser(String usernameReporting, String usernameReported) throws ConnectionException;
}
