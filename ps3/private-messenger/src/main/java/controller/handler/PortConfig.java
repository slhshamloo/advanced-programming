package controller.handler;

import connection.ListenRunnable;
import controller.Controller;
import controller.annotation.Attribute;
import controller.annotation.Label;
import exception.InvalidInstructionsException;
import exception.MessengerException;

@SuppressWarnings({"UnusedDeclaration", "FieldMayBeFinal", "FieldCanBeLocal"})
public class PortConfig extends LoggedInHandler {
    @Label(name = "listen", description = "listen on a port")
    private boolean listenOnPort = false;
    @Label(name = "close", description = "close a port")
    private boolean closePort = false;
    @Label(name = "rebind", required = false, description = "listen on a new port")
    private boolean rebind = false;

    @Attribute(name = "port", description = "host port")
    private String portString = null;

    public PortConfig(Controller controller) {
        super(controller, true);
    }

    public void listenOnPort() throws InvalidInstructionsException, MessengerException {
        if (portString == null)
            throw new InvalidInstructionsException(this);
        if (controller.getPort() >= 0 && !rebind)
            throw new MessengerException("the port is already set");

        controller.setPort(Integer.parseInt(portString));
        new Thread(new ListenRunnable(controller)).start();
    }

    public void closePort() throws InvalidInstructionsException, MessengerException {
        if (portString == null)
            throw new InvalidInstructionsException(this);
        if (controller.getPort() != Integer.parseInt(portString))
            throw new MessengerException("the port you specified was not open");

        controller.setPort(-1);
    }
}
