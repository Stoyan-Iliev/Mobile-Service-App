package com.company;

import com.company.repositories.ClientRepository;
import com.company.repositories.base.DataRepository;

import java.net.Socket;
import java.sql.Connection;

public class ClientThread implements Runnable {
    private Socket clientSocket;
    private Connection connection;

    public ClientThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
        connection = DatabaseConnection.createConnection();
    }

    @Override
    public void run() {

    }
}