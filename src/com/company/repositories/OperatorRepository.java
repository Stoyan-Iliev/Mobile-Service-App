package com.company.repositories;

import com.company.models.Client;
import com.company.models.Operator;
import com.company.models.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class OperatorRepository extends JdbcDataRepository<Operator> {
    public OperatorRepository(Connection connection) {
        super(connection);
    }

    public List<Client> getAllClientsWithUnpaidBills() throws SQLException {
        String queryString = "select * from clients\n" +
                "where id in (select client_id from phone_numbers\n" +
                "    where id in (select phone_number_id from services_phoneNumbers\n" +
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

    public boolean isLogged(String username, String password) throws SQLException {
        String queryString =
                "select * from " + getTableName() +
                        " where username = '" + username +
                        "' and password = '" + password + "'";
        PreparedStatement preparedStatement = connection.prepareStatement(queryString);
        ResultSet set = preparedStatement.executeQuery();
        return set.next();
    }

    public Map<Client, List<String>> getClientsWithService(int id) throws SQLException {
        String queryString = "select * from clients as c " +
                "join phone_numbers as pn.client_id = c.id " +
                "where pn.id in (select phone_number_id from services_phoneNumbers " +
                "where service_id = ?";

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
//    public void getServiceOfClient(String egn) throws SQLException {
//        String queryString = "select * from clients as c " +
//                "join mobile_phones as mp on mp.client_id = c.id " +
//                "join services as s on s.id in (select service_id from services_phoneNumbers " +
//                "where phone_number_id = mp.id)";
//
//        PreparedStatement preparedStatement = connection.prepareStatement(queryString);
//
//        ResultSet set = preparedStatement.executeQuery();
//
//        while(set.next()){
//            String fullName = set.getString("first_name") + " " + set.getString("last_name");
//            String phoneNumber = set.getString("number");
//            String service = set.getString("name") + " " + set.getDouble("value");
//
//            Map<String, Map<String, List<String>>> clientsServices = new HashMap<>();
//
//            if(!clientsServices.containsKey(fullName)){
//                clientsServices.put(fullName, new HashMap<>());
//            } else if(!clientsServices.get(fullName).containsKey(phoneNumber)){
//                Map<String, List<String>> temp = clientsServices.get(fullName);
//                temp.put(phoneNumber, new ArrayList<>();
//                clientsServices.put(fullName, temp);
//            }
//
//            List<String> temp = clientsServices.get(fullName).get(phoneNumber);
//            temp.add(service);
//
//            clientsServices.get(fullName).put(phoneNumber,temp);
//
//            clientsServices.put(fullName,clientsServices
//                    .put(phoneNumber, clientsServices.get(phoneNumber)));
//        }
//    }

    //    public boolean insertClient(Client client){
//        String queryString = "insert into " + getTableName() +
//                "(" ) values" +
//                client.getFirstName()
//    }

    @Override
    protected String getTableName() {
        return "operators";
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
}
