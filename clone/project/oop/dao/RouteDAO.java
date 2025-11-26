
package project.oop.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import project.oop.Route;
import project.oop.utils.JDBCUtils;
import project.oop.utils.ConnectionManager;

public class RouteDAO {
    private Connection connection;
    public RouteDAO (Connection connection){
        this.connection= connection;
    }
    private static final String INSERT_ROUTE_SQL = "INSERT INTO route (id, name, start_point, end_point, distance) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_ROUTE_BY_ID = "SELECT * FROM route WHERE id = ?";
    private static final String SELECT_ALL_ROUTES = "SELECT * FROM route";
    private static final String DELETE_ROUTE_SQL = "DELETE FROM route WHERE id = ?";
    private static final String UPDATE_DISTANCE_SQL = "UPDATE route SET distance = ? WHERE id = ?"; // Thay thế update Salary/Capacity
    private static final String COUNT_ROUTES_SQL = "SELECT COUNT(*) FROM route";

    
    public void AddRoute (Route route) {
        if (FindRouteById(route.getId()) != null) {
            System.err.println("Error, this ID route" + route.getId() + " already exist");
            return;
        }
        System.out.println (INSERT_ROUTE_SQL);
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_ROUTE_SQL)
                ) {
            preparedStatement.setString(1, route.getId());
            preparedStatement.setString(2, route.getName());
            preparedStatement.setString(3, route.getStartPoint());
            preparedStatement.setString(4, route.getEndPoint());
            preparedStatement.setDouble(5, route.getDistance());
            
            preparedStatement.executeUpdate();
            System.out.println("Route " + route.getId() + " added successfully name:" + route.getName());
        } catch (SQLException e) {
            System.err.println ("Error when adding Route into the Database: " +e.getMessage());
        }
    }
    
    public Route FindRouteById(String id) {
        Route route = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ROUTE_BY_ID)){
            preparedStatement.setString(1,id);
            
            ResultSet rs = preparedStatement.executeQuery();
            
            if (rs.next()) {
                    String name = rs.getString("name");
                    String startPoint = rs.getString("start_point");
                    String endPoint = rs.getString("end_point");
                    double distance = rs.getDouble("distance");
                
                    route = new Route(id, name, startPoint, endPoint, distance);
            }
        } catch (SQLException e) {
            System.err.println("Error when finding Route" + e.getMessage());
        }
        return route;
    }
    
    public List<Route> ListAllRoutes() {
        List<Route> routeList = new ArrayList<>();
        
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_ROUTES)) {
            
            ResultSet rs = preparedStatement.executeQuery();
            
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String startPoint = rs.getString("start_point");
                String endPoint = rs.getString("end_point");
                double distance = rs.getDouble("distance");
                
                Route route = new Route(id, name, startPoint, endPoint, distance);
                routeList.add(route);
            }
        } catch (SQLException e) {
            System.err.println ("Error when loading Route list: "+ e.getMessage());
        }
        return routeList;
    }
    
    public int GetTotalRoutes() {
        int count = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(COUNT_ROUTES_SQL);
                ResultSet rs = preparedStatement.executeQuery()){
           if (rs.next()) {
               count = rs.getInt(1);
           }
        }catch (SQLException e) {
            System.err.println ("Error when calculating total of Route: "+ e.getMessage());
        }
        return count;
    }
    
    public boolean updateRouteDistance(String id, double newDistance) {
        boolean rowUpdated = false;
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_DISTANCE_SQL)) {

            preparedStatement.setDouble(1, newDistance);
            preparedStatement.setString(2, id);

            rowUpdated = preparedStatement.executeUpdate() > 0;

            if (rowUpdated) {
                System.out.println("Update Distance successfull for Route:" + id + " to " + newDistance + " km.");
            } else {

                System.err.println("Cannot find " + id + " to update distance");
            }
        } catch (SQLException e) {
            System.err.println("Error when updating  Route's distance: " + e.getMessage());
        }
        return rowUpdated;
    }
    
    public boolean RemoveRoute (String id) {
        boolean rowDeleted = false;
         try (PreparedStatement statement = connection.prepareStatement(DELETE_ROUTE_SQL)) {
            
             statement.setString (1, id);
             rowDeleted = statement.executeUpdate() > 0;
             
             if (rowDeleted) {
                 System.out.println ("Delete ID" + id +" Successful.");
             } else {
                 System.err.println ("Cannot find this ID" + id + " To Delete");
             }
         } catch (SQLException e) {
             System.err.println("Error when delete Route" + e.getMessage());
         }
         return rowDeleted;
    }
}
