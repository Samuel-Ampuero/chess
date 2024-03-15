package service;

import dataAccess.AuthDAO;
import exception.DataAccessException;
import dataAccess.UserDAO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import request_result.FailureRepsonse;
import request_result.LoginRequest;
import request_result.UserResult;

public class LoginService {
    public Object login(UserDAO userDAO, LoginRequest loginData, AuthDAO authDAO) throws DataAccessException {
        try{
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if(userDAO.listUsers().isEmpty() || userDAO.getUser(loginData.username()) == null || !encoder.matches(loginData.password(), userDAO.getUser(loginData.username()).password())){
                return new FailureRepsonse("Error: unauthorized");
            }
            String authToken = authDAO.createAuth(loginData.username());
            return new UserResult(loginData.username(), authToken);
        } catch (DataAccessException err) {
            return new UserResult("Error", "error");
        }
    }
}
