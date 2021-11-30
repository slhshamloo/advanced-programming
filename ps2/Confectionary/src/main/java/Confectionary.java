import java.util.HashMap;


public class Confectionary {
    private static HashMap<Integer, Integer> discounts = new HashMap<>();
    private int balance = 0;

    public Confectionary() {
        discounts.put(-1, 0);
    }

    public static boolean isDiscountExists(int code) {
        return discounts.containsKey(code);
    }

    public static void addDiscount(int code, int price) {
        discounts.put(code, price);
    }

    public static int getDiscountPriceByCode(int code) {
        return discounts.get(code);
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void increaseBalance(int balance) {
        this.balance += balance;
    }
}
