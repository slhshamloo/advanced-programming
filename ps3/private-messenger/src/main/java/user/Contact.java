package user;

public class Contact {
    private final String username;
    private String hostIP;
    private int port;

    public Contact(String username, String hostIP, int port) {
        this.username = username;
        this.hostIP = hostIP;
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public String getHostIP() {
        return hostIP;
    }

    public synchronized void setHostIP(String hostIP) {
        this.hostIP = hostIP;
    }

    public int getPort() {
        return port;
    }

    public synchronized void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return username + " -> " + hostIP + ":" + port;
    }
}
