package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import request_result.AuthTokenRequest;
import request_result.FailureRepsonse;
import request_result.GameResult;
import request_result.UserResult;
import spark.Response;

public class ListGamesService {
    public Object listGames(AuthTokenRequest logoutData, AuthDAO authDAO, GameDAO gameDAO, Response res) throws DataAccessException {
        try{
            if(authDAO.getAuth(logoutData.authToken()) == null){
                res.status(401);
                return new FailureRepsonse("Error: unauthorized");
            }
            return new GameResult(gameDAO.listGames());
        } catch (DataAccessException err) {
            return new UserResult("Error", "error");
        }
    }
}
