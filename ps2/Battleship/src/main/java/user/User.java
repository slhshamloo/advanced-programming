package user;

import game.Item;

import java.util.EnumMap;
import java.util.Map;

public class User {
    private final String username;
    private final String password;
    private final Map<Item, Integer> items = createItemMap();
    private int score = 0;
    private int wins = 0;
    private int draws = 0;
    private int losses = 0;
    private int coins = 50;

    public User(String username, String password, Userbase userbase) {
        this.username = username;
        this.password = password;

        userbase.addUser(this);
    }

    private static Map<Item, Integer> createItemMap() {
        Map<Item, Integer> items = new EnumMap<>(Item.class);
        for (Item item : Item.values())
            items.put(item, 0);
        return items;
    }

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }

    public int getWins() {
        return wins;
    }

    public void submitWin(boolean isGameConceded) {
        this.wins += 1;
        if (isGameConceded)
            this.score += 2;
        else
            this.score += 3;
    }

    public int getDraws() {
        return draws;
    }

    public void submitDraw() {
        this.draws += 1;
        this.score += 1;
    }

    public int getLosses() {
        return losses;
    }

    public void submitLoss(boolean isGameConceded) {
        this.losses += 1;
        if (isGameConceded)
            this.score -= 1;
    }

    public int getCoins() {
        return coins;
    }

    public void increaseCoins(int coins) {
        this.coins += coins;
    }

    public void decreaseCoins(int coins) {
        this.coins -= coins;
    }

    public boolean isPasswordCorrect(String password) {
        return this.password.equals(password);
    }

    public int getItemAmount(Item item) {
        return items.get(item);
    }

    public void addItem(Item item, int amount) {
        int prevAmount = items.get(item);
        items.put(item, prevAmount + amount);
    }

    public void useItem(Item item) {
        int prevAmount = items.get(item);
        items.put(item, prevAmount - 1);
    }

    @Override
    public String toString() {
        return username + " " + score + " " + wins + " " + draws + " " + losses;
    }
}
