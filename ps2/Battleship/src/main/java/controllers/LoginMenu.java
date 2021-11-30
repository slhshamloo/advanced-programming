package controllers;

import user.User;
import user.Userbase;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginMenu extends AbstractController {
    private final Userbase userbase = new Userbase();

    public LoginMenu(Scanner inputStream) {
        super(inputStream, "exit");
    }

    private static void printHelp() {
        System.out.println("register [username] [password]\n" +
                "login [username] [password]\n" +
                "remove [username] [password]\n" +
                "list_users\n" +
                "help\n" +
                "exit");
    }

    private static boolean isUsernameFormatValid(String username) {
        return username.matches("\\w+");
    }

    private static boolean isPasswordFormatValid(String password) {
        return password.matches("\\w+");
    }

    private static boolean isUsernameAndPasswordFormatValid(String username, String password) {
        if (!isUsernameFormatValid(username)) {
            System.out.println("username format is invalid");
            return false;
        }
        else if (!isPasswordFormatValid(password)) {
            System.out.println("password format is invalid");
            return false;
        }
        return true;
    }

    @Override
    protected Map<Pattern, Consumer<Matcher>> createCommandMap() {
        Map<Pattern, Consumer<Matcher>> commandMap = new HashMap<>();

        commandMap.put(Pattern.compile("register (\\S+) (\\S+)"), this::register);
        commandMap.put(Pattern.compile("login (\\S+) (\\S+)"), this::login);
        commandMap.put(Pattern.compile("remove (\\S+) (\\S+)"), this::removeUser);

        return commandMap;
    }

    @Override
    protected Map<String, Runnable> createNoArgumentCommandMap() {
        Map<String, Runnable> noArgumentCommandMap = new HashMap<>();

        noArgumentCommandMap.put("help", LoginMenu::printHelp);
        noArgumentCommandMap.put("list_users", this::printUsernameList);

        return noArgumentCommandMap;
    }

    private void register(Matcher matcher) {
        String username = matcher.group(1);
        String password = matcher.group(2);

        if (isUsernameAndPasswordFormatValid(username, password)) {
            if (userbase.getUserByUsername(username) == null) {
                new User(username, password, userbase);
                System.out.println("register successful");
            } else
                System.out.println("a user exists with this username");
        }
    }

    private void login(Matcher matcher) {
        String username = matcher.group(1);
        String password = matcher.group(2);

        if (isUsernameAndPasswordFormatValid(username, password)) {
            User user = userbase.getUserByUsername(username);
            if (user != null) {
                if (user.isPasswordCorrect(password)) {
                    System.out.println("login successful");
                    Controller mainMenu = new MainMenu(inputStream, user, userbase);
                    mainMenu.run();
                } else
                    System.out.println("incorrect password");
            } else
                System.out.println("no user exists with this username");
        }
    }

    private void removeUser(Matcher matcher) {
        String username = matcher.group(1);
        String password = matcher.group(2);
        
        if (isUsernameAndPasswordFormatValid(username, password)) {
            User user = userbase.getUserByUsername(username);
            if (user != null) {
                if (user.isPasswordCorrect(password)) {
                    userbase.removeUserByUsername(username);
                    System.out.println("removed " + username + " successfully");
                } else
                    System.out.println("incorrect password");
            } else
                System.out.println("no user exists with this username");
        }
    }

    private void printUsernameList() {
        String[] usernames = userbase.getAlphabeticallySortedUsernames();
        if (usernames.length > 0)
            System.out.println(arrayToLineBreakSeparatedString(usernames));
    }

    @Override
    protected void escape() {
        System.out.println("program ended");
    }
}
