package service;

import dataAccess.*;
import model.UserData;
import request_result.FailureRepsonse;
import request_result.UserResult;

public class RegistrationService {
    public Object register(UserDAO userDAO, UserData userData, AuthDAO authDAO) throws DataAccessException {


        try{
            if (userData.username() == null || userData.password() == null || userData.email() == null){
                return new FailureRepsonse("Error: bad request");
            }

            if(userDAO.getUser(userData.username()) != null){
                return new FailureRepsonse("Error: already taken");
            }
            userDAO.createUser(userData.username(), userData.password(), userData.email());
            String authToken = authDAO.createAuth(userData.username());
            return new UserResult(userData.username(), authToken);
        } catch (DataAccessException err) {
            return new UserResult("Error", "error");
        }
    }
}
