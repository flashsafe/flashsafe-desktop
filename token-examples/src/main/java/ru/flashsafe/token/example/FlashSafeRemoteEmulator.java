package ru.flashsafe.token.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class FlashSafeRemoteEmulator {

    private static final int HOST_VALUE_INDEX = 0;
    
    private static final int PORT_VALUE_INDEX = 1;
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        
        if (args.length != 2) {
            System.out.println("Use: token-examples <host> <port>");
            return;
        }
        
        int portNumber = Integer.parseInt(args[PORT_VALUE_INDEX]);

        try (
            Socket emulatorSocket = new Socket(args[HOST_VALUE_INDEX], portNumber);
            PrintWriter out =
                new PrintWriter(emulatorSocket.getOutputStream(), true);
                Scanner scanner = new Scanner(System.in);
        ) {
            String command = "";
            while (true) {
                System.out.println("Input command:");
                command = scanner.next();
                if ("exit".equalsIgnoreCase(command)) {
                    break;
                }
                out.println(command);
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + args[HOST_VALUE_INDEX]);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + args[HOST_VALUE_INDEX] + "[" + args[PORT_VALUE_INDEX] + "]");
            System.exit(1);
        }
    }

}
