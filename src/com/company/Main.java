package com.company;

public class Main {

    public static void main(String[] args) {
        ThreadPooledServer server = new ThreadPooledServer(8080);
        new Thread(server).start();
    }
}
