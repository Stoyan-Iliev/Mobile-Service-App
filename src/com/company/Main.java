package com.company;

import com.company.server.ThreadPooledServer;

public class Main {

    public static void main(String[] args) {
        ThreadPooledServer server = new ThreadPooledServer(8080);
        new Thread(server).start();
    }
}
