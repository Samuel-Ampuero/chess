package service;

import dataAccess.AuthDAO;
import exception.DataAccessException;
import request_result.FailureRepsonse;
import request_result.AuthTokenRequest;
import request_result.SucessResult;
import request_result.UserResult;

public class LogoutService {
    public Object logout(AuthTokenRequest logoutData, AuthDAO authDAO) throws DataAccessException {
        try{
            if(authDAO.getAuth(logoutData.authToken()) == null){
                return new FailureRepsonse("Error: unauthorized");
            }
            authDAO.deleteAuth(logoutData.authToken());
            return new SucessResult(null);
        } catch (DataAccessException err) {
            return new UserResult("Error", "error");
        }
    }
}
