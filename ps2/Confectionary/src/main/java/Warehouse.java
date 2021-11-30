import java.util.ArrayList;


public class Warehouse {
    private static ArrayList<Warehouse> warehouses = new ArrayList<>();
    private int amount;
    private String materialName;

    public Warehouse(String materialName, int amount) {
        this.materialName = materialName;
        this.amount = amount;

        warehouses.add(this);
    }

    public static Warehouse getWarehouseByName(String name) {
        for (Warehouse warehouse : warehouses)
            if (warehouse.materialName.equals(name))
                return warehouse;
        return null; // warehouse not found
    }

    public void increaseMaterial(int amount) {
        this.amount += amount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void decreaseMaterial(int amount) {
        this.amount -= amount;
    }
}
