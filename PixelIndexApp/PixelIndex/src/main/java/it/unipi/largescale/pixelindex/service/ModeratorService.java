package it.unipi.largescale.pixelindex.service;

import it.unipi.largescale.pixelindex.exceptions.ConnectionException;

public interface ModeratorService {
    void banUser(String username) throws ConnectionException;
    void deleteUserFromGraph(String username) throws ConnectionException;
}
