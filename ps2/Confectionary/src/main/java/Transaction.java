import java.util.ArrayList;


public class Transaction {
    private static int idCounter = 1;
    private static ArrayList<Transaction> transactions = new ArrayList<>();
    private int id;
    private int customerId;
    private int amount;
    private int discountCode;
    private int discountPrice;
    private int finalPayment;
    private boolean isAccepted;

    public Transaction(int customerId, int amount, int discountCode) {
        this.customerId = customerId;
        this.amount = amount;
        this.discountCode = discountCode;
        this.discountPrice = Confectionary.getDiscountPriceByCode(discountCode);

        if (amount > discountPrice)
            this.finalPayment = amount - discountPrice;
        else
            this.finalPayment = 0;

        this.id = idCounter++;

        transactions.add(this);
    }

    public static Transaction getTransactionByID(int id) {
        for (Transaction transaction : transactions)
            if (transaction.id == id)
                return transaction;
        return null;
    }

    public static ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public int getId() {
        return id;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }

    public void exchangeMoney() {
        Customer customer = Customer.getCustomerByID(customerId);
        customer.decreaseBalance(finalPayment);
    }

    public boolean isTransactionAccepted() {
        return isAccepted;
    }

    public int getDiscountCode() {
        return discountCode;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getAmount() {
        return amount;
    }

    public int getFinalPayment() {
        return finalPayment;
    }
}
