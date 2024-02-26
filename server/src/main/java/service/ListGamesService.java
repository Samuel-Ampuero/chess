package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import request_result.AuthTokenRequest;
import request_result.FailureRepsonse;
import request_result.GameListResult;
import request_result.UserResult;
import spark.Response;

public class ListGamesService {
    public Object listGames(AuthTokenRequest authData, AuthDAO authDAO, GameDAO gameDAO, Response res) throws DataAccessException {
        try{
            if(authDAO.getAuth(authData.authToken()) == null){
                res.status(401);
                return new FailureRepsonse("Error: unauthorized");
            }
            return new GameListResult(gameDAO.listGames());
        } catch (DataAccessException err) {
            return new UserResult("Error", "error");
        }
    }
}
