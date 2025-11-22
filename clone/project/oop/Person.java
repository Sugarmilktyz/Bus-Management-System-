
package project.oop;

public abstract class Person {
    private String id;
    private String name;
    private String phoneNumber;
    private String address;
    
    public Person (String id, String name, String phoneNumber, String address){
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name=name;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber=phoneNumber;
    }
    
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address=address;
    }
    
    public abstract void displayDetail();
    
    @Override
    public String toString () {
        return "ID: " + id + ", Name: " + name + ", Phone: " + phoneNumber;
    }
}
