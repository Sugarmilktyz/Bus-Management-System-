
package project.oop.service;

import project.oop.Route;
import project.oop.dao.RouteDAO;
import java.util.List;
import java.sql.SQLException;
import project.oop.dao.AssignmentDAO;

public class RouteService {
    private final RouteDAO routeDAO;
    private final AssignmentDAO assignmentDAO;
    
    public RouteService(RouteDAO routeDAO, AssignmentDAO assignmentDAO) {
        this.routeDAO = routeDAO;
        this.assignmentDAO = assignmentDAO;
    }
    
    private boolean isValidRoute(Route route) {
        if (route.getName() == null || route.getName().trim().isEmpty()) {
            System.err.println ("Service Error: Route Name cannot be empty.");
            return false;
        }
        if (route.getStartPoint() == null || route.getStartPoint().trim().isEmpty() || 
            route.getEndPoint() == null || route.getEndPoint().trim().isEmpty()) {
            System.err.println ("Service Error: Start Point and End Point cannot be empty.");
            return false;
        }
        
        if (route.getDistance() <= 0) {
            System.err.println ("Service Error: Distance must be positive.");
            return false;
        }
        return true;
    }
    

    public boolean AddNewRoute(Route route){
        if (!isValidRoute(route)) {
            return false;
        }
        
        String NewId = routeDAO.GetNextRouteId();
        route.setId(NewId);
        
        return routeDAO.Add(route);
    }
    
    public List<Route> GetAllRoutes() {
        return routeDAO.SelectAll();
    }
    
    public Route GetRouteById(String id) {
        if (id == null || id.trim().isEmpty()) {
            System.err.println ("Service Error: Route ID cannot be empty for Find operation.");
            return null;
        }
        return routeDAO.FindById(id);
    }
    
    public boolean UpdateRoute(Route route) {
        if (route.getId() == null || routeDAO.FindById(route.getId()) == null) {
            System.err.println("Service Error: Cannot update. Route ID not found or invalid.");
            return false;
        }
        
        if (!isValidRoute(route)) {
            return false;
        }
        return routeDAO.Update(route);
    }
    
    public boolean RemoveRoute(String id) {
        if (id == null || id.trim().isEmpty()) {
            System.err.println ("Service Error: Route ID cannot be empty for Remove operation.");
            return false;
        }
        try {
            if (assignmentDAO.IsUsedInAssignment(id, "route_id")) { 
                System.err.println("Cannot remove Route ID " + id + ". It has active assignments.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Failed to check Route usage: " + e.getMessage());
            return false;
        }
        
        return routeDAO.Remove(id);
    }
    
    public int GetTotalRoutes() {
        return routeDAO.GetTotalRoutes();
    }
}
