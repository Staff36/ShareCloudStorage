package DAO;



import Entities.User;

import java.sql.*;

public class UserDAOImplMySQL implements UserDAO<User>{
    private final String connectionAddress = "jdbc:mysql://localhost:3306/cloudstorage";
    private final String login = "root";
    private final String password = "123456";
    Connection connection;
    public UserDAOImplMySQL() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(connectionAddress, login, password);


        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public User getInstanceByName(String userName, String password) throws SQLException {
        String sqlAsking = "SELECT * FROM USERS WHERE email = ?";
        PreparedStatement statement = connection.prepareStatement(sqlAsking);
        statement.setString(1, userName);
        ResultSet resultSet = statement.executeQuery();
        User userFromDB = new User();
        if (resultSet.next()){
            userFromDB.setUser(resultSet.getString(2));
            userFromDB.setPassword(resultSet.getString(3));
            userFromDB.calculateSessionCode();
        }
        return userFromDB;
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
