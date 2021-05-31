package DAO;


import Entities.User;

import java.sql.SQLException;

public interface UserDAO<T> {
    T getInstanceByName(T entity);
    void create(T entity);
    void updateUser(T oldEntity, T newEntity);
    boolean deleteUser(T entity);
}
