package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import request_result.*;
import spark.Response;

public class CreateGameService {
    public Object createGame(CreateGameRequest game, AuthTokenRequest authData, AuthDAO authDAO, GameDAO gameDAO, Response res) throws DataAccessException {
        try{
            if (game.gameName() == null || authData == null){
                res.status(400);
                return new FailureRepsonse("Error: bad request");
            }
            if(authDAO.getAuth(authData.authToken()) == null){
                res.status(401);
                return new FailureRepsonse("Error: unauthorized");
            }
            res.status(200);
            return new CreateGameResult(gameDAO.createGame(game.gameName()));
        } catch (DataAccessException err) {
            return new UserResult("Error", "error");
        }
    }
}
