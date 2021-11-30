package game;

public enum Item {
    MINE("mine", 1),
    SCAN("scanner", 9),
    AIRPLANE("airplane", 10),
    INVISIBLE("invisible", 20),
    ANTIAIR("antiaircraft", 30);

    private final String name;
    private final int price;

    Item(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }
}

