package controller.handler;

import controller.Controller;
import controller.annotation.Attribute;
import controller.annotation.Label;
import exception.InvalidInstructionsException;
import user.Message;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings({"UnusedDeclaration", "FieldMayBeFinal"})
public class Show extends LoggedInHandler {
    @Label(name = "contacts", description = "show all contacts")
    private boolean showContacts = false;
    @Label(name = "senders", description = "show all users that sent a message")
    private boolean showSenders = false;
    @Label(name = "messages", description = "show all messages")
    private boolean showMessages = false;
    @Label(name = "count", description = "get the count of a collection", required = false)
    private boolean count = false;

    @Attribute(name = "contact", description = "show a saved contact", standalone = true)
    private String showContactUsername = null;
    @Attribute(name = "from", description = "show messages from this contact")
    private String fromUsername = null;

    public Show(Controller controller) {
        super(controller, false);
    }

    public void showContactUsername() throws InvalidInstructionsException {
        if (fromUsername != null)
            throw new InvalidInstructionsException(this);
        controller.printContact(showContactUsername);
    }

    public void showContacts() throws InvalidInstructionsException {
        showSimpleCollection(controller.getUser().getContactList());
    }

    public void showSenders() throws InvalidInstructionsException {
        showSimpleCollection(controller.getUser().getSenders());
    }

    public void showMessages() {
        List<?> messages;
        if (fromUsername != null)
            messages = controller.getUser().getMessages().stream()
                    .filter(message -> message.getUsername().matches(fromUsername))
                    .map(Message::getMessage).collect(Collectors.toList());
        else
            messages = controller.getUser().getMessages();

        showCollectionOrCount(messages);
    }

    public void showSimpleCollection(Collection<?> collection)
            throws InvalidInstructionsException {
        if (fromUsername != null)
            throw new InvalidInstructionsException(this);

        showCollectionOrCount(collection);
    }

    public void showCollectionOrCount(Collection<?> collection) {
        if (count)
            controller.printSizeOfCollection(collection);
        else
            controller.printCollection(collection);
    }
}
