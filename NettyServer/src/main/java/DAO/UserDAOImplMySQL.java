package DAO;



import Entities.User;

import java.sql.*;

public class UserDAOImplMySQL implements UserDAO<User>{
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
            e.printStackTrace();
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
        }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            return userFromDB;
        }
    }

    @Override
    public void create(User user) {

    }

    @Override
    public void updateUser(User oldUser, User newUser) {

    }

    @Override
    public boolean deleteUser(User user) {
        return false;
    }
}
