package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import request_result.FailureRepsonse;
import request_result.LoginRequest;
import request_result.UserResult;

import java.util.Objects;

public class LoginService {
    public Object login(UserDAO userDAO, LoginRequest loginData, AuthDAO authDAO) throws DataAccessException {
        try{
            if(userDAO.listUsers().isEmpty() || userDAO.getUser(loginData.username()) == null || !Objects.equals(userDAO.getUser(loginData.username()).password(), loginData.password())){
                return new FailureRepsonse("Error: unauthorized");
            }
            String authToken = authDAO.createAuth(loginData.username());
            return new UserResult(loginData.username(), authToken);
        } catch (DataAccessException err) {
            return new UserResult("Error", "error");
        }
    }
}
