package com.company.repositories;

import com.company.models.PhoneNumber;
import com.company.models.PhoneNumberService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ClientRepository {

    private final Connection connection;

    public ClientRepository(Connection connection) {
        this.connection = connection;
    }

    protected String getTableName() {
        return "clients";
    }

    protected List<String> getColumnNames() {
        return List.of("first_name", "last_name", "email", "username", "password");
    }

    public long isLogged(String username, String password) throws SQLException {
        String queryString =
                "select * from " + getTableName() +
                        " where username = '" + username +
                        "' and password = '" + password + "'";
        PreparedStatement preparedStatement = connection.prepareStatement(queryString);
        ResultSet set = preparedStatement.executeQuery();
        set.next();
        return set.getLong("id");
    }

    public List<PhoneNumber> getClientPhoneNumber(long clientId) throws SQLException {
        String queryString = "select * from phone_numbers " +
                "where client_id = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(queryString);
        preparedStatement.setLong(1, clientId);

        ResultSet set = preparedStatement.executeQuery();

        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        while (set.next()) {
            long id = set.getLong("id");
            String numberAsString = set.getString("number");

            PhoneNumber number = new PhoneNumber(id, numberAsString, clientId);

            phoneNumbers.add(number);
        }

        return phoneNumbers;
    }

    public List<PhoneNumberService> getPhoneNumberActiveServicesByPhoneId(long id) throws SQLException {
        String queryString = "select * from services_phone_numbers as sp " +
                "join services as s on s.id = sp.service_id " +
                "where is_activated = true and " +
                "phone_number_id = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(queryString);
        preparedStatement.setLong(1, id);

        ResultSet set = preparedStatement.executeQuery();

        List<PhoneNumberService> activatedServices = new ArrayList<>();

        while (set.next()) {
            String name = set.getString("name");
            double remainingValue = set.getDouble("remaining_value");
            LocalDate deactivationDate = LocalDate.parse(set.getDate("deactivation_date").toString());

            PhoneNumberService service = new PhoneNumberService(name, remainingValue, deactivationDate);

            activatedServices.add(service);
        }
        return activatedServices;
    }

    public List<PhoneNumberService> getDeactivationDatesOnUnpaidServicesByPhoneId(long id) throws SQLException {
        String queryString = "select * from services_phone_numbers as sp " +
                "join services as s on s.id = sp.service_id " +
                "where sp.is_paid = false " +
                "and sp.is_activated = true " +
                "and sp.phone_number_id = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(queryString);
        preparedStatement.setLong(1, id);
        ResultSet set = preparedStatement.executeQuery();

        List<PhoneNumberService> services = new ArrayList<>();
        while (set.next()) {
            String name = set.getString("name");
            double startingValue = set.getDouble("value");
            double price = set.getDouble("price");
            LocalDate deactivationDate = LocalDate.parse(set.getString("deactivation_date"));

            PhoneNumberService phoneNumberService = new PhoneNumberService(name, deactivationDate, price, startingValue);
            services.add(phoneNumberService);
        }

        return services;
    }
}
