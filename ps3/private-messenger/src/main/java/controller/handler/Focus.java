package controller.handler;

import controller.Controller;
import controller.annotation.Attribute;
import controller.annotation.Label;
import exception.InvalidInstructionsException;
import exception.MessengerException;
import user.Contact;

@SuppressWarnings({"UnusedDeclaration", "FieldMayBeFinal"})
public class Focus extends InstructionHandler {
    private final Controller controller;

    @Label(name = "start", description = "start focusing on a host; can additionally take a port", required = false)
    private boolean start = false;
    @Label(name = "stop", description = "stop focusing on the current host", required = false)
    private boolean stop = false;

    @Attribute(name = "host", description = "host's IP")
    private String hostIP = null;
    @Attribute(name = "port",
            description = "host's port; can be entered independently after starting focus and setting host")
    private String portString = null;
    @Attribute(name = "username", description = "connect to this contact")
    private String contactUsername = null;

    public Focus(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void handle() throws InvalidInstructionsException, MessengerException {
        if (controller.getUser() == null)
            throw new MessengerException("you must login to access this feature");
        if (start)
            start();
        else if (stop)
            stop();
        else if (portString != null)
            setPortStandalone();
        else
            throw new InvalidInstructionsException(this);
        Controller.printSuccess();
    }

    private void start() throws InvalidInstructionsException, MessengerException {
        if (stop)
            throw new InvalidInstructionsException(this);
        if (hostIP != null) {
            if (contactUsername != null)
                throw new InvalidInstructionsException(this);

            changeFocus();
            if (portString != null) {
                controller.setFocusPort(Integer.parseInt(portString));
                controller.getSender().connectAndFocus(controller.getFocusIP(), controller.getFocusPort());
            }
        } else if (contactUsername != null)
            connectToContact();
    }

    private void changeFocus() throws MessengerException {
        if (controller.getSender().isConnected())
            controller.getSender().disconnect();
        controller.setFocusIP(hostIP);
        controller.setFocusPort(-1);
    }

    private void connectToContact() throws InvalidInstructionsException, MessengerException {
        if (portString != null)
            throw new InvalidInstructionsException(this);

        Contact contact = controller.getUser().getContactByUsername(contactUsername);
        if (contact == null)
            throw new MessengerException("no contact with such username was found");

        if (controller.getSender().isConnected())
            controller.getSender().disconnect();
        controller.setFocusIP(contact.getHostIP());
        controller.setFocusPort(contact.getPort());
        controller.getSender().connectAndFocus(controller.getFocusIP(), controller.getFocusPort());
    }

    private void stop() throws InvalidInstructionsException, MessengerException {
        if (start || hostIP != null || portString != null || contactUsername != null)
            throw new InvalidInstructionsException(this);
        if (controller.getFocusIP() != null) {
            if (controller.getSender().isConnected())
                controller.getSender().disconnect();
            controller.setFocusIP(null);
            controller.setFocusPort(-1);
        } else
            throw new MessengerException("you must focus on a host before using this command");
    }

    private void setPortStandalone() throws InvalidInstructionsException, MessengerException {
        if (start || stop || hostIP != null || contactUsername != null)
            throw new InvalidInstructionsException(this);
        if (controller.getFocusIP() == null)
            throw new MessengerException("you must focus on a host before using this command");

        if (controller.getSender().isConnected())
            controller.getSender().disconnect();
        controller.setFocusPort(Integer.parseInt(portString));
        controller.getSender().connectAndFocus(controller.getFocusIP(), controller.getFocusPort());
    }
}
