package DAO;


import Entities.User;

import java.sql.SQLException;

public interface UserDAO<T> {
    T getInstanceByName(String name, String password) throws SQLException;
    void create(T entity);
    void updateUser(T oldEntity, T newEntity);
    boolean deleteUser(T entity);
}
