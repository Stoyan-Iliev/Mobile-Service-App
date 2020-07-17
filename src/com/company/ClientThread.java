package com.company;

import com.company.models.PhoneNumber;
import com.company.models.PhoneNumberService;
import com.company.repositories.ClientRepository;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class ClientThread implements Runnable {
    private Socket clientSocket;
    private long clientId;

    private Scanner scanner;
    private PrintStream printout;

    private ClientRepository repository;

    public ClientThread(Socket clientSocket, Scanner scanner, PrintStream printout) {
        this.clientSocket = clientSocket;
        this.scanner = scanner;
        this.printout = printout;

        Connection connection = DatabaseConnection.createConnection();
        repository = new ClientRepository(connection);
    }

    @Override
    public void run() {
        clientId = getLoginId();
        if (clientId == -1) {
            printout.println("Goodbye");
            scanner.close();
            printout.close();
            return;
        } else if (clientId == 0) {
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
                    printClientServicesInfo();
                    break;
                case "2":
                    printPaymentDeadlineForEveryService();
                    break;
                default:
                    printout.println("Enter valid command.");

            }
        }

        scanner.close();
        printout.close();
    }

    private void printPaymentDeadlineForEveryService() {
        try {
            List<PhoneNumber> phoneNumbers = repository.getClientPhoneNumber(clientId);

            StringBuilder stringBuilder = new StringBuilder();
            for (PhoneNumber phoneNumber : phoneNumbers) {
                List<PhoneNumberService> services = repository.
                        getDeactivationDatesOnUnpaidServicesByPhoneId(phoneNumber.getId());

                for (PhoneNumberService service : services) {
                    stringBuilder
                            .append("You need to pay ")
                            .append(service.getPrice())
                            .append("lv for ")
                            .append(service.getStartingValue())
                            .append(" ")
                            .append(service.getName())
                            .append(" before ")
                            .append(service.getDeactivationDate())
                            .append(System.lineSeparator());
                }
            }

            printout.println(stringBuilder);
        } catch (SQLException e) {
            printout.println("Something went wrong try again");
        }
    }

    private void printClientServicesInfo() {
        try {
            List<PhoneNumber> phoneNumbers = repository.getClientPhoneNumber(clientId);

            StringBuilder stringBuilder = new StringBuilder();
            for (PhoneNumber phoneNumber : phoneNumbers) {
                List<PhoneNumberService> services = repository.
                        getPhoneNumberActiveServicesByPhoneId(phoneNumber.getId());

                for (PhoneNumberService service : services) {
                    stringBuilder
                            .append(service.getRemainingValue())
                            .append(" ")
                            .append(service.getName())
                            .append(" until ")
                            .append(service.getDeactivationDate())
                            .append(System.lineSeparator());
                }
            }

            printout.println(stringBuilder);
        } catch (SQLException e) {
            printout.println("Something went wrong try again");
        }
    }

    private void printMenu() {
        String[] menu = {
                "Enter:",
                "1 To view information about your active services and the remaining Minutes, MBs or SMSes.",
                "2 To view the next payment date for the services you have not paid",
                "Quit For exit from the program."};

        printout.println(String.join(System.lineSeparator(), menu));
        Utils.sendStopSignal(printout);
    }

    private long getLoginId() {
        long isLogged = -1;
        for (int i = 0; i < 3; i++) {
            printout.println("Enter username or enter quit to exit:");
            Utils.sendStopSignal(printout);
            String username = scanner.nextLine();

            if (username.toLowerCase().equals("quit")) {
                return isLogged;
            }

            printout.println("Enter password:");
            Utils.sendStopSignal(printout);
            String password = scanner.nextLine();

            try {
                isLogged = repository.isLogged(username, password);
                return isLogged;
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
