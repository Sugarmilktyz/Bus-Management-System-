package project.oop;
public class Bus {
    private String id;
    private String licensePlate;
    private int capacity;
    private String model; 
    private int purchaseYear; 
    private boolean isActive;
    
    public Bus(String id, String licensePlate, int capacity, String model, int purchaseYear, boolean isActive) {
        this.id = id;
        this.licensePlate = licensePlate;
        this.capacity = capacity;
        this.model = model;
        this.purchaseYear = purchaseYear;
        this.isActive = isActive;
    }
    
    public Bus(String licensePlate, int capacity, String model, int purchaseYear, boolean isActive) {
        this (null, licensePlate, capacity, model, purchaseYear, isActive);
    }

    
    public String getId() {
        return id;
    }   
    public void setId(String id) {
        this.id= id;
    }
    
    public String getLicensePlate() {
        return licensePlate;
    }  
    public void setLicensePlate(String licensePlate) {
        this.licensePlate= licensePlate;
    }
    
    public int getCapacity() {
        return capacity;
    } 
    public void setCapacity(int capacity) {
        this.capacity= capacity;
    }
    
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }
    
    public int getPurchaseYear() {
        return purchaseYear;
    }
    public void setPurchaseYear(int purchaseYear) {
        this.purchaseYear= purchaseYear;
    }
    
    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void displayDetail() {
        String status = isActive ? "ACTIVE" : "INACTIVE";
        System.out.println("----Detail of bus----");
        System.out.println("ID: " + id);
        System.out.println("License plate: " + licensePlate);
        System.out.println("Capacity: " + capacity + " seats");
        System.out.println("Model: " + model);
        System.out.println("Purchase Year: " + purchaseYear);
        System.out.println("Status: " + status);
        System.out.println("------------------------------------");
    }
}