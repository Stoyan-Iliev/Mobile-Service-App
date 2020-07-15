package com.company;

import com.company.models.Client;
import com.company.models.Service;
import com.company.repositories.OperatorRepository;
import com.company.repositories.base.DataRepository;
import com.sun.source.tree.TryTree;
import jdk.jshell.execution.Util;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class OperatorThread implements Runnable {
    private Socket clientSocket;
    private Connection connection;
    private final OperatorRepository repository;

    public OperatorThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
        connection = DatabaseConnection.createConnection();
        repository = new OperatorRepository(connection);
    }

    @Override
    public void run() {

        OperatorRepository repository = new OperatorRepository(connection);

        try {
            Scanner scanner = new Scanner(clientSocket.getInputStream());
            PrintStream printout = new PrintStream(clientSocket.getOutputStream());

            if (!isLoginSuccessful(scanner, printout)) {
                printout.println("We am sorry, you are not allowed access!");
                scanner.close();
                printout.close();
                return;
            }

            while (true) {
                printMenu(printout);
                String input = scanner.nextLine().toLowerCase();

                if (input.equals("quit")) {
                    break;
                }

                switch (input) {
                    case "1":
                        break;
                    case "2":
                        addNewClient(scanner, printout);
                        break;
                    case "3":
                        printServiceOfClient(scanner, printout);
                        break;
                    case "4":
                        printClientsWithService(scanner, printout);
                        break;
                    case "5":
                        printClientsWithUnpaidBill(printout);
                        break;
                    case "6":
                        addNewService(scanner, printout);
                        break;
                }
            }

            scanner.close();
            printout.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addNewService(Scanner scanner, PrintStream printout) {

        printout.println("Please enter the name of the service or Back to return to the menu: ");
        Utils.sendStopSignal(printout);
        String name = scanner.nextLine();

        if (name.toLowerCase().equals("back")) {
            return;
        }

        double value;
        while (true) {
            printout.println("Please enter the volume of the service or Back to return to the menu: ");
            Utils.sendStopSignal(printout);
            String input = scanner.nextLine();

            if (input.toLowerCase().equals("back")) {
                return;
            }

            try {
                value = Double.parseDouble(input);
                break;
            } catch (NumberFormatException e) {
                printout.println("Invalid input try again.");
            }
        }

        double price;
        while (true) {
            printout.println("Please enter the price of the service or Back to return to the menu: ");
            Utils.sendStopSignal(printout);
            String input = scanner.nextLine();

            if (input.toLowerCase().equals("back")) {
                return;
            }

            try {
                price = Double.parseDouble(input);
                break;
            } catch (NumberFormatException e) {
                printout.println("Invalid input try again.");
            }
        }

        int durationDays;
        while (true) {
            printout.println("Please enter the duration of the service in days or Back to return to the menu: ");
            Utils.sendStopSignal(printout);
            String input = scanner.nextLine();

            if (input.toLowerCase().equals("back")) {
                return;
            }

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
        } catch (SQLException e) {
            printout.println("Something went wrong, please try again.");
        }
    }

    private void addNewClient(Scanner scanner, PrintStream printout) {
        String message = "Please enter the %s of the client or Back to return to the menu: ";

        String firstName = getString(scanner, printout, message, "first name");
        if (firstName == null) return;

        String lastName = getString(scanner, printout, message, "last name");
        if (lastName == null) return;

        String email = getString(scanner, printout, message, "email");
        if (email == null) return;

        String egn = getString(scanner, printout, message, "egn");
        if (egn == null) return;

        String username = getString(scanner, printout, message, "username");
        if (username == null) return;

        String password = getString(scanner, printout, message, "password");
        if (password == null) return;

        Client client = new Client(firstName, lastName, email, egn, username, password);

        try {
            repository.addNewClient(client);
        } catch (SQLException e) {
            printout.println("Something went wrong. Try again");
        }
    }


    private String getString(Scanner scanner, PrintStream printout, String message, String argString) {
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

    private void printServiceOfClient(Scanner scanner, PrintStream printout) {
        printout.println("Please enter egn of client or Back to return to the menu:");

        while (true) {
            String input = scanner.nextLine().toLowerCase();

            if (input.equals("back")) {
                return;
            }


        }
    }

    private void printClientsWithService(Scanner scanner, PrintStream printout) {
        int serviceId = -1;

        printout.println("Please enter service number or Back to return to the menu:");
        Utils.sendStopSignal(printout);

        while (true) {
            String input = scanner.nextLine().toLowerCase();

            if (input.equals("back")) {
                return;
            }

            try {
                serviceId = Integer.parseInt(input);
                break;
            } catch (NumberFormatException e) {
                printout.println("Please enter valid service number or Back to return to the menu:");
                Utils.sendStopSignal(printout);
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
                        entry.getKey().getLastName() + " " +
                        entry.getKey().getEmail();

                printout.println(clientInfo + ": " + String.join(", ", entry.getValue()));
            }
        }

    }

    private void printClientsWithUnpaidBill(PrintStream printout) {
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

    private void printMenu(PrintStream printout) {
        String[] menu = {
                "Press:",
                "1 For adding service to existing phone number.",
                "2 For adding a new client.",
                "3 For searching by client egn.",
                "4 For searching by service number.",
                "5 For searching for people who have not paid for the next period.",
                "6 For adding new service.",
                "Quit For exit from the program."};

        printout.println(String.join(System.lineSeparator(), menu));
        Utils.sendStopSignal(printout);
    }

    public boolean isLoginSuccessful(Scanner scanner, PrintStream printout) {
        boolean isLogged = false;
        for (int i = 0; i < 3; i++) {
            if (isLogged) {
                break;
            }

            printout.println("Enter username or enter quit to exit:");
            Utils.sendStopSignal(printout);
            String username = scanner.nextLine();
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
}
