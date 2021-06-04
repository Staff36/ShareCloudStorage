package DAO;

import Entities.Confirmation;
import MessageTypes.FileData;
import MessageTypes.FileImpl;
import MessageTypes.ShareFileRequest;
import lombok.extern.log4j.Log4j;

import java.io.File;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
    public static FileImpl[] getListOfSharableFiles(String rootDirName){
        log.info("RootDirName is " + rootDirName);
        connect();
        List<FileImpl> list = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("select real_path from share_files left join users u on share_files.shares_destinator = u.id where u.rootDir = ?"))
            {
            statement.setString(1, rootDirName);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                log.info(resultSet.getString(1));
                File file = Paths.get(resultSet.getString(1)).toFile();
                list.add(new FileImpl(file.getName(), file.list(),true, true, true));
            }
            resultSet.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            disconnect();
            return list.stream().toArray(FileImpl[]::new);
        }
    }
    public static void disconnect(){
        try {
            connection.close();
        } catch (SQLException throwables) {
            log.error("Exception when we try to close connection", throwables);
        }
    }

    public static File getSharableFileByName(String filename, File parentDir) {
        File file = null;
        connect();
        try(PreparedStatement statement = connection.prepareStatement("select real_path from share_files left join users u on share_files.shares_destinator = u.id where u.rootDir = ? and real_path like ?")){
            statement.setString(1, parentDir.getName());
            statement.setString(2, "%" + filename);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                file = new File(resultSet.getString(1));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            disconnect();
            return file;

        }

    }

    public static void shareFile(File file, File parentDir, String destinator) {
        connect();
        try(PreparedStatement statement = connection.prepareStatement("insert into share_files (owner, real_path, shares_destinator) values ((select id from users where rootDir = ?),?,(select id from users where login = ?))")){
            statement.setString(1, parentDir.getName());
            statement.setString(2, file.getAbsolutePath());
            statement.setString(3, destinator);
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            disconnect();
        }

    }

    public static void unshareFileByName(FileImpl file, File rootDir) {
        connect();

        try (PreparedStatement statement = connection.prepareStatement("delete from share_files where real_path like ? and shares_destinator = (select id from users where rootDir = ?)")){
            statement.setString(1, "%" + file.getFileName());
            statement.setString(2, rootDir.getAbsolutePath());
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            disconnect();

        }
    }
}
