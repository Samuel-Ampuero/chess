package service;

import dataAccess.*;
import model.UserData;
import request_result.FailureRepsonse;
import request_result.UserResult;
import spark.Response;

public class RegistrationService {
    public Object register(UserDAO userDAO, UserData userData, AuthDAO authDAO, Response res) throws DataAccessException {
        try{
            if (userData.username() == null || userData.password() == null || userData.email() == null){
                res.status(400);
                return new FailureRepsonse("Error: bad request");
            }

            if(userDAO.getUser(userData.username()) != null){
                res.status(403);
                return new FailureRepsonse("Error: already taken");
            }
            userDAO.createUser(userData.username(), userData.password(), userData.email());
            String authToken = authDAO.createAuth(userData.username());
            res.status(200);
            return new UserResult(userData.username(), authToken);
        } catch (DataAccessException err) {
            return new UserResult("Error", "error");
        }
    }
}
