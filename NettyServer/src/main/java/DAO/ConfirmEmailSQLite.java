package DAO;

import Entities.Confirmation;
import lombok.extern.log4j.Log4j;

import java.sql.*;
@Log4j
public class ConfirmEmailSQLite implements DAO<Confirmation> {
    private final String connectionAddress = "jdbc:mysql://localhost:3306/cloudstorage";
    private final String login = "root";
    private final String password = "123456";
    Connection connection;
    String getConfirmationByEmail = "SELECT * FROM USERS WHERE email = ?";
    private final String pathToFile ="jdbc:sqlite:" + getClass().getResource("db.s3db");

    public ConfirmEmailSQLite() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(pathToFile);

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Confirmation getInstanceByName(Confirmation entity) {
        Confirmation confirmation = new Confirmation();
        try (PreparedStatement statement = connection.prepareStatement("select users.email, code from confirm_email left join users on confirm_email.id = users.id where users.email = ? and code = ?")){
            statement.setString(1, entity.getEmail());
            statement.setInt(2, entity.getCode());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                confirmation.setEmail(resultSet.getString(1));
                confirmation.setCode(resultSet.getInt(2));
                log.info("Code was found for: " + confirmation.getEmail());
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return confirmation;
    }

    @Override
    public void create(Confirmation entity) {
        try (PreparedStatement statement = connection.prepareStatement("insert into confirm_email (user, code) values ((select id from users where email = ?),?)")){
            statement.setString(1, entity.getEmail());
            statement.setInt(2, entity.getCode());
            statement.executeUpdate();
        } catch (SQLException throwables) {
            log.error("Exception where we try insert value into confirm_email table. ", throwables);
        }
    }

    @Override
    public int update(Confirmation oldEntity, Confirmation newEntity) {
        int count = 0;
        try (PreparedStatement statement = connection.prepareStatement("update confirm_email set code = ? where user = (select id from users where email = ?)")){
            statement.setInt(1, newEntity.getCode());
            statement.setString(2, newEntity.getEmail());
            count = statement.executeUpdate();
        } catch (SQLException throwables) {
            log.error(throwables);
        }
        return count;
    }

    @Override
    public int delete(Confirmation entity) {
        return 0;
    }

    public void confirmEmail(Confirmation returnedConf) {
        try (PreparedStatement statement = connection.prepareStatement("delete from confirm_email where user = (select id from users where email = ?) and code = ?")){
            statement.setString(1, returnedConf.getEmail());
            statement.setInt(2, returnedConf.getCode());
            statement.executeUpdate();
        } catch (SQLException throwables) {
            log.error(throwables);
        }
        try (PreparedStatement statement = connection.prepareStatement("update users set email_confirmed = 1 where email = ?")){
            statement.setString(1, returnedConf.getEmail());
            statement.executeUpdate();
        } catch (SQLException throwables) {
            log.error(throwables);
        }
    }

    public void createOrUpdate(Confirmation confirmation) {
        if (update(confirmation, confirmation) == 0){
            create(confirmation);
        }
    }
}
