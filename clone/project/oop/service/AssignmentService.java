package project.oop.service;

import project.oop.Assignment;
import project.oop.Bus;
import project.oop.dao.AssignmentDAO;
import project.oop.dao.BusDAO;
import project.oop.dao.DriverDAO;
import project.oop.dao.RouteDAO;
import java.sql.SQLException;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class AssignmentService {
    private final AssignmentDAO assignmentDAO;
    private final BusDAO busDAO;
    private final DriverDAO driverDAO;
    private final RouteDAO routeDAO;

    public AssignmentService(AssignmentDAO assignmentDAO, BusDAO busDAO, DriverDAO driverDAO, RouteDAO routeDAO) {
        this.assignmentDAO = assignmentDAO;
        this.busDAO = busDAO;
        this.driverDAO = driverDAO;
        this.routeDAO = routeDAO;
    }

    public boolean IsBusActive(String busId) {
        Bus bus = busDAO.FindById(busId);
        
        if (bus== null) {
            System.err.println ("Bus ID " + busId + " not found or database error.");
            return false;
        }
        return bus.isActive();
    }
    
    public boolean IsAssignmentOverlapped(String driverId, String busId, String date, String shift) {
        try {
            return assignmentDAO.IsAssignmentOverlapped(driverId, busId, date, shift);
        } catch (SQLException e) {
            System.err.println ("Failed to check assignment overlap: " + e.getMessage());
            return true;
        }
    }

    private boolean IsValidAssignment(Assignment assignment) {
        if (driverDAO.FindById(assignment.getDriverId()) == null) {
            System.err.println ("Driver ID " + assignment.getDriverId() + " does not exist.");
            return false;
        }
        if (busDAO.FindById(assignment.getBusId()) == null) {
            System.err.println ("Bus ID " + assignment.getBusId() + " does not exist.");
            return false;
        }
        if (routeDAO.FindById(assignment.getRouteId()) == null) {
            System.err.println ("Route ID " + assignment.getRouteId() + " does not exist.");
            return false;
        }
        
        if (assignment.getAssignmentDate() == null || assignment.getShift() == null || assignment.getShift().trim().isEmpty()) {
            System.err.println ("Date and Shift cannot be empty.");
            return false;
        }
        try {
             LocalDate.parse(assignment.getAssignmentDate());
        } catch (DateTimeParseException e) {
             System.err.println ("Wrong date format. Must be YYYY-MM-DD.");
             return false;
        }
        
        if (!IsBusActive(assignment.getBusId())) {
            System.err.println ("Cannot assign an Inactive Bus.");
            return false;
        }

        if (IsAssignmentOverlapped(assignment.getDriverId(), assignment.getBusId(), assignment.getAssignmentDate(), assignment.getShift())) {
            System.err.println("Driver or Bus is already assigned for this Date and Shift.");
            return false;
        }

        return true;
    }

    public boolean AddNewAssignment(Assignment assignment) {
        if (!IsValidAssignment(assignment)) {
            return false;
        }
        
        String newId = assignmentDAO.GetNextAssignmentId();
        assignment.setId(newId);
        return assignmentDAO.Add(assignment);
    }
    
    public List<Assignment> GetAllAssignments() {
        return assignmentDAO.SelectAll();
    }
    
    public Assignment GetAssignmentById(String id) {
        if (id == null || id.trim().isEmpty()) {
             System.err.println ("Assignment ID cannot be empty for Find operation.");
             return null;
        }
        return assignmentDAO.FindById(id);
    }
    
    public boolean UpdateAssignment(Assignment assignment) {
        if (assignment.getId()== null ||assignmentDAO.FindById(assignment.getId())== null) {
            System.err.println("Cannot update. Assignment ID not found or invalid.");
            return false;
        }
        
        if (!IsValidAssignment(assignment)) {
            return false;
        }

        return assignmentDAO.Update(assignment);
    }

    public boolean RemoveAssignment(String id) {
        if (id== null ||id.trim().isEmpty()) {
            System.err.println ("Assignment ID cannot be empty for Remove function.");
            return false;
        }
        return assignmentDAO.Remove(id);
    }
    public int GetTotalAssignments() {
        return assignmentDAO.GetTotalAssignments();
    }
    
    public List<Vector<String>> GetReportDataForGUI() {
        return assignmentDAO.GetReportDataForGUI();
    }
}
