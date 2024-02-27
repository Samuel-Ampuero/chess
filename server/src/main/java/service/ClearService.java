package service;

import dataAccess.*;
import request_result.SucessResult;

public class ClearService {
    public Object clear(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) throws DataAccessException {
        userDAO.deleteAllUsers();
        authDAO.deleteAllAuths();
        gameDAO.deleteAllGames();
        return new SucessResult(null);
    }
}
