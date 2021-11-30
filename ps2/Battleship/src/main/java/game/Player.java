package game;

import game.board.Board;
import user.User;

public class Player {
    private final User user;
    private final Board board;
    private int shipCount = 0;
    private int points = 0;

    public Player(User user, Board board) {
        this.user = user;
        this.board = board;
    }

    public User getUser() {
        return user;
    }

    public Board getBoard() {
        return board;
    }

    public int getShipCount() {
        return shipCount;
    }

    public int getPoints() {
        return points;
    }

    public void addShip() {
        this.shipCount++;
    }

    public void removeShip() {
        this.shipCount--;
    }

    public void increasePoints(int amount) {
        this.points += amount;
    }

    public void decreasePoints(int amount) {
        this.points -= amount;
    }
}
