package com.company;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class WorkerRunnable implements Runnable{

    protected Socket clientSocket;
    protected String serverText;

    public WorkerRunnable(Socket clientSocket, String serverText) {
        this.clientSocket = clientSocket;
        this.serverText   = serverText;
    }

    public void run() {
        try {
            Scanner scanner  = new Scanner(clientSocket.getInputStream());
            PrintStream printout = new PrintStream(clientSocket.getOutputStream());

            boolean isChoiceCorrect = false;
            while(!isChoiceCorrect){
                printout.println("Enter quit to exit, 1 if you are operator or 2 if you are client: ");
                Utils.sendStopSignal(printout);
                String choice = scanner.nextLine();
                System.out.println(2345);

                isChoiceCorrect = true;
                switch (choice.toLowerCase()){
                    case "quit":
                        printout.println("Goodbye!");
                        Utils.sendStopSignal(printout);
                        return;
                    case "1":
                        new OperatorThread(clientSocket).run();
                        break;
                    case "2":
                        new ClientThread(clientSocket).run();
                        break;
                    default:
                        isChoiceCorrect = false;
                        printout.println("Please enter a correct choice!");
                        break;
                }
            }

            printout.close();
            scanner.close();

        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }
}
