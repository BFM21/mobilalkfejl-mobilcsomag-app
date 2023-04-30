package hu.mobilalkfej.mobilcsomag.models;

public class MobilePackage {

    private String id;
    private String name;
    private String description;

    private PartialPackage internet;
    private PartialPackage call;
    private PartialPackage message;

    private boolean monthly;
    private int price;

    private boolean custom;



    public MobilePackage(String name, String description, int price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public MobilePackage(PartialPackage internet, PartialPackage call, PartialPackage message, int price) {
        this.internet = internet;
        this.call = call;
        this.message = message;
        this.price = price;
    }

    public MobilePackage() {

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

    public PartialPackage getInternet() {
        return internet;
    }

    public void setInternet(PartialPackage internet) {
        this.internet = internet;
    }

    public PartialPackage getCall() {
        return call;
    }

    public void setCall(PartialPackage call) {
        this.call = call;
    }

    public PartialPackage getMessage() {
        return message;
    }

    public void setMessage(PartialPackage message) {
        this.message = message;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isCustom() {
        return custom;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }
}
