package DAO;

import Entities.Confirmation;
import lombok.extern.log4j.Log4j;

import java.sql.*;
@Log4j
public class ConfirmEmailSQLite {
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

    public static void disconnect(){
        try {
            connection.close();
        } catch (SQLException throwables) {
            log.error("Exception when we try to close connection", throwables);
        }
    }
    public static Confirmation getInstanceByName(Confirmation entity) {
        connect();
        Confirmation confirmation = new Confirmation();
        try (PreparedStatement statement = connection.prepareStatement("select users.email, code from confirm_email left join users on confirm_email.user = users.id where users.email = ? and code = ?")){
            statement.setString(1, entity.getEmail());
            statement.setInt(2, entity.getCode());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                confirmation.setEmail(resultSet.getString(1));
                confirmation.setCode(resultSet.getInt(2));
                log.info("Code was found for: " + confirmation.getEmail());
            }
            resultSet.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            disconnect();
            return confirmation;
        }
    }

    public static void create(Confirmation entity) {
        connect();
        try (PreparedStatement statement = connection.prepareStatement("insert into confirm_email (user, code) values ((select id from users where email = ?),?)")){
            statement.setString(1, entity.getEmail());
            statement.setInt(2, entity.getCode());
            statement.executeUpdate();
        } catch (SQLException throwables) {
            log.error("Exception where we try insert value into confirm_email table. ", throwables);
        } finally {
            disconnect();
        }
    }

    public static int update(Confirmation oldEntity, Confirmation newEntity) {
        connect();
        int count = 0;
        try (PreparedStatement statement = connection.prepareStatement("update confirm_email set code = ? where user = (select id from users where email = ?)")){
            statement.setInt(1, newEntity.getCode());
            statement.setString(2, newEntity.getEmail());
            count = statement.executeUpdate();
        } catch (SQLException throwables) {
            log.error(throwables);
        }finally {
            disconnect();
            return count;
        }
    }

    public static int delete(Confirmation entity) {
       connect();
       int rows = 0;
       try(PreparedStatement statement = connection.prepareStatement("delete from confirm_email where user = (select id from users where email = ?)")){
           statement.setString(1, entity.getEmail());
           rows  = statement.executeUpdate();
       } catch (SQLException throwables) {
           throwables.printStackTrace();
       } finally {
           disconnect();
       }
    return rows;
    }

    public static void confirmEmail(Confirmation returnedConf) {
        connect();
        try (PreparedStatement statement = connection.prepareStatement("delete from confirm_email where user = (select id from users where email = ?) and code = ?")){
            statement.setString(1, returnedConf.getEmail());
            statement.setInt(2, returnedConf.getCode());
            statement.executeUpdate();
        } catch (SQLException throwables) {
            log.error(throwables);
        } finally {
            disconnect();
        }
        connect();
        try (PreparedStatement statement = connection.prepareStatement("update users set email_confirmed = 1 where email = ?")){
            statement.setString(1, returnedConf.getEmail());
            statement.executeUpdate();
        } catch (SQLException throwables) {
            log.error(throwables);
        }finally {
            disconnect();
        }
    }

    public static void createOrUpdate(Confirmation confirmation) {

        if (update(confirmation, confirmation) == 0){
            create(confirmation);
        }
    }
}
