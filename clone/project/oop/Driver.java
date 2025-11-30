
package project.oop;

public class Driver extends Person {
    private String licenseNumber;
    private double salary;
    private int experienceYears;
    
    public Driver() {super();}
    
    public Driver (String id, String name, String phoneNumber, String address, String licenseNumber, double salary, int experienceYears){
        super (id, name, phoneNumber, address);
        this.licenseNumber= licenseNumber;
        this.salary= salary;
        this.experienceYears= experienceYears;
    }
    public Driver (String name, String phoneNumber, String address, String licenseNumber, double salary, int experienceYears){
        this (null, name, phoneNumber, address, licenseNumber, salary, experienceYears);
    }
    
    public String getLicenseNumber (){
        return licenseNumber;
    }
    
    public void setLicenseNumber (String licenseNumber) {
        this.licenseNumber= licenseNumber;
    }
    
    public double getSalary () {
        return salary;
    }
    
    public void setSalary (double salary) {
        this.salary= salary;
    }
    
    public int getExperienceYears (){
        return experienceYears;
    }
    
    public void setExperienceYears (int experienceYears) {
        this.experienceYears= experienceYears;
    }

    @Override
    public void displayDetail () {
        System.out.println ("----Detail of Driver----");
        System.out.println (super.toString());
        System.out.println("Address: " + getAddress());
        System.out.println("License: " + licenseNumber);
        System.out.println("Experience: " + experienceYears + " years");
        System.out.printf("Salary: %.0f VND%n", salary);
    }
    
    public double calculateBonus () {
        return experienceYears > 5 ? salary * 0.05 : 0;
    }
    
    public double calculateBonus (double extraHoursBonus) {
        return calculateBonus() + extraHoursBonus;
    }
    
}
