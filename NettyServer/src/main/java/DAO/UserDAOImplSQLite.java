package DAO;

import Entities.User;
import lombok.extern.log4j.Log4j;

import java.sql.*;

@Log4j
public class UserDAOImplSQLite {
    private static Connection connection;
    private static final String pathToFile ="jdbc:sqlite:NettyServer/src/main/resources/DAO/db.s3db";

    private static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(pathToFile);

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
    public static void disconnect(){
        try {
            connection.close();
        } catch (SQLException throwables) {
            log.error("Exception when we try to close connection", throwables);
        }
    }

    public static User getInstanceByName(User entity) {
        connect();
        User userFromDB = new User();
        try (PreparedStatement statement = connection.prepareStatement("select * from users where login = ? and password = ?")){
            statement.setString(1, entity.getUser());
            statement.setString(2, entity.getPassword());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                userFromDB.setUser(resultSet.getString(2));
                userFromDB.setPassword(resultSet.getString(3));
                userFromDB.setRootDir(resultSet.getString(6));
                userFromDB.setEmailIsConfirmed(resultSet.getBoolean(4));
                userFromDB.calculateSessionCode();
                log.info("User was found: " + userFromDB.getUser());
            }
            resultSet.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            disconnect();
        }

    return userFromDB;
    }

    public static void create(User entity) {
        connect();
        try (PreparedStatement statement = connection.prepareStatement("insert into users (login, password, email, rootDir, email_confirmed) values (?,?,?,?,?)")){
            statement.setString(1, entity.getUser());
            statement.setString(2, entity.getPassword());
            statement.setString(3, entity.getEmail());
            statement.setString(4, entity.getUser());
            statement.setInt(5, 0);
            statement.executeUpdate();
        } catch (SQLException throwables) {
            log.error("Exception where we try insert value into confirm_email table. ", throwables);
        } finally {
            disconnect();
        }
    }

    public static String[] getAllINstances() {
        connect();
        String[] ss = new String[10];
        try (PreparedStatement statement = connection.prepareStatement("select * from users");){
            ResultSet resultSet = statement.executeQuery();
            int i = 0;
            while (resultSet.next()){
                ss[i] = resultSet.getString(2);
                i++;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        disconnect();
        return ss;
    }

    public int update(User oldEntity, User newEntity) {
        return 0;
    }

    public int delete(User entity) {
        return 0;
    }
}
