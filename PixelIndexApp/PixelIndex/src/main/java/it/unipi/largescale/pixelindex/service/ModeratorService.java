package it.unipi.largescale.pixelindex.service;

import it.unipi.largescale.pixelindex.controller.ConsistencyThread;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;

public interface ModeratorService {
    void banUser(String username, ConsistencyThread consistencyThread) throws ConnectionException;
    void synchronizeGames(ConsistencyThread consistencyThread) throws ConnectionException;
}
