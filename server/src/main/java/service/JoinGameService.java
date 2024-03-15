package service;

import dataAccess.AuthDAO;
import exception.DataAccessException;
import dataAccess.GameDAO;
import model.GameData;
import request_result.*;
import java.util.Objects;

public class JoinGameService {
    public Object joinGame(JoinGameRequest request, AuthTokenRequest authData, AuthDAO authDAO, GameDAO gameDAO) throws DataAccessException {
        try{
            if (request.gameID() == 0 ||authData == null){
                return new FailureRepsonse("Error: bad request");
            }
            if(authDAO.getAuth(authData.authToken()) == null){
                return new FailureRepsonse("Error: unauthorized");
            }
            if((Objects.equals(request.playerColor(), "WHITE") && gameDAO.getGame(request.gameID()).whiteUsername() != null)
                    ||(Objects.equals(request.playerColor(), "BLACK") && gameDAO.getGame(request.gameID()).blackUsername() != null)){
                return new FailureRepsonse("Error: already taken");
            }

            if(request.playerColor() == null){
                return new SucessResult(null);
            }

            GameData originalGame = gameDAO.getGame(request.gameID());
            if (request.playerColor().equals("WHITE")){
                gameDAO.updateGame(originalGame.gameID(), authDAO.getAuth(authData.authToken()).username(), originalGame.blackUsername(), originalGame.gameName(), originalGame.game());
            } else if (request.playerColor().equals("BLACK")){
                gameDAO.updateGame(originalGame.gameID(), originalGame.whiteUsername(), authDAO.getAuth(authData.authToken()).username(), originalGame.gameName(), originalGame.game());
            }
            return new SucessResult(null);
        } catch (DataAccessException err) {
            return new UserResult("Error", "error");
        }
    }
}
