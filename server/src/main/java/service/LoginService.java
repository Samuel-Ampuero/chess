package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import request_result.FailureRepsonse;
import request_result.LoginRequest;
import request_result.UserResult;
import spark.Response;

import java.util.Objects;

public class LoginService {
    public Object login(UserDAO userDAO, LoginRequest loginData, AuthDAO authDAO, Response res) throws DataAccessException {
        try{
            if(!Objects.equals(userDAO.getUser(loginData.username()).password(), loginData.password())){
                res.status(401);
                return new FailureRepsonse("Error: unauthorized");
            }
            String authToken = authDAO.createAuth(loginData.username());
            res.status(200);
            return new UserResult(loginData.username(), authToken);
        } catch (DataAccessException err) {
            return new UserResult("Error", "error");
        }
    }
}
