package user;

import java.util.*;
import java.util.stream.Collectors;

public class User {
    private final Map<String,Contact> contacts = new HashMap<>();
    private final List<Message> messages = new ArrayList<>();

    private final String username;
    private final String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public boolean isPasswordCorrect(String password) {
        return this.password.equals(password);
    }

    public synchronized void addContact(String username, String host, int port) {
        addContact(new Contact(username, host, port));
    }

    public synchronized void addContact(Contact contact) {
        contacts.put(contact.getUsername(), contact);
    }

    public Contact getContactByUsername(String username) {
        return contacts.get(username);
    }

    public List<Contact> getContactList() {
        return new ArrayList<>(contacts.values());
    }

    public synchronized void updateContact(String username, String host, int port) {
        Contact contact = contacts.get(username);
        if (contact != null) {
            contact.setHostIP(host);
            contact.setPort(port);
        } else
            addContact(username, host, port);
    }

    public synchronized void submitMessage(String username, String message, String host, int port) {
        updateContact(username, host, port);
        addMessage(username, message);
    }

    public synchronized void addMessage(String username, String message) {
        addMessage(new Message(username, message));
    }

    public synchronized void addMessage(Message message) {
        messages.add(message);
    }

    public List<Message> getMessages() {
        return messages;
    }

    public Set<String> getSenders() {
        return messages.stream().map(Message::getUsername).collect(Collectors.toSet());
    }
}
