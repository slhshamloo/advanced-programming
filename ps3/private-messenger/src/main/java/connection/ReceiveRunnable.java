package connection;

import controller.Controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ReceiveRunnable implements Runnable {
    private final Socket socket;
    private final Controller controller;
    private final int port;

    private boolean keepConnected = false;

    public ReceiveRunnable(Controller controller, Socket socket) {
        this.controller = controller;
        this.port = controller.getPort();
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            do {
                String result = process(dataInputStream.readUTF());
                dataOutputStream.writeUTF(result);
                dataOutputStream.flush();
            } while (keepConnected && port == controller.getPort());
            dataInputStream.close();
            socket.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private String process(String input) {
        Scanner inputScanner = new Scanner(input);
        try {
            String commandType = inputScanner.next();
            switch (commandType) {
                case "message":
                    String username = inputScanner.next();
                    String clientIP = inputScanner.next();
                    int clientPort = Integer.parseInt(inputScanner.next());
                    String message = inputScanner.nextLine().trim();

                    controller.getUser().submitMessage(
                            username, message, clientIP, clientPort);
                    return "success";
                case "focus":
                    keepConnected = true;
                    return "success";
                case "stop":
                    keepConnected = false;
                    return "success";
                default:
                    return "fail";
            }
        } catch (NoSuchElementException | NumberFormatException exception) {
            return "fail";
        }
    }
}
