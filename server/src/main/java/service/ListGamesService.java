package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.GameData;
import request_result.*;

import java.util.ArrayList;
import java.util.Collection;

public class ListGamesService {
    public Object listGames(AuthTokenRequest authData, AuthDAO authDAO, GameDAO gameDAO) throws DataAccessException {
        try{
            if(authDAO.getAuth(authData.authToken()) == null){
                return new FailureRepsonse("Error: unauthorized");
            }
            var games = gameDAO.listGames();
            Collection<GameResult> result = new ArrayList<>();
            for (GameData elem : games){
                result.add(new GameResult(elem.gameID(), elem.whiteUsername(), elem.blackUsername(), elem.gameName()));
            }
            return new GameListResult(result);
        } catch (DataAccessException err) {
            return new UserResult("Error", "error");
        }
    }
}
