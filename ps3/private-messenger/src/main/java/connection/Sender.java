package connection;

import exception.MessengerException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Sender {
    private Socket socket = null;
    private DataInputStream dataInputStream = null;
    private DataOutputStream dataOutputStream = null;

    public boolean isConnected() {
        return socket != null && !socket.isClosed();
    }

    public void sendMessage(String username, String senderIP, int senderPort, String message)
            throws MessengerException {
        try {
            if (senderIP == null)
                senderIP = "null";
            dataOutputStream.writeUTF("message " + username + " " + senderIP + " " + senderPort + " " + message);
            dataOutputStream.flush();
            checkInput();
        } catch (IOException exception) {
            throw new MessengerException("could not send message");
        }
    }

    public void sendMessage(
            String username, String senderIP, int senderPort, String message, String hostIP, int port)
            throws MessengerException {
        try {
            connect(hostIP, port);
            sendMessage(username, senderIP, senderPort, message);
        } catch (IOException exception) {
            throw new MessengerException("could not send message");
        }
    }

    public void connect(String hostIP, int port) throws IOException {
        socket = new Socket(hostIP, port);
        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
    }

    public void disconnect() throws MessengerException {
        try {
            dataOutputStream.writeUTF("stop");
            dataOutputStream.flush();
            socket.close();
        } catch (IOException exception) {
            throw new MessengerException("unknown connection error");
        }
    }

    public void connectAndFocus(String hostIP, int port) throws MessengerException {
        try {
            connect(hostIP, port);
            dataOutputStream.writeUTF("focus");
            dataOutputStream.flush();
            checkInput();
        } catch (IOException exception) {
            throw new MessengerException("unknown connection error");
        }
    }

    private void checkInput() throws IOException, MessengerException {
        String result = dataInputStream.readUTF();
        if (result.equals("fail"))
            throw new MessengerException("connection error: bad communication");
        else if (!result.equals("success"))
            throw new MessengerException("unknown connection error");
    }
}
