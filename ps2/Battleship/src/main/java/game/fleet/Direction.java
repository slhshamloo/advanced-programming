package game.fleet;

public enum Direction {
    HORIZONTAL, VERTICAL;

    public static Direction getDirectionFromString(String string) {
        if (string.equals("-h"))
            return HORIZONTAL;
        else if (string.equals("-v"))
            return VERTICAL;
        else
            return null;
    }
}
