package com.company.repositories.base;

import java.sql.SQLException;

public interface DataRepository<T> {

    boolean isLogged(String username, String password) throws SQLException;
}
