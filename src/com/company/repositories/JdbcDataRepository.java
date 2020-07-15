package com.company.repositories;

import com.company.models.Client;
import com.company.repositories.base.DataRepository;

import java.sql.*;

public abstract class JdbcDataRepository<T> implements DataRepository<T> {

    protected final Connection connection;

    public JdbcDataRepository(Connection connection){
        this.connection = connection;
    }

    @Override
    public boolean isLogged(String username, String password) throws SQLException {
        String queryString =
                "select * from " + getTableName() +
                " where username = '" + username +
                "' and password = '" + password + "'";
        PreparedStatement preparedStatement = connection.prepareStatement(queryString);
        ResultSet set = preparedStatement.executeQuery();
        return set.next();
    }

//    public boolean insertClient(Client )

    protected abstract String getTableName();
}
