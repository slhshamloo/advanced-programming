package controllers;

import game.Item;
import user.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Shop extends AbstractController {
    private static final Map<String, Item> itemMap = createItemMap();
    private final User user;

    Shop(Scanner inputStream, User user) {
        super(inputStream, "back");
        this.user = user;
    }

    private static Map<String, Item> createItemMap() {
        Map<String, Item> itemMap = new HashMap<>();
        for (Item item : Item.values())
            itemMap.put(item.getName(), item);
        return itemMap;
    }

    private static void printHelp() {
        System.out.println("buy [product] [number]\n" +
                "show-amount\n" +
                "help\n" +
                "back");
    }

    @Override
    protected Map<Pattern, Consumer<Matcher>> createCommandMap() {
        Map<Pattern, Consumer<Matcher>> commandMap = new HashMap<>();

        commandMap.put(Pattern.compile("buy (\\S+) (-?[0-9]+.?[0-9]*)"), this::buyItem);

        return commandMap;
    }

    @Override
    protected Map<String, Runnable> createNoArgumentCommandMap() {
        Map<String, Runnable> noArgumentCommandMap = new HashMap<>();

        noArgumentCommandMap.put("show-amount", this::showCoins);
        noArgumentCommandMap.put("help", Shop::printHelp);

        return noArgumentCommandMap;
    }

    private void buyItem(Matcher matcher) {
        String itemName = matcher.group(1);
        String amountString = matcher.group(2);

        if (!itemMap.containsKey(itemName))
            System.out.println("there is no product with this name");
        else if (!amountString.matches("[0-9]+"))
            System.out.println("invalid number");
        else {
            int amount = Integer.parseInt(amountString);
            Item item = itemMap.get(itemName);
            int itemPrice = item.getPrice();
            int payment = itemPrice * amount;

            if (amount == 0)
                System.out.println("invalid number");
            else if (payment > user.getCoins())
                System.out.println("you don't have enough money");
            else {
                user.addItem(item, amount);
                user.decreaseCoins(payment);
            }
        }
    }

    private void showCoins() {
        System.out.println(user.getCoins());
    }

    @Override
    protected void escape() {
    }
}
