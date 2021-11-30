package controller.handler;

import controller.Controller;
import exception.InvalidInstructionsException;
import exception.MessengerException;

public abstract class LoggedInHandler extends InstructionHandler {
    protected final Controller controller;
    private final boolean printSuccess;

    public LoggedInHandler(Controller controller, boolean printSuccess) {
        this.controller = controller;
        this.printSuccess = printSuccess;
    }

    @Override
    public void handle() throws InvalidInstructionsException, MessengerException {
        if (controller.getUser() == null)
            throw new MessengerException("you must login to access this feature");
        super.handle();
        if (printSuccess)
            Controller.printSuccess();
    }
}
