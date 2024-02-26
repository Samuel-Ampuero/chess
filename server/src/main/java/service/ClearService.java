package service;

import dataAccess.*;

public class ClearService {
    public void clear(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) throws DataAccessException {
        userDAO.deleteAllUsers();
        authDAO.deleteAllAuths();
        gameDAO.deleteAllGames();
    }
}
