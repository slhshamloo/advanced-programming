import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ProgramController {
    public static Confectionary confectionary = new Confectionary();

    public void run() {
        Scanner in = new Scanner(System.in);
        String input = firstCommand(in); // "end" or the first input after "create confectionary"
        HashMap<String, Consumer<Matcher>> commandMap = generateCommandMap();

        while (!input.equals("end")) {
            if (input.equals("print income"))
                printIncome();
            else if (input.equals("print transactions list"))
                printTransActions();
            else {
                boolean commandWasFound = false;
                for (String command : commandMap.keySet()) {
                    Matcher matcher = getCommandMatcher(input, command);
                    if (matcher.find()) {
                        commandMap.get(command).accept(matcher);
                        commandWasFound = true;
                        break;
                    }
                }
                if (!commandWasFound)
                    System.out.println("invalid command");
            }
            input = in.nextLine().trim();
        }
    }

    private String firstCommand(Scanner in) {
        String input = in.nextLine().trim();
        while (!input.equals("create confectionary")) {
            if (input.equals("end"))
                return "end";
            else {
                System.out.println("invalid command");
                input = in.nextLine().trim();
            }
        }
        return in.nextLine().trim();
    }

    private HashMap<String, Consumer<Matcher>> generateCommandMap() {
        HashMap<String, Consumer<Matcher>> commandMap = new HashMap<>();

        commandMap.put("add customer id ([0-9]+) name (.+)", this::addCustomer);
        commandMap.put("increase balance customer ([0-9]+) amount ([0-9]+)", this::chargeCustomerBalance);
        commandMap.put("add warehouse material (.+) amount ([0-9]+)", this::addWarehouse);
        commandMap.put("increase warehouse material (.+) amount ([0-9]+)",
                this::increaseWarehouseMaterial);
        commandMap.put(
                "add sweet name (.+) price ([0-9]+) materials: ((?:.+ [0-9]+,?)+)",
                this::addSweet);
        commandMap.put("increase sweet (.+) amount ([0-9]+)", this::increaseSweet);
        commandMap.put("add discount code ([0-9]+) price ([0-9]+)", this::addDiscount);
        commandMap.put("add discount code code ([0-9]+) to customer id ([0-9]+)",
                this::addDiscountToCustomer);
        commandMap.put("sell sweet (.+) amount ([0-9]+) to customer ([0-9]+)", this::sellSweet);
        commandMap.put("accept transaction ([0-9]+)", this::acceptTransAction);

        return commandMap;
    }

    private void addCustomer(Matcher matcher) {
        int id = Integer.parseInt(matcher.group(1));
        String name = matcher.group(2);

        if (Customer.getCustomerByID(id) == null)
            new Customer(name, id);
        else
            System.out.println("customer with this id already exists");
    }

    private void chargeCustomerBalance(Matcher matcher) {
        int id = Integer.parseInt(matcher.group(1));
        int amount = Integer.parseInt(matcher.group(2));
        Customer customer = Customer.getCustomerByID(id);

        if (customer == null)
            System.out.println("customer not found");
        else
            customer.increaseCustomerBalance(amount);
    }

    private void addWarehouse(Matcher matcher) {
        String name = matcher.group(1);
        int amount = Integer.parseInt(matcher.group(2));

        if (Warehouse.getWarehouseByName(name) == null)
            new Warehouse(name, amount);
        else
            System.out.println("warehouse having this material already exists");
    }

    private void increaseWarehouseMaterial(Matcher matcher) {
        String name = matcher.group(1);
        int amount = Integer.parseInt(matcher.group(2));
        Warehouse warehouse = Warehouse.getWarehouseByName(name);

        if (warehouse == null)
            System.out.println("warehouse not found");
        else
            warehouse.increaseMaterial(amount);
    }

    private void addSweet(Matcher matcher) {
        String name = matcher.group(1);
        int price = Integer.parseInt(matcher.group(2));
        HashMap<String, Integer> materials = new HashMap<>();
        ArrayList<String> notFoundWarehouses = new ArrayList<>();

        String[] materialNameAndAmounts = matcher.group(3).split(", ");
        for (String material : materialNameAndAmounts) {
            String materialName = material.replaceAll("[0-9]", "").trim();
            int amount = Integer.parseInt(material.replaceAll("[^0-9]", ""));

            if (Warehouse.getWarehouseByName(materialName) == null)
                notFoundWarehouses.add(materialName);
            else
                materials.put(materialName, amount);
        }

        if (notFoundWarehouses.size() > 0)
            System.out.println("not found warehouse(s): "
                    + String.join(" ", notFoundWarehouses));
        else
            new Sweet(name, price, materials);
    }

    private void increaseSweet(Matcher matcher) {
        String name = matcher.group(1);
        int amount = Integer.parseInt(matcher.group(2));
        Sweet sweet = Sweet.getSweetByName(name);
        ArrayList<String> insufficientMaterials = new ArrayList<>();

        if (sweet == null) {
            System.out.println("sweet not found");
            return;
        }

        HashMap<String, Integer> materials = sweet.getMaterials();
        for (String material : materials.keySet())
            if (Warehouse.getWarehouseByName(material).getAmount()
                    < materials.get(material) * amount)
                insufficientMaterials.add(material);

        if (insufficientMaterials.size() > 0)
            System.out.println("insufficient material(s): "
                    + String.join(" ", insufficientMaterials));
        else {
            sweet.decreaseMaterialOfSweetFromWarehouse(amount);
            sweet.increaseSweet(amount);
        }
    }

    private void addDiscount(Matcher matcher) {
        int code = Integer.parseInt(matcher.group(1));
        int price = Integer.parseInt(matcher.group(2));

        if (Confectionary.isDiscountExists(code))
            System.out.println("discount with this code already exists");
        else
            Confectionary.addDiscount(code, price);
    }

    private void addDiscountToCustomer(Matcher matcher) {
        int code = Integer.parseInt(matcher.group(1));
        int id = Integer.parseInt(matcher.group(2));
        Customer customer = Customer.getCustomerByID(id);

        if (!Confectionary.isDiscountExists(code))
            System.out.println("discount code not found");
        else if (customer == null)
            System.out.println("customer not found");
        else
            customer.setDiscountCode(code);
    }

    private void sellSweet(Matcher matcher) {
        String name = matcher.group(1);
        int amount = Integer.parseInt(matcher.group(2));
        int id = Integer.parseInt(matcher.group(3));
        Sweet sweet = Sweet.getSweetByName(name);
        Customer customer = Customer.getCustomerByID(id);

        if (sweet == null)
            System.out.println("sweet not found");
        else if (sweet.getAmount() < amount)
            System.out.println("insufficient sweet");
        else if (customer == null)
            System.out.println("customer not found");
        else {
            int payment = sweet.getPrice() * amount;
            int discountCode = customer.getDiscountCode();
            int discountPrice = Confectionary.getDiscountPriceByCode(discountCode);

            if (customer.getBalance() < payment - discountPrice)
                System.out.println("customer has not enough money");
            else {
                Transaction transaction = new Transaction(id, payment, discountCode);
                sweet.setAmount(sweet.getAmount() - amount);
                customer.setDiscountCode(-1);
                System.out.println("transaction " + transaction.getId() + " successfully created");
            }
        }
    }

    private void acceptTransAction(Matcher matcher) {
        int id = Integer.parseInt(matcher.group(1));
        Transaction transaction = Transaction.getTransactionByID(id);

        if (transaction == null || transaction.isTransactionAccepted())
            System.out.println("no waiting transaction with this id was found");
        else {
            transaction.exchangeMoney();
            confectionary.increaseBalance(transaction.getFinalPayment());
            transaction.setAccepted(true);
        }
    }

    private void printTransActions() {
        for (Transaction transaction : Transaction.getTransactions())
            if (transaction.isTransactionAccepted())
                System.out.println("transaction " + transaction.getId() + ": "
                        + transaction.getCustomerId() + " "
                        + transaction.getAmount() + " "
                        + transaction.getDiscountCode() + " "
                        + transaction.getFinalPayment());
    }

    private void printIncome() {
        System.out.println(confectionary.getBalance());
    }

    private Matcher getCommandMatcher(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(input);
    }
}
