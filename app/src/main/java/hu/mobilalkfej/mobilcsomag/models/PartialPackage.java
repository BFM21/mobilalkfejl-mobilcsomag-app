package hu.mobilalkfej.mobilcsomag.models;

public class PartialPackage {

    private String id;
    private String name;
    private String description;
    private int amount;

    private boolean monthly;
    private int price;

    private boolean selected;

    public PartialPackage(String name, String description, int amount, int price) {
        this.name = name;
        this.description = description;
        this.amount = amount;
        this.price = price;
        this.selected = false;
    }

    public PartialPackage() {

    }

    public boolean isMonthly() {
        return monthly;
    }

    public void setMonthly(boolean monthly) {
        this.monthly = monthly;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
