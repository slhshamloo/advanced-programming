package connection;

import controller.Controller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ListenRunnable implements Runnable {
    private final Controller controller;
    private final int port;

    public ListenRunnable(Controller controller) {
        this.controller = controller;
        this.port = controller.getPort();
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port, 0, InetAddress.getByName(null));
            while (port == controller.getPort()) {
                Socket socket = serverSocket.accept();
                new Thread(new ReceiveRunnable(controller, socket)).start();
            }
            serverSocket.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
