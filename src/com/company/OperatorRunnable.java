package com.company;

import com.company.models.Client;
import com.company.models.PhoneNumber;
import com.company.models.Service;
import com.company.repositories.OperatorRepository;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class OperatorRunnable implements Runnable {
    private Socket clientSocket;
    private final OperatorRepository repository;

    private Scanner scanner;
    private PrintStream printout;

    private final String message = "Please enter the %s or Back to return to the menu: ";
    private String messageSuccess = "Operation successful.";

    public OperatorRunnable(Socket clientSocket, Scanner scanner, PrintStream printout) {
        this.clientSocket = clientSocket;
        this.scanner = scanner;
        this.printout = printout;

        Connection connection = DatabaseConnection.createConnection();
        repository = new OperatorRepository(connection);
    }

    @Override
    public void run() {

        if (!isLoginSuccessful()) {
            printout.println("We are sorry, you are not allowed access!");
            scanner.close();
            printout.close();
            return;
        }

        while (true) {
            printMenu();
            String input = scanner.nextLine().toLowerCase();

            if (input.equals("quit")) {
                printout.println("Goodbye");
                scanner.close();
                printout.close();
                break;
            }

            switch (input) {
                case "1":
                    addServiceToAClientPhone();
                    break;
                case "2":
                    addNewClient();
                    break;
                case "3":
                    printServiceOfClient();
                    break;
                case "4":
                    printClientsWithService();
                    break;
                case "5":
                    printClientsWithUnpaidBill();
                    break;
                case "6":
                    addNewService();
                    break;
                case "7":
                    giveClientAPhoneNumber();
                    break;
                default:
                    printout.println("Enter valid command.");
                    break;
            }
        }

        scanner.close();
        printout.close();
    }

    private void addServiceToAClientPhone() {
        String egn = getString("egn of the client");
        if (egn == null) return;

        List<String> phoneNumbers = getClientsPhoneNumbers(egn);
        if (phoneNumbers == null) {
            return;
        }

        String phoneNumbersAsString = String.join(", ", phoneNumbers);

        printout.println(String.format("The client with egn %s has %s numbers", egn, phoneNumbersAsString));

        String chosenPhone = getString("phone number on which to activate the service");
        if (chosenPhone == null) return;

        long serviceId;
        while (true) {
            String serviceIdAsString = getString("service number you want to activate");
            if (serviceIdAsString == null) return;

            try {
                serviceId = Long.parseLong(serviceIdAsString);
            } catch (NumberFormatException e) {
                printout.println("Invalid service number.");
                Utils.sendStopSignal(printout);
                continue;
            }

            break;
        }

        try {
            repository.activateService(chosenPhone, serviceId);
            printout.println("Service Activated successfully.");
        } catch (SQLException e) {
            printout.println("The service is already active");
        }
    }

    private List<String> getClientsPhoneNumbers(String egn) {
        List<PhoneNumber> phoneNumbers;
        try {
            phoneNumbers = repository.getPhoneNumbersByEgnOfClient(egn);
        } catch (SQLException e) {
            printout.println("Make sure that the egn you have given is correct and if it is " +
                    "make sure that the given client has at least one phone number given to him.");
            return null;
        }

        return phoneNumbers.stream()
                .map(PhoneNumber::getNumber)
                .collect(Collectors.toList());
    }

    private void addNewService() {
        String name = getString("name of the service");
        if (name == null) return;

        double value;
        while (true) {
            String input = getString("volume of the service");
            if (input == null) return;

            try {
                value = Double.parseDouble(input);
                break;
            } catch (NumberFormatException e) {
                printout.println("Invalid input try again.");
            }
        }

        double price;
        while (true) {
            String input = getString("price of the service");
            if (input == null) return;

            try {
                price = Double.parseDouble(input);
                break;
            } catch (NumberFormatException e) {
                printout.println("Invalid input try again.");
            }
        }

        int durationDays;
        while (true) {
            String input = getString("duration of the service in days");
            if (input == null) return;


            try {
                durationDays = Integer.parseInt(input);
                break;
            } catch (NumberFormatException e) {
                printout.println("Invalid input try again.");
            }
        }

        Service service = new Service(name, value, price, durationDays);

        try {
            repository.addNewService(service);
            printout.println("Service added successfully");
        } catch (SQLException e) {
            printout.println("The service already exists.");
        }
    }

    private void addNewClient() {
        String currentMessage = " of the client";

        String firstName = getString("first name" + currentMessage);
        if (firstName == null) return;

        String lastName = getString("last name" + currentMessage);
        if (lastName == null) return;

        String email = getString("email" + currentMessage);
        if (email == null) return;

        String egn = getString("egn" + currentMessage);
        if (egn == null) return;

        String username = getString("username" + currentMessage);
        if (username == null) return;

        String password = getString("password" + currentMessage);
        if (password == null) return;

        Client client = new Client(firstName, lastName, email, egn, username, password);

        try {
            repository.addNewClient(client);
            printout.println("Client added successfully");
        } catch (SQLException e) {
            printout.println("Client with this egn, email or username is already in the system.");
        }
    }

    private void giveClientAPhoneNumber() {
        String clientEgn = getString("egn of the client");
        if (isBack(clientEgn) == null) return;

        long clientId;
        try {
            clientId = repository.getClientId(clientEgn);
        } catch (SQLException e) {
            printout.println("There is no client with the given egn. " +
                    "Make sure you have added the client before attempting to give him phone number.");
            return;
        }

        String phoneNumber;
        while (true) {
            phoneNumber = getString("phone number to be given to the client");
            if (isBack(phoneNumber) == null) return;

            try {
                repository.addNewPhoneNumber(phoneNumber, clientId);
                printout.println("Phone number assigned successfully");
                return;
            } catch (SQLException e) {
                printout.println("Make sure that the phone number you have given is not given to another client.");
            }
        }
    }

    //ToDo Make it work
    private void printServiceOfClient() {
        printout.println("Please enter egn of client or Back to return to the menu:");

        while (true) {
            String input = scanner.nextLine().toLowerCase();

            if (input.equals("back")) {
                return;
            }


        }
    }

    private void printClientsWithService() {
        int serviceId;

        printout.println("Please enter service number or Back to return to the menu:");
        Utils.sendStopSignal(printout);

        while (true) {
            String input = scanner.nextLine();

            if (isBack(input) == null) {
                return;
            }

            try {
                serviceId = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                printout.println("Please enter valid service number or Back to return to the menu:");
                Utils.sendStopSignal(printout);
                continue;
            }

            Map<Client, List<String>> clients = null;
            try {
                clients = repository.getClientsWithService(serviceId);

            } catch (SQLException e) {
                printout.println("Something went wrong. Please try again:");
            }

            if (clients == null || clients.isEmpty()) {
                printout.println("There is no service with the given service number.");
                printout.println("Please enter service number or Back to return to the menu:");
                Utils.sendStopSignal(printout);
                continue;
            }

            for (Map.Entry<Client, List<String>> entry : clients.entrySet()) {
                String clientInfo = entry.getKey().getFirstName() + " " +
                        entry.getKey().getLastName() + " - " +
                        entry.getKey().getEmail();

                printout.println(clientInfo + ": " + String.join(", ", entry.getValue()));
            }
            break;
        }
    }

    private void printClientsWithUnpaidBill() {
        while (true) {
            try {
                List<Client> clients = repository.getAllClientsWithUnpaidBills();

                if (clients.isEmpty()) {
                    printout.println("There are no clients with unpaid bills.");
                } else {
                    clients.forEach(client -> printout.println(String
                            .format("%s %s %s",
                                    client.getFirstName(),
                                    client.getLastName(),
                                    client.getEmail())));
                }

                break;
            } catch (SQLException e) {
                printout.println("Error occurred. Please try again!");
            }
        }
    }

    private boolean isLoginSuccessful() {
        boolean isLogged = false;
        for (int i = 0; i < 3; i++) {
            if (isLogged) {
                break;
            }

            printout.println("Enter username or enter quit to exit:");
            Utils.sendStopSignal(printout);
            String username = scanner.nextLine();

            if (username.toLowerCase().equals("quit")) {
                return false;
            }

            printout.println("Enter password:");
            Utils.sendStopSignal(printout);
            String password = scanner.nextLine();

            try {
                isLogged = repository.isLogged(username, password);
            } catch (SQLException e) {
                printout.println("Error occurred. Please try again!");
                if (i == 2) {
                    Utils.sendStopSignal(printout);
                }
            }
        }
        return isLogged;
    }

    private String getString(String argString) {
        printout.println(String.format(message, argString));
        Utils.sendStopSignal(printout);

        String input = scanner.nextLine();
        return isBack(input);
    }

    private String isBack(String input) {
        if (input.toLowerCase().equals("back")) {
            return null;
        }
        return input;
    }

    private void printMenu() {
        String[] menu = {
                "Enter:",
                "1 For adding service to existing phone number.",
                "2 For adding a new client.",
                "3 For searching by client egn.",
                "4 For searching by service number.",
                "5 For searching for people who have not paid for the next period.",
                "6 For adding new service.",
                "7 For to give client a new phone number.",
                "Quit For exit from the program."};

        printout.println(String.join(System.lineSeparator(), menu));
        Utils.sendStopSignal(printout);
    }
}
