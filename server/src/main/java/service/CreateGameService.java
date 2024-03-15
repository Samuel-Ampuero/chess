package service;

import dataAccess.AuthDAO;
import exception.DataAccessException;
import dataAccess.GameDAO;
import request_result.*;

public class CreateGameService {
    public Object createGame(CreateGameRequest game, AuthTokenRequest authData, AuthDAO authDAO, GameDAO gameDAO) throws DataAccessException {
        try{
            if (game.gameName() == null || authData == null){
                return new FailureRepsonse("Error: bad request");
            }
            if(authDAO.getAuth(authData.authToken()) == null){
                return new FailureRepsonse("Error: unauthorized");
            }
            return new CreateGameResult(gameDAO.createGame(game.gameName()));
        } catch (DataAccessException err) {
            return new UserResult("Error", "error");
        }
    }
}
