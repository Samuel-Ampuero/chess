package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import request_result.FailureRepsonse;
import request_result.LoginRequest;
import request_result.LogoutRequest;
import request_result.UserResult;

public class LogoutService {
    public Object logout(LogoutRequest logoutData, AuthDAO authDAO) throws DataAccessException {
        try{
            if(authDAO.getAuth(logoutData.authToken()) == null){
                return new FailureRepsonse("Error: unauthorized");
            }
            authDAO.deleteAuth(logoutData.authToken());
            return "";
        } catch (DataAccessException err) {
            return new UserResult("Error", "error");
        }
    }
}
