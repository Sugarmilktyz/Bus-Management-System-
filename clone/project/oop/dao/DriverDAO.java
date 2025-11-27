
package project.oop.dao;

import project.oop.Driver;
import project.oop.utils.JDBCUtils;
import project.oop.utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DriverDAO {
    private Connection connection;
    public DriverDAO(Connection connection) {
        this.connection= connection;
    }
    private static final String INSERT_DRIVER_SQL = "INSERT INTO driver (id, name, phone_number, address, license_number, salary, experience_years)VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_DRIVER_BY_ID = "SELECT * FROM driver WHERE id = ?";
    private static final String SELECT_ALL_DRIVERS = "SELECT * FROM driver";
    private static final String DELETE_DRIVER_SQL = "DELETE FROM driver WHERE id = ?";
    private static final String UPDATE_SALARY_SQL = "UPDATE driver SET salary = ? WHERE id = ?";
    private static final String COUNT_DRIVERS_SQL = "SELECT COUNT(*) FROM driver";
    private static final String GET_LAST_ID_SQL = "SELECT id FROM driver ORDER BY id DESC LIMIT 1";
    
    public String GetNextDriverId(){
        try(PreparedStatement preparedStatement= connection.prepareStatement(GET_LAST_ID_SQL);
            ResultSet rs = preparedStatement.executeQuery()){
                if (rs.next()){
                    String LastId= rs.getString("id");
                    int number = Integer.parseInt(LastId.substring(1));
                    number++;
                    
                    return String.format("D%03d", number);
                }
        } catch (SQLException e){
            System.err.println("Error when generating driver ID:"+ e.getMessage());
        }
        return "D001";
    }
    
    public void AddDriver (Driver driver) {
        String NewId = GetNextDriverId();
        driver.setId(NewId);
        System.out.println (INSERT_DRIVER_SQL);
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_DRIVER_SQL)
                ) {
            preparedStatement.setString (1, NewId);
            preparedStatement.setString (2, driver.getName());
            preparedStatement.setString (3, driver.getPhoneNumber());
            preparedStatement.setString (4, driver.getAddress());
            preparedStatement.setString (5, driver.getLicenseNumber());
            preparedStatement.setDouble (6, driver.getSalary());
            preparedStatement.setInt (7, driver.getExperienceYears());
            
            preparedStatement.executeUpdate();
            System.out.println("Driver " +driver.getId()+ " added successfully name:" + driver.getName());
        } catch (SQLException e) {
            System.err.println ("Error when adding Driver into the Database: " +e.getMessage());
        }
    }
    
    public Driver FindDriverById(String id) {
        Driver driver = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_DRIVER_BY_ID)){
            preparedStatement.setString(1,id);
            
            ResultSet rs = preparedStatement.executeQuery();
            
            if (rs.next()) {
                String name = rs.getString("name");
                String phone_number = rs.getString ("phone_number");
                String address = rs.getString ("address");
                String license_number = rs.getString ("license_number");
                double salary =rs.getDouble ("salary");
                int experience_years = rs.getInt ("experience_years");
                
                driver = new Driver (id, name, phone_number, address, license_number, salary, experience_years);
            }
        } catch (SQLException e) {
            System.err.println ("Error when finding Driver" +e.getMessage());
        }
        return driver;
    }
    
    public List<Driver> ListAllDriver() {
        List<Driver> driverList = new ArrayList<>();
        
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_DRIVERS)) {
            
            ResultSet rs = preparedStatement.executeQuery();
            
            while (rs.next()) {
                String id = rs.getString ("id");
                String name = rs.getString ("name");
                String phone_number = rs.getString ("phone_number");
                String address = rs.getString ("address");
                String license_number = rs.getString ("license_number");
                double salary = rs.getDouble("salary");
                int experience_years = rs.getInt("experience_years");
                
                Driver driver = new Driver (id, name, phone_number, address, license_number, salary, experience_years);
                driverList.add(driver);
            }
        } catch (SQLException e) {
            System.err.println ("Error when loading Driver list: "+ e.getMessage());
        }
        return driverList;
    }
    
    public int GetTotalDrivers() {
        int count = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(COUNT_DRIVERS_SQL);
                ResultSet rs = preparedStatement.executeQuery()){
           if (rs.next()) {
               count = rs.getInt(1);
           }
        }catch (SQLException e) {
            System.err.println ("Error when calculating total of Driver: "+ e.getMessage());
        }
        return count;
    }
    
    public boolean updateDriverSalary (String id, double newSalary) {
        boolean rowUpdated = false;
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SALARY_SQL)) {
            
            preparedStatement.setDouble (1, newSalary);
            preparedStatement.setString (2, id);
            
            rowUpdated = preparedStatement.executeUpdate() > 0;
            
            if (rowUpdated) {
                System.out.println ("Update Salary successfull for Driver:" + id);
            } else {
                System.err.println ("Cannot find " + id + " to update salary");
            }
        } catch (SQLException e) {
            System.err.println ("Error when updating Driver's salary: "+ e.getMessage());
        }
        return rowUpdated;
    }
    
    public boolean RemoveDriver (String id) {
        boolean rowDeleted = false;
         try (PreparedStatement statement = connection.prepareStatement(DELETE_DRIVER_SQL)) {
            
             statement.setString (1, id);
             rowDeleted = statement.executeUpdate() > 0;
             
             if (rowDeleted) {
                 System.out.println ("Delete ID" + id +" Successful.");
             } else {
                 System.err.println ("Cannot find this ID" + id + " To Delete");
             }
         } catch (SQLException e) {
             System.err.println("Error when delete Driver" + e.getMessage());
         }
         return rowDeleted;
    }
}    
