package it.unipi.largescale.pixelindex.service.impl;

import it.unipi.largescale.pixelindex.dao.RegisteredUserDAO;
import it.unipi.largescale.pixelindex.dao.impl.RegisteredUserMongoDAO;
import it.unipi.largescale.pixelindex.dto.AuthUserDTO;
import it.unipi.largescale.pixelindex.dto.UserRegistrationDTO;
import it.unipi.largescale.pixelindex.exceptions.UserNotFoundException;
import it.unipi.largescale.pixelindex.exceptions.WrongPasswordException;
import it.unipi.largescale.pixelindex.model.RegisteredUser;
import it.unipi.largescale.pixelindex.security.Crypto;
import it.unipi.largescale.pixelindex.service.RegisteredUserService;

public class RegUserServiceImpl implements RegisteredUserService {
    private RegisteredUserDAO registeredUserDAO;
    UserRegistrationDTO registrationDTO = new UserRegistrationDTO();

    public RegUserServiceImpl(){
        this.registeredUserDAO = new RegisteredUserMongoDAO();
    }

    @Override
    public AuthUserDTO makeLogin(String username, String password) throws WrongPasswordException, UserNotFoundException {
        RegisteredUser registeredUser = registeredUserDAO.makeLogin(username, password);
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
    public AuthUserDTO register(UserRegistrationDTO userRegistrationDTO, String preferredLanguage){
        RegisteredUser registeringUser = new RegisteredUser(preferredLanguage);
        registeringUser.setUsername(userRegistrationDTO.getUsername());
        registeringUser.setName(userRegistrationDTO.getName());
        registeringUser.setSurname(userRegistrationDTO.getSurname());
        registeringUser.setHashedPassword(Crypto.hashPassword(userRegistrationDTO.getPassword()));
        registeringUser.setDateOfBirth(userRegistrationDTO.getDateOfBirth());
        registeringUser.setEmail(userRegistrationDTO.getEmail());

        RegisteredUser registeredUser = registeredUserDAO.register(registeringUser);
        AuthUserDTO authUserDTO = new AuthUserDTO();
        authUserDTO.setName(registeredUser.getName());
        authUserDTO.setSurname(registeredUser.getSurname());
        authUserDTO.setDateOfBirth(registeredUser.getDateOfBirth());
        authUserDTO.setEmail(registeredUser.getEmail());
        return authUserDTO;
    }
}
