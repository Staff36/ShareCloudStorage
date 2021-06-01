package DAO;



import Entities.User;
import lombok.extern.log4j.Log4j;

import java.sql.*;
@Log4j
public class UserDAOImplMySQL implements DAO<User> {
    private final String connectionAddress = "jdbc:mysql://localhost:3306/cloudstorage";
    private final String login = "root";
    private final String password = "123456";
    Connection connection;
    String getUserByLogin = "SELECT * FROM USERS WHERE email = ? AND password = ?";

    public UserDAOImplMySQL() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(connectionAddress, login, password);
        } catch (ClassNotFoundException | SQLException e) {
            log.error("Exception when we try connect to DB ", e);
        }
    }

    @Override
    public User getInstanceByName(User user){
        User userFromDB = new User();
        try (PreparedStatement statement = connection.prepareStatement(getUserByLogin)){
        statement.setString(1, user.getUser());
        statement.setString(2, user.getPassword());
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()){
            userFromDB.setUser(resultSet.getString(2));
            userFromDB.setPassword(resultSet.getString(3));
            userFromDB.calculateSessionCode();
            userFromDB.setRootDir(resultSet.getString(4));
            log.info("User was found: " + user.getUser());
        }

        } catch (SQLException throwables) {
            log.error("Exception when we try to get User: ", throwables);
        } finally {
            return userFromDB;
        }
    }

    @Override
    public void create(User user) {
        try(PreparedStatement statement = connection.prepareStatement("INSERT INTO USERS (email, password, login) VALUES (?, ?, ?)")){
            statement.setString(1, user.getUser());
            statement.setString(2, user.getPassword());
            statement.executeUpdate();
        } catch (SQLException throwables) {
           log.error("Exception when we try to create new User: ", throwables);
        }
    }

    @Override
    public int update(User oldUser, User newUser) {
        return 0;
    }

    @Override
    public int delete(User user) {
        return 0;
    }
}
