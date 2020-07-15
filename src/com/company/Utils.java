package com.company;

import java.io.PrintStream;
import java.util.Scanner;

public class Utils {
    public static void sendStopSignal(PrintStream printStream){
        printStream.println("#");
    }
}
