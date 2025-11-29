
package project.oop.dao;

import project.oop.Bus;
import project.oop.utils.JDBCUtils;
import project.oop.utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BusDAO implements CrudDAO<Bus> {
    private Connection connection;
    
    public BusDAO( Connection connection) {
        this.connection= connection;
    }
    private static final String INSERT_BUS_SQL = "INSERT INTO bus (id, license_plate, capacity, model, purchase_year, is_active) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SELECT_BUS_BY_ID = "SELECT * FROM bus WHERE id = ?";
    private static final String SELECT_ALL_BUSES = "SELECT * FROM bus";
    private static final String DELETE_BUS_SQL = "DELETE FROM bus WHERE id = ?";
    private static final String UPDATE_BUS_SQL = "UPDATE bus SET license_plate=?, capacity=?, model=?, purchase_year=?, is_active=? WHERE id = ?";
    private static final String COUNT_BUSES_SQL = "SELECT COUNT(*) FROM bus";
    private static final String GET_LAST_ID_SQL = "SELECT id FROM bus ORDER BY id DESC LIMIT 1";

    public String GetNextBusId(){
        try(PreparedStatement preparedStatement= connection.prepareStatement(GET_LAST_ID_SQL);
            ResultSet rs = preparedStatement.executeQuery()){
                if (rs.next()){
                    String LastId= rs.getString("id");
                    int number = Integer.parseInt(LastId.substring(1));
                    number++;
                    
                    return String.format("B%03d", number);
                }
        } catch (SQLException e){
            System.err.println("Error when generating driver ID:"+ e.getMessage());
        }
        return "B001";
    }
    
@Override
    public boolean Add (Bus bus) {
        System.out.println (INSERT_BUS_SQL);
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_BUS_SQL)
                ) {
            preparedStatement.setString(1, bus.getId());
            preparedStatement.setString(2, bus.getLicensePlate());
            preparedStatement.setInt(3, bus.getCapacity());
            preparedStatement.setString(4, bus.getModel());
            preparedStatement.setInt(5, bus.getPurchaseYear());
            preparedStatement.setBoolean(6, bus.isActive()); 
            
            int affected_rows= preparedStatement.executeUpdate();
            if (affected_rows > 0) {
            System.out.println("Bus " + bus.getId() + " added successfully model:" + bus.getModel());
            return true;
            }else {return false;}
        } catch (SQLException e) {
            System.err.println ("Error when adding Bus into the Database: " +e.getMessage());
            return false;
        }
    }

@Override
    public Bus FindById(String id) {
        Bus bus = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BUS_BY_ID)){
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

@Override
    public List<Bus> SelectAll() {
        List<Bus> busList = new ArrayList<>();
        
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_BUSES)) {
            
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
        try (PreparedStatement preparedStatement = connection.prepareStatement(COUNT_BUSES_SQL);
                ResultSet rs = preparedStatement.executeQuery()){
           if (rs.next()) {
               count = rs.getInt(1);
           }
        }catch (SQLException e) {
            System.err.println ("Error when calculating total of Bus: "+ e.getMessage());
        }
        return count;
    }
    
@Override
    public boolean Update(Bus bus) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_BUS_SQL)){
            preparedStatement.setString(1, bus.getLicensePlate());
            preparedStatement.setInt(2, bus.getCapacity());
            preparedStatement.setString(3, bus.getModel());
            preparedStatement.setInt(4, bus.getPurchaseYear());
            preparedStatement.setBoolean(5, bus.isActive());
            preparedStatement.setString(6, bus.getId());
            return preparedStatement.executeUpdate() > 0;
        }catch (SQLException e) {
            System.err.println("Error when performing full update Bus: " + e.getMessage());
            return false;
        }
    }

@Override
    public boolean Remove (String id) {
        boolean rowDeleted = false;
         try (PreparedStatement statement = connection.prepareStatement(DELETE_BUS_SQL)) {
            
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
