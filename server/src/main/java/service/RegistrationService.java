package service;

import dataAccess.*;
import model.UserData;
import request_result.RegisterResult;

public class RegistrationService {
    public RegisterResult register(UserDAO userDAO, UserData userData, AuthDAO authDAO){
        try{
            userDAO.createUser(userData.username(), userData.password(), userData.email());
            String authToken = authDAO.createAuth(userData.username());
            return new RegisterResult(userData.username(), authToken);
        } catch (DataAccessException err) {
            return new RegisterResult("error", "error");
        }
    }
}
