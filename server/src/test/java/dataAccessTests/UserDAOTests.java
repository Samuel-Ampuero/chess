package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.SQLUserDAO;
import dataAccess.UserDAO;
import org.junit.jupiter.api.*;

public class UserDAOTests {
    private UserDAO sql;
    @BeforeEach
    public void createDatabases(){
        try {
            sql = new SQLUserDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void createPositiveTest(){
        try {
            sql.createUser("username", "password", "email");
            Assertions.assertNotNull(sql.listUsers());

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void createNegativeTest(){
        try {
            sql.createUser("username", "password", "email");
            Assertions.assertThrows(DataAccessException.class, () ->
                    sql.createUser("username", "password", "email"));

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void listPositiveTest(){
        try {
            sql.createUser("username", "password", "email");
            sql.createUser("username1", "password", "email");
            sql.createUser("username2", "password", "email");
            sql.createUser("username3", "password", "email");
            sql.createUser("username4", "password", "email");
            sql.createUser("username5", "password", "email");

            Assertions.assertEquals(sql.listUsers().size(), 6);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void listNegativeTest(){
        try {
            sql.createUser("username", "password", "email");
            sql.createUser("username1", "password", "email");
            sql.createUser("username2", "password", "email");
            sql.createUser("username3", "password", "email");
            sql.createUser("username4", "password", "email");
            sql.createUser("username", "password", "email");
        } catch (DataAccessException e) {
            try {
                Assertions.assertEquals(sql.listUsers().size(), 5);
            } catch (DataAccessException err) {
                throw new RuntimeException(err);
            }
        }
    }

    @Test
    public void getPositiveTest(){
        try {
            sql.createUser("username", "password", "email");
            sql.createUser("username1", "password", "email");
            var test = sql.getUser("username1");
            Assertions.assertNotNull(test);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void getNegativeTest(){
        try {
            sql.createUser("username", "password", "email");
            sql.createUser("username1", "password", "email");
            var test = sql.getUser("username2");
            Assertions.assertNull(test);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

    }

    @AfterEach
    @Test
    public void clearDatabase(){
        try {
            sql.createUser("test1", "test1", "test1");
            sql.createUser("test2", "test2", "test2");
            sql.deleteAllUsers();
            Assertions.assertEquals(sql.listUsers().size(), 0);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
