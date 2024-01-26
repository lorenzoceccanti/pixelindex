package it.unipi.largescale.pixelindex.service;

import it.unipi.largescale.pixelindex.dto.AuthUserDTO;

public interface RegisteredUserService {
    AuthUserDTO authenticate(String username, String password);
    AuthUserDTO register(RegisteredUserDTO registeredUserDTO);
}
