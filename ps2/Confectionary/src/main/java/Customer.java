import java.util.ArrayList;


public class Customer {
    private static ArrayList<Customer> customers = new ArrayList<>();
    private String name;
    private int id;
    private int balance = 0;
    private int discountCode = -1;

    public Customer(String name, int id) {
        this.name = name;
        this.id = id;

        customers.add(this);
    }

    public static Customer getCustomerByID(int id) {
        for (Customer customer : customers)
            if (customer.id == id)
                return customer;
        return null; // customer id not found
    }

    public void increaseCustomerBalance(int balance) {
        this.balance += balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getBalance() {
        return balance;
    }

    public int getID() {
        return id;
    }

    public void setDiscountCode(int discountCode) {
        this.discountCode = discountCode;
    }

    public int getDiscountCode() {
        return discountCode;
    }

    public void decreaseBalance(int balance) {
        this.balance -= balance;
    }
}
