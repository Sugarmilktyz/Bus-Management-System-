package project.oop.service;

import project.oop.Driver;
import project.oop.dao.DriverDAO;
import java.util.List;
import java.sql.SQLException;
import project.oop.dao.AssignmentDAO;

public class DriverService {
    private final DriverDAO driverDAO;
    private final AssignmentDAO assignmentDAO;
    
    public DriverService(DriverDAO driverDAO, AssignmentDAO assignmentDAO) {
        this.driverDAO = driverDAO;
        this.assignmentDAO = assignmentDAO;
    }
    
    private boolean IsValidDriver(Driver driver) {
        if (driver.getName() == null || driver.getName().trim().isEmpty()) {
            System.err.println ("Service Error: Driver Name cannot be empty.");
            return false;
        }
        if (driver.getPhoneNumber() == null || driver.getPhoneNumber().trim().isEmpty() || !driver.getPhoneNumber().matches("\\d{10,11}")) {
            System.err.println ("Service Error: Phone Number must be 10-11 digits.");
            return false;
        }
        if (driver.getAddress() == null || driver.getAddress().trim().isEmpty()) {
            System.err.println ("Service Error: Address cannot be empty.");
            return false;
        }
        if (driver.getLicenseNumber() == null || driver.getLicenseNumber().trim().isEmpty()) {
            System.err.println ("Service Error: License Number cannot be empty.");
            return false;
        }
        
        if (driver.getSalary() <= 0) {
            System.err.println ("Service Error: Salary must be positive.");
            return false;
        }
        if (driver.getExperienceYears() < 0) {
            System.err.println ("Service Error: Experience Years cannot be negative.");
            return false;
        }
        return true;
    }
    
    public boolean AddNewDriver(Driver driver){
        if (!IsValidDriver(driver)) {
            return false;
        }
        
        String NewId = driverDAO.GetNextDriverId();
        driver.setId(NewId);
        
        return driverDAO.Add(driver);
    }
    
    public List<Driver> GetAllDrivers() {
        return driverDAO.SelectAll();
    }
    
    public Driver GetDriverById(String id) {
        if (id == null || id.trim().isEmpty()) {
            System.err.println ("Service Error: Driver ID cannot be empty for Find operation.");
            return null;
        }
        return driverDAO.FindById(id);
    }
    
    public boolean UpdateDriver(Driver driver) {
        if (driver.getId() == null || driverDAO.FindById(driver.getId()) == null) {
            System.err.println("Service Error: Cannot update. Driver ID not found or invalid.");
            return false;
        }
        
        if (!IsValidDriver(driver)) {
            return false;
        }
        return driverDAO.Update(driver);
    }
    
    public boolean RemoveDriver(String id) {
        if (id == null || id.trim().isEmpty()) {
            System.err.println ("Service Error: Driver ID cannot be empty for Remove operation.");
            return false;
        }
        try {
            if (assignmentDAO.IsUsedInAssignment(id, "driver_id")) {
                System.err.println("Cannot remove Driver ID " + id + ". They are currently assigned to one or more routes.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Failed to check Driver usage: " + e.getMessage());
            return false;
        }
        return driverDAO.Remove(id);
    }
    
    public int GetTotalDrivers() {
        return driverDAO.GetTotalDrivers();
    }
}
