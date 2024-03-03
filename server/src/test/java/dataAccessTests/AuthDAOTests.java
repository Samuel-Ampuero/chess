package dataAccessTests;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.SQLAuthDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AuthDAOTests {
    private AuthDAO sql;
    @BeforeEach
    public void createDatabases(){
        try {
            sql = new SQLAuthDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void createPositiveTest(){
        try {
            var authToken = sql.createAuth("username");
            Assertions.assertNotNull(sql.getAuth(authToken));

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void createNegativeTest(){
        Assertions.assertThrows(DataAccessException.class, () -> {
            sql.createAuth(null);
        });

    }

    @Test
    public void getPositiveTest(){
        try {
            var authToken = sql.createAuth("username");
            var test = sql.getAuth(authToken);
            Assertions.assertNotNull(test);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void getNegativeTest(){
        try {
            sql.createAuth("username");
            sql.createAuth("username1");
            var test = sql.getAuth("not an AuthToken");
            Assertions.assertNull(test);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void deletePositiveTest(){
        try {
            var authToken = sql.createAuth("username");
            sql.createAuth("username1");
            sql.deleteAuth(authToken);
            Assertions.assertNull(sql.getAuth(authToken));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void deleteNegativeTest(){
        Assertions.assertThrows(DataAccessException.class, () ->
                sql.deleteAuth(null));
    }

    @AfterEach
    @Test
    public void clearDatabase(){
        try {
            sql.createAuth("test1");
            sql.createAuth("test2");
            sql.deleteAllAuths();
            Assertions.assertNull(sql.getAuth("test1"));
            Assertions.assertNull(sql.getAuth("test2"));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
