package DAO;

import Entities.User;
import lombok.extern.log4j.Log4j;

import java.sql.*;

@Log4j
public class UserDAOImplSQLite implements DAO<User>{
    private final String connectionAddress = "jdbc:mysql://localhost:3306/cloudstorage";
    private final String login = "root";
    private final String password = "123456";
    Connection connection;
    String getUserByLogin = "SELECT * FROM USERS WHERE email = ? AND password = ?";
    private final String pathToFile ="jdbc:sqlite:" + getClass().getResource("db.s3db");

    public UserDAOImplSQLite() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(pathToFile);

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public User getInstanceByName(User entity) {
        User userFromDB = new User();
        try (PreparedStatement statement = connection.prepareStatement("select * from users where login = ? and password = ?")){
            statement.setString(1, entity.getUser());
            statement.setString(2, entity.getPassword());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                userFromDB.setUser(resultSet.getString(2));
                userFromDB.setPassword(resultSet.getString(3));
                userFromDB.setRootDir(resultSet.getString(6));
                userFromDB.setEmailIsConfirmed(resultSet.getBoolean(4));
                userFromDB.calculateSessionCode();
                log.info("User was found: " + userFromDB.getUser());
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    return userFromDB;
    }

    @Override
    public void create(User entity) {

    }

    @Override
    public int update(User oldEntity, User newEntity) {
        return 0;
    }

    @Override
    public int delete(User entity) {
        return 0;
    }
}
