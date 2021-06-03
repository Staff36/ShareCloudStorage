package DAO;

import Entities.Confirmation;
import MessageTypes.FileImpl;
import lombok.extern.log4j.Log4j;

import java.sql.*;

@Log4j
public class SharedFilesImplSQLite {
    private static Connection connection;
    private static final String pathToFile ="jdbc:sqlite:NettyServer/src/main/resources/DAO/db.s3db";

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(pathToFile);

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
    public FileImpl[] getListOfSharableFiles(){
        connect();
        try {
            PreparedStatement statement = connection.prepareStatement("select real_path, shares_destinator, u.rootDir from share_files left join users u on share_files.owner = u.id where u.login = 'staff'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
    public static void disconnect(){
        try {
            connection.close();
        } catch (SQLException throwables) {
            log.error("Exception when we try to close connection", throwables);
        }
    }

}
