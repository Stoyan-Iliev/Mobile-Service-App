package com.company;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class WorkerRunnable implements Runnable{

    protected Socket clientSocket;

    public WorkerRunnable(Socket clientSocket) {
        this.clientSocket = clientSocket;
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
                        scanner.close();
                        printout.close();
                        break;
                    case "1":
                        OperatorRunnable operatorRunnable = new OperatorRunnable(clientSocket, scanner, printout);
                        new Thread(operatorRunnable, "Operator thread").start();
                        break;
                    case "2":
                        ClientRunnable clientRunnable = new ClientRunnable(clientSocket, scanner, printout);
                        new Thread(clientRunnable, "Client Thread").start();
                        break;
                    default:
                        isChoiceCorrect = false;
                        printout.println("Please enter a correct choice!");
                        break;
                }
            }
        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }
}
