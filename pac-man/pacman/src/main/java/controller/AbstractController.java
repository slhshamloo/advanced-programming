package controller;

import model.user.User;
import model.user.database.Database;

import java.io.IOException;

public abstract class AbstractController {

    protected static final Database DATABASE = new Database();
    protected static User user;

    public static void closeApplication() throws IOException {
        DATABASE.saveData();
        System.exit(0);
    }

    public static void saveData() throws IOException {
        DATABASE.saveData();
    }

    public static void logoutUser() {
        user = null;
    }
}
