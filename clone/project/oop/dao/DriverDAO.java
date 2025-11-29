
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

public class DriverDAO implements CrudDAO<Driver> {
    private Connection connection;
    public DriverDAO(Connection connection) {
        this.connection= connection;
    }
    private static final String INSERT_DRIVER_SQL = "INSERT INTO driver (id, name, phone_number, address, license_number, salary, experience_years)VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_DRIVER_BY_ID = "SELECT * FROM driver WHERE id = ?";
    private static final String SELECT_ALL_DRIVERS = "SELECT * FROM driver";
    private static final String DELETE_DRIVER_SQL = "DELETE FROM driver WHERE id = ?";
    private static final String UPDATE_DRIVER_SQL = "UPDATE driver SET name=?, phone_number=?, address=?, license_number=?, salary=?, experience_years=? WHERE id = ?";
    private static final String COUNT_DRIVERS_SQL = "SELECT COUNT(*) FROM driver";
    private static final String GET_LAST_ID_SQL = "SELECT id FROM driver ORDER BY id DESC LIMIT 1";
    private static final String UPDATE_SALARY_SQL = "UPDATE driver SET salary = ? WHERE id = ?";

    
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

@Override
    public boolean Add (Driver driver) {
        System.out.println (INSERT_DRIVER_SQL);
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_DRIVER_SQL)
                ) {
            preparedStatement.setString (1, driver.getId());
            preparedStatement.setString (2, driver.getName());
            preparedStatement.setString (3, driver.getPhoneNumber());
            preparedStatement.setString (4, driver.getAddress());
            preparedStatement.setString (5, driver.getLicenseNumber());
            preparedStatement.setDouble (6, driver.getSalary());
            preparedStatement.setInt (7, driver.getExperienceYears());
            
            return preparedStatement.executeUpdate()> 0;

        } catch (SQLException e) {
            System.err.println ("Error when adding Driver into the Database: " +e.getMessage());
            return false;
        }
    }
  
@Override
    public Driver FindById(String id) {
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

    public boolean UpdateDriverSalary (String id, double newSalary) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SALARY_SQL)) {
            
            preparedStatement.setDouble (1, newSalary);
            preparedStatement.setString (2, id);
            
            return preparedStatement.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println ("Error when updating Driver's salary: "+ e.getMessage());
            return false;
        }
    }
    
@Override
    public List<Driver> SelectAll() {
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
    
@Override
    public boolean Update (Driver driver){
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_DRIVER_SQL)) {
            preparedStatement.setString(1, driver.getName());
            preparedStatement.setString(2, driver.getPhoneNumber());
            preparedStatement.setString(3, driver.getAddress());
            preparedStatement.setString(4, driver.getLicenseNumber());
            preparedStatement.setDouble(5, driver.getSalary());
            preparedStatement.setInt(6, driver.getExperienceYears());
            preparedStatement.setString(7, driver.getId()); 
            
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error when performing full update Driver: " + e.getMessage());
            return false;
        }
    }

@Override
    public boolean Remove (String id) {
         try (PreparedStatement statement = connection.prepareStatement(DELETE_DRIVER_SQL)) {
             statement.setString (1, id);
             return statement.executeUpdate() > 0;
         } catch (SQLException e) {
             System.err.println("Error when delete Driver" + e.getMessage());
             return false;
         }
    }
}    
