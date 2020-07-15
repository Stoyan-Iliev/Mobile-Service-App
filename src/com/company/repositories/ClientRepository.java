package com.company.repositories;

import com.company.models.Client;

import java.sql.Connection;
import java.util.List;

public class ClientRepository extends JdbcDataRepository<Client> {

    public ClientRepository(Connection connection) {
        super(connection);
    }

    @Override
    protected String getTableName() {
        return "clients";
    }

    protected List<String> getColumnNames(){
        return List.of("first_name", "last_name", "email", "username", "password");
    }
}
