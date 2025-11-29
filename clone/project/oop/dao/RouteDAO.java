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

public class RouteDAO implements CrudDAO<Route> {
    private Connection connection;
    public RouteDAO (Connection connection){
        this.connection= connection;
    }
    private static final String INSERT_ROUTE_SQL = "INSERT INTO route (id, name, start_point, end_point, distance) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_ROUTE_BY_ID = "SELECT * FROM route WHERE id = ?";
    private static final String SELECT_ALL_ROUTES = "SELECT * FROM route";
    private static final String DELETE_ROUTE_SQL = "DELETE FROM route WHERE id = ?";
    private static final String UPDATE_ROUTE_SQL = "UPDATE route SET name=?, start_point=?, end_point=?, distance=? WHERE id = ?";
    private static final String COUNT_ROUTES_SQL = "SELECT COUNT(*) FROM route";
    private static final String GET_LAST_ID_SQL = "SELECT id FROM route ORDER BY id DESC LIMIT 1";

    public String GetNextRouteId(){
        try(PreparedStatement preparedStatement= connection.prepareStatement(GET_LAST_ID_SQL);
            ResultSet rs = preparedStatement.executeQuery()){
                if (rs.next()){
                    String LastId= rs.getString("id");
                    int number = Integer.parseInt(LastId.substring(1));
                    number++;
                    
                    return String.format("R%03d", number);
                }
        } catch (SQLException e){
            System.err.println("Error when generating route ID:"+ e.getMessage());
        }
        return "R001";
    }
    
@Override    
    public boolean Add (Route route) {
        System.out.println (INSERT_ROUTE_SQL);
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_ROUTE_SQL)
                ) {
            preparedStatement.setString(1, route.getId());
            preparedStatement.setString(2, route.getName());
            preparedStatement.setString(3, route.getStartPoint());
            preparedStatement.setString(4, route.getEndPoint());
            preparedStatement.setDouble(5, route.getDistance());
            
            return preparedStatement.executeUpdate()>0;

        } catch (SQLException e) {
            System.err.println ("Error when adding Route into the Database: " +e.getMessage());
            return false;
        }
    }
  
@Override
    public Route FindById(String id) {
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

@Override
    public List<Route> SelectAll() {
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
    
@Override
    public boolean Update(Route route) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ROUTE_SQL)) {
            preparedStatement.setString(1, route.getName());
            preparedStatement.setString(2, route.getStartPoint());
            preparedStatement.setString(3, route.getEndPoint());
            preparedStatement.setDouble(4, route.getDistance());
            preparedStatement.setString(5, route.getId());
            
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error when performing full update Route: " + e.getMessage());
            return false;
        }
    }

@Override
    public boolean Remove (String id) {
         try (PreparedStatement statement = connection.prepareStatement(DELETE_ROUTE_SQL)) {
            
             statement.setString (1, id);
             return statement.executeUpdate() > 0;
         } catch (SQLException e) {
             System.err.println("Error when delete Route" + e.getMessage());
             return false;
         }
    }
}
