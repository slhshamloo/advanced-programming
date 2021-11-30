package controllers;

import user.User;
import user.Userbase;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainMenu extends AbstractController {
    private final User user;
    private final Userbase userbase;

    public MainMenu(Scanner inputStream, User user, Userbase userbase) {
        super(inputStream, "logout");
        this.user = user;
        this.userbase = userbase;
    }

    private static void printHelp() {
        System.out.println("new_game [username]\n" +
                "scoreboard\n" +
                "list_users\n" +
                "shop\n" +
                "help\n" +
                "logout");
    }

    @Override
    protected Map<Pattern, Consumer<Matcher>> createCommandMap() {
        Map<Pattern, Consumer<Matcher>> commandMap = new HashMap<>();

        commandMap.put(Pattern.compile("new_game (\\S+)"), this::playGame);

        return commandMap;
    }

    @Override
    protected Map<String, Runnable> createNoArgumentCommandMap() {
        Map<String, Runnable> noArgumentCommandMap = new HashMap<>();

        noArgumentCommandMap.put("scoreboard", this::printScoreboard);
        noArgumentCommandMap.put("list_users", this::printUsernameList);
        noArgumentCommandMap.put("shop", this::openShop);
        noArgumentCommandMap.put("help", MainMenu::printHelp);

        return noArgumentCommandMap;
    }

    private void playGame(Matcher matcher) {
        String username = matcher.group(1);
        User secondUser = userbase.getUserByUsername(username);

        if (!username.matches("\\w+"))
            System.out.println("username format is invalid");
        else if (username.equals(user.getUsername()))
            System.out.println("you must choose another player to start a game");
        else if (secondUser == null)
            System.out.println("no user exists with this username");
        else {
            System.out.println("new game started successfully between "
                    + user.getUsername() + " and " + username);
            Controller gameController = new GameController(inputStream, user, secondUser);
            gameController.run();
        }
    }

    private void printScoreboard() {
        User[] users = userbase.getUsersSortedByScoreboard();
        if (users.length > 0)
            System.out.println(arrayToLineBreakSeparatedString(users));
    }

    private void printUsernameList() {
        String[] usernames = userbase.getAlphabeticallySortedUsernames();
        if (usernames.length > 0)
            System.out.println(arrayToLineBreakSeparatedString(usernames));
    }

    private void openShop() {
        Controller shop = new Shop(inputStream, user);
        shop.run();
    }

    @Override
    protected void escape() {
        System.out.println("logout successful");
    }
}
