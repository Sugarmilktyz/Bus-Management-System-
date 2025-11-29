
package project.oop.service;

import project.oop.Bus;
import project.oop.dao.BusDAO;
import java.time.Year;
import java.util.List;
import project.oop.dao.AssignmentDAO;
import java.sql.SQLException;

public class BusService {
    private final BusDAO busDAO;
    private final AssignmentDAO assignmentDAO;
    
    public BusService(BusDAO busDAO, AssignmentDAO assignmentDAO) {
        this.busDAO= busDAO;
        this.assignmentDAO = assignmentDAO;
    }
    
    private boolean IsValidBus(Bus bus){
        if (bus.getLicensePlate()== null|| bus.getLicensePlate().trim().isEmpty()){
            System.err.println ("Service Error: License Plate cannot be empty.");
            return false;
        }
        
        if (bus.getModel() == null || bus.getModel().trim().isEmpty()) {
            System.err.println ("Service Error: Model cannot be empty.");
            return false;
        }
        
        int currentYear = Year.now().getValue();
        if (bus.getPurchaseYear() <= 0 || bus.getPurchaseYear() > currentYear) {
            System.err.println ("Service Error: Purchase year must be positive and not in the future.");
            return false;
        }
        
        if (bus.getCapacity() <= 0 || bus.getCapacity() > 100) {
            System.err.println ("Service Error: Capacity must be between 1 and 100.");
            return false;
        }
        return true;
        
    }
    
    public boolean AddNewBus(Bus bus){ 
        if (!IsValidBus(bus)) {
            return false;
        }
        
        String NewId= busDAO.GetNextBusId();
        bus.setId(NewId);
        return busDAO.Add(bus);
    }
    
    public List<Bus> GetAllBuses() {
        return busDAO.SelectAll();
    }
    
    public Bus GetBusById(String id) {
        if (id == null || id.trim().isEmpty()) {
            System.err.println ("Service Error: Bus ID cannot be empty for Find operation.");
            return null;
        }
        return busDAO.FindById(id);
    }
    
    public boolean UpdateBus(Bus bus) {
        if (bus.getId() == null || busDAO.FindById(bus.getId()) == null) {
            System.err.println("Service Error: Cannot update. Bus ID not found or invalid.");
            return false;
        }    
        if (!IsValidBus(bus)) {
            return false;
        }
        return busDAO.Update(bus);
    }
    
    public boolean RemoveBus(String id) {
        if (id==null || id.isEmpty()){
            System.err.println ("Service Error: Bus ID cannot be empty for Remove function.");
            return false;
        }
        try {
            if (assignmentDAO.IsUsedInAssignment(id, "bus_id")) { 
                System.err.println("Cannot remove Bus ID " + id + ". It is currently assigned to one or more routes.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Failed to check Bus usage: " + e.getMessage());
            return false;
        }
        return busDAO.Remove(id);
    }
    public int GetTotalBuses() {
        return busDAO.GetTotalBuses();
    }
}
