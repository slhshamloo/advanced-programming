package controller;

import exception.GameErrorException;

public class SignUpController extends AbstractController {

    public void createUser(String username, String password, String confirmPassword) {
        checkEmptyFields(username, password, confirmPassword);
        if (DATABASE.getUserbase().getUserByUsername(username) != null)
            throw new GameErrorException("User with username " + username + " already exists");
        if (!password.equals(confirmPassword))
            throw new GameErrorException("Passwords didn't match");
        DATABASE.getUserbase().addUser(username, password);
    }

    private void checkEmptyFields(String username, String password, String confirmPassword) {
        if (username.length() == 0)
            throw new GameErrorException("Enter username");
        if (password.length() == 0)
            throw new GameErrorException("Enter password");
        if (confirmPassword.length() == 0)
            throw new GameErrorException("Confirm password");
    }
}
