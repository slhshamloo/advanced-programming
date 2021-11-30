package game.fleet;

public class Antiair {
    private final int startRow;
    private final int width;
    private final Direction direction;

    public Antiair(int startRow, int width, Direction direction) {
        this.startRow = startRow;
        this.width = width;
        this.direction = direction;
    }

    public int getStartRow() {
        return startRow;
    }

    public int getWidth() {
        return width;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean coversArea(int xCoordinate, int yCoordinate,
            int xAreaLength, int yAreaLength) {
        if (direction == Direction.HORIZONTAL)
            return yCoordinate <= startRow + width - 1
                    && yCoordinate + yAreaLength - 1 >= startRow;
        else if (direction == Direction.VERTICAL)
            return xCoordinate <= startRow + width - 1
                    && xCoordinate + xAreaLength - 1 >= startRow;
        return false;
    }
}
