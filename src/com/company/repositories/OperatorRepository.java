package com.company.repositories;

import com.company.models.Client;
import com.company.models.Operator;
import com.company.models.PhoneNumber;
import com.company.models.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class OperatorRepository {
    private final Connection connection;

    public OperatorRepository(Connection connection) {
        this.connection = connection;
    }

    public List<Client> getAllClientsWithUnpaidBills() throws SQLException {
        String queryString = "select * from clients\n" +
                "where id in (select client_id from phone_numbers\n" +
                "    where id in (select phone_number_id from services_phone_numbers\n" +
                "        where is_paid = false))";

        PreparedStatement preparedStatement = connection.prepareStatement(queryString);
        ResultSet set = preparedStatement.executeQuery();

        List<Client> clientList = getClients(set);

        return clientList;
    }

    private List<Client> getClients(ResultSet set) throws SQLException {
        List<Client> clientList = new ArrayList<>();

        while (set.next()) {
            Client client = new Client();
            client.setFirstName(set.getString("first_name"));
            client.setLastName(set.getString("last_name"));
            client.setEmail(set.getString("email"));

            clientList.add(client);
        }
        return clientList;
    }

    private Service getServiceById(long id) throws SQLException {
        String queryString = "select * from services " +
                "where id = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(queryString);

        preparedStatement.setLong(1, id);

        ResultSet set = preparedStatement.executeQuery();
        set.next();

        Service service = new Service();

        service.setName(set.getString("name"));
        service.setValue(set.getDouble("value"));
        service.setPrice(set.getDouble("price"));
        service.setDurationDays(set.getInt("duration_days"));

        return service;

    }

    public boolean isLogged(String username, String password) throws SQLException {
        String queryString =
                "select * from operators" +
                        " where username = '" + username +
                        "' and password = '" + password + "'";
        PreparedStatement preparedStatement = connection.prepareStatement(queryString);
        ResultSet set = preparedStatement.executeQuery();
        return set.next();
    }

    public Map<Client, List<String>> getClientsWithService(int id) throws SQLException {
        String queryString = "select * from clients as c " +
                "join phone_numbers as pn on pn.client_id = c.id " +
                "where pn.id in (select phone_number_id from services_phone_numbers " +
                "where service_id = ?)";

        PreparedStatement preparedStatement = connection.prepareStatement(queryString);
        preparedStatement.setInt(1, id);

        ResultSet set = preparedStatement.executeQuery();

        Map<Client, List<String>> clientsPhoneNumbers = new HashMap<>();

        while (set.next()) {
            Client client = new Client();
            client.setFirstName(set.getString("first_name"));
            client.setLastName(set.getString("last_name"));
            client.setEmail(set.getString("email"));

            if (!clientsPhoneNumbers.containsKey(client)) {
                clientsPhoneNumbers.put(client, new ArrayList<>());
            }

            String phoneNumber = set.getString("number");

            List<String> numbers = clientsPhoneNumbers.get(client);
            numbers.add(phoneNumber);

            clientsPhoneNumbers.put(client, numbers);
        }

        return clientsPhoneNumbers;
    }

    public void addNewService(Service service) throws SQLException {
        String queryString = "insert into services (name, value, price, duration_days)" +
                "values (?, ?, ?, ?)";

        PreparedStatement preparedStatement = connection.prepareStatement(queryString);

        preparedStatement.setString(1, service.getName());
        preparedStatement.setDouble(2, service.getValue());
        preparedStatement.setDouble(3, service.getPrice());
        preparedStatement.setInt(4, service.getDurationDays());

        preparedStatement.executeUpdate();
    }

    public void addNewClient(Client client) throws SQLException {
        String queryString = "insert into clients (first_name, last_name, email, egn, username, password) " +
                "value (?, ?, ?, ?, ?, ?)";

        PreparedStatement preparedStatement = connection.prepareStatement(queryString);

        preparedStatement.setString(1, client.getFirstName());
        preparedStatement.setString(2, client.getLastName());
        preparedStatement.setString(3, client.getEmail());
        preparedStatement.setString(4, client.getEgn());
        preparedStatement.setString(5, client.getUsername());
        preparedStatement.setString(6, client.getPassword());

        preparedStatement.executeUpdate();
    }

    public long getClientId(String egn) throws SQLException {
        String queryString = "select id from clients " +
                "where egn = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(queryString);

        preparedStatement.setString(1, egn);
        ResultSet set = preparedStatement.executeQuery();

        set.next();
        return set.getLong("id");
    }

    private long getPhoneNumberId(String phoneNumber) throws SQLException {
        String queryString = "select id from phone_numbers " +
                "where number = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(queryString);
        preparedStatement.setString(1, phoneNumber);

        ResultSet set = preparedStatement.executeQuery();

        set.next();
        return set.getLong("id");

    }

    public void addNewPhoneNumber(String phoneNumber, long clientId) throws SQLException {
        String queryString = "insert into phone_numbers (number, client_id) " +
                "value (?, ?)";

        PreparedStatement preparedStatement = connection.prepareStatement(queryString);

        preparedStatement.setString(1, phoneNumber);
        preparedStatement.setLong(2, clientId);

        preparedStatement.executeUpdate();
    }

    public List<PhoneNumber> getPhoneNumbersByEgnOfClient(String egn) throws SQLException {
        String queryString = "select number from phone_numbers " +
                "where client_id = ?";

        long clientId = getClientId(egn);

        PreparedStatement preparedStatement = connection.prepareStatement(queryString);
        preparedStatement.setLong(1, clientId);

        ResultSet set = preparedStatement.executeQuery();

        List<PhoneNumber> phoneNumbers = new ArrayList<>();

        while(set.next()){
            PhoneNumber phoneNumber = new PhoneNumber(set.getString("number"), clientId);
            phoneNumbers.add(phoneNumber);
        }

        return phoneNumbers;
    }

    public void activateService(String chosenPhone, long serviceId) throws SQLException {
        String queryString = "insert into services_phone_numbers " +
                "(phone_number_id, service_id, remaining_value, activation_date) " +
                "values (?, ?, ?, curdate())";

        PreparedStatement preparedStatement = connection.prepareStatement(queryString);

        Service service = getServiceById(serviceId);
        long phoneNumberId = getPhoneNumberId(chosenPhone);

        preparedStatement.setLong(1, phoneNumberId);
        preparedStatement.setLong(2, serviceId);
        preparedStatement.setDouble(3, service.getValue());

        preparedStatement.executeUpdate();
    }
}
