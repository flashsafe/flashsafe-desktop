package ru.flashsafe.token.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.flashsafe.token.util.EventMonitor;
import ru.flashsafe.token.util.EventMonitor.EventHandler;

/**
 * 
 * 
 * @author Andrew
 *
 */
public class RemoteEmulatorMonitor implements EventHandler {
    
    private static final String ATTACH_COMMAND = "attach";
    
    private static final String  DETACH_COMMAND = "detach";
    
    private static final Logger LOGGER = LogManager.getLogger(RemoteEmulatorMonitor.class);

    private final EventMonitor monitor;
    
    private final RemoteEmulatorTokenService tokenService;
    
    private final int portNumber;
    
    private ServerSocket serverSocket;
    
    private Socket clientSocket;
    
    private BufferedReader commandReader;
    
    public RemoteEmulatorMonitor(int portNumber, RemoteEmulatorTokenService tokenService) {
        monitor = new EventMonitor("Remote emulator monitor", this, 0);
        this.portNumber = portNumber;
        this.tokenService = tokenService;
    }
    
    public void start() throws IOException {
        initMonitor();
        monitor.start();
    }
    
    public void stop() {
        monitor.stop();
        stopMonitor();
    }

    @Override
    public void onEvent() {
        try {
            acceptConnection();
            String inputCommand = null;
            while ((inputCommand = commandReader.readLine()) != null) {
                processCommand(inputCommand);
            }
            closeConnection();
        } catch (IOException e) {
            LOGGER.error("Error while listening events from emulator", e);
        }
    }
    
    private void processCommand(String commandString) {
        String[] command = commandString.split("=");
        if (command.length != 2) {
            LOGGER.warn("Bad command format: " + commandString);
            return;
        }
        if (ATTACH_COMMAND.equalsIgnoreCase(command[0])) {
            tokenService.fireAttachEvent(command[1]);
        } else if (DETACH_COMMAND.equalsIgnoreCase(command[0])) {
            tokenService.fireDetachEvent(command[1]);
        } else {
            LOGGER.warn("Unknown command: " + commandString);
        }
    }
    
    private void initMonitor() throws IOException {
        serverSocket = new ServerSocket(portNumber);
        LOGGER.debug("started server at " + portNumber);
    }
    
    private void closeConnection() {
        if (commandReader != null) {
            try {
                commandReader.close();
            } catch (IOException e) {
                LOGGER.error("Error while closing commandReader", e);
            }
        }
        if (clientSocket != null) {
            try {
                clientSocket.close();
            } catch (IOException e) {
                LOGGER.error("Error while closing clientSocket", e);
            }
        }
    }
    
    private void stopMonitor() {
        closeConnection();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                LOGGER.error("Error while closing serverSocket", e);
            }
        }
    }
    
    private void acceptConnection() throws IOException {
        try {
            clientSocket = serverSocket.accept();
            commandReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            throw e;
        }
    }

}
