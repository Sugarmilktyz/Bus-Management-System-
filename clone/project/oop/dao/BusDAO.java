
package project.oop.dao;

import project.oop.Bus;
import project.oop.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BusDAO {
        private static final String INSERT_BUS_SQL = "INSERT INTO bus (id, license_plate, capacity, model, purchase_year, is_active) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SELECT_BUS_BY_ID = "SELECT * FROM bus WHERE id = ?";
    private static final String SELECT_ALL_BUSES = "SELECT * FROM bus";
    private static final String DELETE_BUS_SQL = "DELETE FROM bus WHERE id = ?";
    private static final String UPDATE_CAPACITY_SQL = "UPDATE bus SET capacity = ? WHERE id = ?";
    private static final String UPDATE_STATUS_SQL = "UPDATE bus SET is_active = ? WHERE id = ?";
    private static final String COUNT_BUSES_SQL = "SELECT COUNT(*) FROM bus";
    
    public void AddBus (Bus bus) {
        if (FindBusById(bus.getId()) != null) {
            System.err.println("Error, this ID bus" + bus.getId() + " already exist");
            return;
        }
        System.out.println (INSERT_BUS_SQL);
        try (Connection connection = JDBCUtils.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(INSERT_BUS_SQL)
                ) {
            preparedStatement.setString(1, bus.getId());
            preparedStatement.setString(2, bus.getLicensePlate());
            preparedStatement.setInt(3, bus.getCapacity());
            preparedStatement.setString(4, bus.getModel());
            preparedStatement.setInt(5, bus.getPurchaseYear());
            preparedStatement.setBoolean(6, bus.isActive()); 
            
            preparedStatement.executeUpdate();
            System.out.println("Bus " + bus.getId() + " added successfully model:" + bus.getModel());
        } catch (SQLException e) {
            System.err.println ("Error when adding Bus into the Database: " +e.getMessage());
        }
    }
    
    public Bus FindBusById(String id) {
        Bus bus = null;
        try (Connection connection = JDBCUtils.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BUS_BY_ID)){
            preparedStatement.setString(1,id);
            
            ResultSet rs = preparedStatement.executeQuery();
            
            if (rs.next()) {
                    String licensePlate = rs.getString("license_plate");
                    int capacity = rs.getInt("capacity");
                    String model = rs.getString("model");
                    int purchaseYear = rs.getInt("purchase_year");
                    boolean isActive = rs.getBoolean("is_active");
                
                    bus = new Bus(id, licensePlate, capacity, model, purchaseYear, isActive);
            }
        } catch (SQLException e) {
            System.err.println("Error when finding Bus" + e.getMessage());
        }
        return bus;
    }
    
    public List<Bus> ListAllBuses() {
        List<Bus> busList = new ArrayList<>();
        
        try (Connection connection = JDBCUtils.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_BUSES)) {
            
            ResultSet rs = preparedStatement.executeQuery();
            
            while (rs.next()) {
                String id = rs.getString("id");
                String licensePlate = rs.getString("license_plate");
                int capacity = rs.getInt("capacity");
                String model = rs.getString("model");
                int purchaseYear = rs.getInt("purchase_year");
                boolean isActive = rs.getBoolean("is_active");
                
                Bus bus = new Bus(id, licensePlate, capacity, model, purchaseYear, isActive);
                busList.add(bus);
            }
        } catch (SQLException e) {
            System.err.println ("Error when loading Bus list: "+ e.getMessage());
        }
        return busList;
    }
    
    public int GetTotalBuses() {
        int count = 0;
        try (Connection connection = JDBCUtils.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(COUNT_BUSES_SQL);
                ResultSet rs = preparedStatement.executeQuery()){
           if (rs.next()) {
               count = rs.getInt(1);
           }
        }catch (SQLException e) {
            System.err.println ("Error when calculating total of Bus: "+ e.getMessage());
        }
        return count;
    }
    
    public boolean updateBusCapacity(String id, int newCapacity) {
        boolean rowUpdated = false;
        try (Connection connection = JDBCUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CAPACITY_SQL)) {

            preparedStatement.setInt(1, newCapacity);
            preparedStatement.setString(2, id);

            rowUpdated = preparedStatement.executeUpdate() > 0;

            if (rowUpdated) {
                System.out.println("Update Capacity successfull for Bus:" + id + " to " + newCapacity + " seats.");
            } else {

                System.err.println("Cannot find " + id + " to update capacity");
            }
        } catch (SQLException e) {
            System.err.println("Error when updating Bus capacity: " + e.getMessage());
        }
        return rowUpdated;
    }
    
        public boolean updateBusStatus(String id, boolean isActive) {
        boolean rowUpdated = false;
        try (Connection connection = JDBCUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_STATUS_SQL)) {

            preparedStatement.setBoolean(1, isActive);
            preparedStatement.setString(2, id);

            rowUpdated = preparedStatement.executeUpdate() > 0;

            if (rowUpdated) {
                String status = isActive ? "ACTIVE" : "INACTIVE";
                System.out.println("Update Status successfull for Bus:" + id + ". New Status: " + status);
            } else {
                System.err.println("Cannot find " + id + " to update status");
            }
        } catch (SQLException e) {
            System.err.println("Error when updating Bus status: " + e.getMessage());
        }
        return rowUpdated;
    }
    
    public boolean RemoveBus (String id) {
        boolean rowDeleted = false;
         try (Connection connection = JDBCUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_BUS_SQL)) {
            
             statement.setString (1, id);
             rowDeleted = statement.executeUpdate() > 0;
             
             if (rowDeleted) {
                 System.out.println ("Delete ID" + id +" Successful.");
             } else {
                 System.err.println ("Cannot find this ID" + id + " To Delete");
             }
         } catch (SQLException e) {
             System.err.println("Error when delete Bus" + e.getMessage());
         }
         return rowDeleted;
    }
}
