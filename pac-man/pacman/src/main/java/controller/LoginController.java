package controller;

import exception.GameErrorException;
import model.user.User;

public class LoginController extends AbstractController {

    public void login(String username, String password) {
        User user = DATABASE.getUserbase().getUserByUsername(username);

        if (user == null || !user.isPasswordCorrect(password))
            throw new GameErrorException("Username and password didn't match");
        AbstractController.user = user;
    }
}
