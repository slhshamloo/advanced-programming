package game.fleet;

public class Ship {
    private final int length;
    private final Direction direction;
    private int health;

    public Ship(int length, Direction direction) {
        this.length = length;
        this.health = length;
        this.direction = direction;
    }

    public int getLength() {
        return length;
    }

    public int getHealth() {
        return health;
    }

    public Direction getDirection() {
        return direction;
    }

    public void damage() {
        health--;
    }
}
