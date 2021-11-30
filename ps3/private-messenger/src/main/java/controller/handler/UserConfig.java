package controller.handler;

import controller.Controller;
import controller.annotation.Attribute;
import controller.annotation.Label;
import exception.InvalidInstructionsException;
import exception.MessengerException;
import user.User;

@SuppressWarnings({"UnusedDeclaration", "FieldMayBeFinal"})
public class UserConfig extends InstructionHandler {
    private final Controller controller;

    @Label(name = "create", description = "create a user with username and password.")
    private boolean createUser = false;
    @Label(name = "login", description = "login with username and password")
    private boolean login = false;
    @Label(name = "logout", description = "logout (accessible after login)")
    private boolean logout = false;

    @Attribute(name = "username", description = "your username")
    private String username = null;
    @Attribute(name = "password", description = "your password")
    private String password = null;

    public UserConfig(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void handle() throws InvalidInstructionsException, MessengerException {
        super.handle();
        Controller.printSuccess();
    }

    public void createUser() throws InvalidInstructionsException, MessengerException {
        if (username == null || password == null)
            throw new InvalidInstructionsException(this);
        if (controller.getUserbase().getUserByUsername(username) != null
                || !username.matches("[-\\w]+"))
            throw new MessengerException("this username is unavailable");

        controller.getUserbase().addUser(username, password);
    }

    public void login() throws InvalidInstructionsException, MessengerException {
        if (username == null || password == null)
            throw new InvalidInstructionsException(this);

        User user = controller.getUserbase().getUserByUsername(username);
        if (user == null)
            throw new MessengerException("user not found");
        if (user.isPasswordCorrect(password))
            controller.setUser(controller.getUserbase().getUserByUsername(username));
        else
            throw new MessengerException("incorrect password");
    }

    public void logout() throws InvalidInstructionsException, MessengerException {
        if (username != null || password != null)
            throw new InvalidInstructionsException(this);
        if (controller.getUser() == null)
            throw new MessengerException("you must login before logging out");

        controller.setUser(null);
        controller.setPort(-1);
    }
}
