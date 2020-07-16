package com.company;

public class Main {

    public static void main(String[] args) {
        ThreadPooledServer server = new ThreadPooledServer(8080);
        new Thread(server).start();

        try {
            Thread.sleep(20 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Stopping Server");
        server.stop();
    }
}
