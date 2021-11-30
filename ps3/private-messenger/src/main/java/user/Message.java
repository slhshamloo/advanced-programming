package user;

public class Message {
    private final String username;
    private final String message;

    public Message(String username, String message) {
        this.username = username;
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return username + " -> " + message;
    }
}
