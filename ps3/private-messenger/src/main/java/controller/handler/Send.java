package controller.handler;

import controller.Controller;
import controller.annotation.Attribute;
import exception.InvalidInstructionsException;
import exception.MessengerException;
import user.Contact;

@SuppressWarnings({"UnusedDeclaration", "FieldMayBeFinal"})
public class Send extends LoggedInHandler {
    @Attribute(name = "message", description = "the message you want to send", standalone = true)
    private String sendMessage = null;
    @Attribute(name = "host", description = "receiver's IP")
    private String hostIP = null;
    @Attribute(name = "port",
            description = "receiver's port; can be entered independently after starting focus and setting host")
    private String portString = null;
    @Attribute(name = "username", description = "send a message to this contact")
    private String contactUsername = null;

    public Send(Controller controller) {
        super(controller, true);
    }

    public void sendMessage() throws InvalidInstructionsException, MessengerException {
        if (hostIP != null)
            sendMessageUnfocused();
        else if (portString != null)
            sendMessageHalfFocused();
        else if (contactUsername != null)
            sendMessageToContact();
        else
            sendMessageFocused();
    }

    private void sendMessageUnfocused() throws InvalidInstructionsException, MessengerException {
        if (portString == null || contactUsername != null)
            throw new InvalidInstructionsException(this);

        if (controller.getSender().isConnected()) {
            if (controller.getFocusIP() != null
                    && controller.getFocusIP().equals(hostIP)
                    && controller.getFocusPort() == Integer.parseInt(portString))
                controller.getSender().sendMessage(controller.getUser().getUsername(), controller.getFocusIP(),
                        controller.getFocusPort(), sendMessage);
            else
                disconnectAndSend(controller.getUser().getUsername(), hostIP, Integer.parseInt(portString));
        } else
            resetFocusAndSend(controller.getUser().getUsername(), hostIP, Integer.parseInt(portString));
    }

    private void sendMessageHalfFocused() throws InvalidInstructionsException, MessengerException {
        if (contactUsername != null)
            throw new InvalidInstructionsException(this);
        if (controller.getFocusIP() == null)
            throw new MessengerException("could not sent message");

        if (controller.getSender().isConnected()) {
            if (controller.getFocusPort() == Integer.parseInt(portString))
                controller.getSender().sendMessage(controller.getUser().getUsername(), controller.getFocusIP(),
                        controller.getFocusPort(), sendMessage);
            else
                disconnectAndSend(controller.getUser().getUsername(), hostIP, Integer.parseInt(portString));
        } else
            resetFocusAndSend(controller.getUser().getUsername(), hostIP, Integer.parseInt(portString));
    }

    private void sendMessageFocused() throws MessengerException {
        if (controller.getFocusIP() == null || controller.getFocusPort() == -1)
            throw new MessengerException("could not send message");
        if (!controller.getSender().isConnected())
            throw new MessengerException("host disconnected");
        controller.getSender().sendMessage(controller.getUser().getUsername(), controller.getFocusIP(),
                controller.getFocusPort(), sendMessage);
    }

    private void sendMessageToContact() throws MessengerException {
        Contact contact = controller.getUser().getContactByUsername(contactUsername);
        if (contact == null)
            throw new MessengerException("no contact with such username was found");

        if (controller.getSender().isConnected()) {
            if (controller.getFocusIP().equals(hostIP)
                    && controller.getFocusPort() == Integer.parseInt(portString))
                controller.getSender().sendMessage(controller.getUser().getUsername(), controller.getFocusIP(),
                        controller.getFocusPort(), sendMessage);
            else
                disconnectAndSend(contact.getUsername(), contact.getHostIP(), contact.getPort());
        } else
            resetFocusAndSend(contact.getUsername(), contact.getHostIP(), contact.getPort());
    }

    private void disconnectAndSend(String username, String hostIP, int hostPort) throws MessengerException {
        controller.getSender().disconnect();
        resetFocusAndSend(username, hostIP, hostPort);
    }

    private void resetFocusAndSend(String username, String hostIP, int hostPort) throws MessengerException {
        controller.setFocusPort(-1);
        controller.getSender().sendMessage(username, controller.getFocusIP(),
                controller.getFocusPort(), sendMessage, hostIP, hostPort);
    }
}
