import java.util.ArrayList;
import java.util.HashMap;


public class Sweet {
    private static ArrayList<Sweet> sweets = new ArrayList<>();
    private String name;
    private int price;
    private int amount = 0;
    private HashMap<String, Integer> materials;

    Sweet(String name, int price, HashMap<String, Integer> materials) {
        this.name = name;
        this.price = price;
        this.materials = materials;

        sweets.add(this);
    }

    public static Sweet getSweetByName(String name) {
        for (Sweet sweet : sweets)
            if (sweet.name.equals(name))
                return sweet;
        return null;
    }

    public HashMap<String, Integer> getMaterials() {
        return materials;
    }

    public String getName() {
        return name;
    }

    public void increaseSweet(int amount) {
        this.amount += amount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getPrice() {
        return price;
    }

    public void decreaseMaterialOfSweetFromWarehouse(int amount) {
        for (String material : materials.keySet()) {
            Warehouse warehouse = Warehouse.getWarehouseByName(material);
            warehouse.decreaseMaterial(materials.get(material) * amount);
        }
    }
}
