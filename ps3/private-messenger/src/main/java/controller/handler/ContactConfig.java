package controller.handler;

import controller.Controller;
import controller.annotation.Attribute;
import controller.annotation.Label;
import exception.InvalidInstructionsException;
import exception.MessengerException;

@SuppressWarnings({"UnusedDeclaration", "FieldMayBeFinal"})
public class ContactConfig extends LoggedInHandler {
    @Label(name = "link", description = "manually set contact info")
    private boolean linkContact = false;

    @Attribute(name = "username", description = "contact username")
    private String username = null;
    @Attribute(name = "host", description = "contact host IP")
    private String hostIP = null;
    @Attribute(name = "port", description = "contact port")
    private String portString = null;

    public ContactConfig(Controller controller) {
        super(controller, true);
    }

    public void linkContact() throws InvalidInstructionsException, MessengerException {
        if (username == null || hostIP == null || portString == null)
            throw new InvalidInstructionsException(this);
        try {
            controller.getUser().updateContact(username, hostIP, Integer.parseInt(portString));
        } catch (NumberFormatException exception) {
            throw new MessengerException("invalid port number");
        }
    }
}
