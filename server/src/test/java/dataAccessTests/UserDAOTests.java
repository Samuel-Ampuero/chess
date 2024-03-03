package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryUserDAO;
import dataAccess.SQLUserDAO;
import dataAccess.UserDAO;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.junit.jupiter.api.Test;
public class UserDAOTests {

    @Test
    public void createPositiveTest(){
        try {
            UserDAO userSQL = new SQLUserDAO();
            UserDAO userMemory = new MemoryUserDAO();
            userSQL.createUser("username", "password", "email");
            userMemory.createUser("username", "password", "email");

            Assertions.assertNotNull(userSQL.listUsers());
            Assertions.assertNotNull(userMemory.listUsers());

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

    }
}
