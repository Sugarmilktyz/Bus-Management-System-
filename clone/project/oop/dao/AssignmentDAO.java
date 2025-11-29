
package project.oop.dao;

import project.oop.Assignment;
import project.oop.utils.JDBCUtils;
import project.oop.Bus;
import project.oop.Driver;
import project.oop.Route;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class AssignmentDAO implements CrudDAO<Assignment>{
    private Connection connection;
    public AssignmentDAO(Connection connection){
        this.connection= connection;
    }
    private static final String INSERT_ASSIGNMENT_SQL = "INSERT INTO assignment (id, driver_id, bus_id, route_id, assignment_date, shift) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SELECT_ALL_ASSIGNMENTS = "SELECT * FROM assignment";
    private static final String DELETE_ASSIGNMENT_SQL = "DELETE FROM assignment WHERE id = ?";
    private static final String COUNT_ASSIGNMENTS_SQL = "SELECT COUNT(*) FROM assignment";
    private static final String UPDATE_ASSIGNMENT_SQL = "UPDATE assignment SET driver_id=?, bus_id=?, route_id=?, assignment_date=?, shift=? WHERE id = ?";
    private static final String CHECK_OVERLAP_SQL = "SELECT COUNT(*) FROM assignment " + "WHERE (driver_id = ? OR bus_id = ?) " + "AND assignment_date = ? AND shift = ?";
    private static final String SELECT_ASSIGNMENT_BY_ID = "SELECT * FROM assignment WHERE id = ?";
    private static final String GET_LAST_ID_SQL = "SELECT id FROM assignment ORDER BY id DESC LIMIT 1";

    
    private static final String REPORT_ASSIGNMENTS_SQL =
            "SELECT a.id, d.name AS driver_name, b.license_plate, r.name AS route_name, a.assignment_date, a.shift " +
            "FROM assignment a " +
            "JOIN driver d ON a.driver_id = d.id " +
            "JOIN bus b ON a.bus_id = b.id " +
            "JOIN route r ON a.route_id = r.id " +
            "ORDER BY a.assignment_date, a.shift";
    
    
    public boolean IsAssignmentOverlapped(String driverId, String busId, String date, String shift) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(CHECK_OVERLAP_SQL)) {
            
            preparedStatement.setString(1, driverId); 
            preparedStatement.setString(2, busId);   
            preparedStatement.setString(3, date);
            preparedStatement.setString(4, shift);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        }
    }
    
    public boolean IsUsedInAssignment(String id, String columnName) throws SQLException {
    String CheckSQL = "SELECT COUNT(*) FROM assignment WHERE " + columnName + " = ?";
    
    try (PreparedStatement preparedStatement = connection.prepareStatement(CheckSQL)) {
        preparedStatement.setString(1, id);
        try (ResultSet rs = preparedStatement.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
    }
    return false;
}
    
    public String GetNextAssignmentId(){
        try(PreparedStatement preparedStatement= connection.prepareStatement(GET_LAST_ID_SQL);
            ResultSet rs = preparedStatement.executeQuery()){
                if (rs.next()){
                    String LastId= rs.getString("id");
                    int number = Integer.parseInt(LastId.substring(1));
                    number++;
                    
                    return String.format("A%03d", number);
                }
        } catch (SQLException e){
            System.err.println("Error when generating assignment ID:"+ e.getMessage());
        }
        return "A001";
    }
    
@Override 
    public boolean Add (Assignment assignment) {
        try 
               (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_ASSIGNMENT_SQL)){
                preparedStatement.setString(1, assignment.getId());
                preparedStatement.setString(2, assignment.getDriverId());
                preparedStatement.setString(3, assignment.getBusId());
                preparedStatement.setString(4, assignment.getRouteId());
                preparedStatement.setString(5, assignment.getAssignmentDate());
                preparedStatement.setString(6, assignment.getShift());

            int affected_rows= preparedStatement.executeUpdate();
            if (affected_rows > 0) {
                System.out.println("Assignment " + assignment.getId() + " added successfully.");
            return true;
            }else {return false;}
            
        } catch (SQLException e) {
            System.err.println("Error when adding Assignment: " + e.getMessage());
            return false;
        }
    }

@Override    
    public Assignment FindById(String id) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ASSIGNMENT_BY_ID)) {

            preparedStatement.setString(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return new Assignment(
                    rs.getString("id"), 
                    rs.getString("driver_id"), 
                    rs.getString("bus_id"), 
                    rs.getString("route_id"), 
                    rs.getString("assignment_date"), 
                    rs.getString("shift"));
            }
        } catch (SQLException e) {
            System.err.println("Error when finding Assignment: " + e.getMessage());
        }
        return null;
    }    

@Override
    public List<Assignment> SelectAll() {
        List<Assignment> assignmentList = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_ASSIGNMENTS);
             ResultSet rs = preparedStatement.executeQuery()) {
            
            while (rs.next()) {
                 Assignment assignment = new Assignment(
                    rs.getString("id"), 
                    rs.getString("driver_id"), 
                    rs.getString("bus_id"), 
                    rs.getString("route_id"), 
                    rs.getString("assignment_date"), 
                    rs.getString("shift"));
                 assignmentList.add(assignment);
            }

        } catch (SQLException e) {
             System.err.println("Error when loading Assignment list: " + e.getMessage());
        }
        return assignmentList;
    }
    
    public List<Vector<String>> GetReportDataForGUI() {
        List<Vector<String>> reportData = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(REPORT_ASSIGNMENTS_SQL);
             ResultSet rs = preparedStatement.executeQuery()) {

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("id"));
                row.add(rs.getString("driver_name"));
                row.add(rs.getString("license_plate"));
                row.add(rs.getString("route_name"));
                row.add(rs.getString("assignment_date"));
                row.add(rs.getString("shift"));
                reportData.add(row);
            }

        } catch (SQLException e) {
            System.err.println("Error when generating Assignment Report for GUI: " + e.getMessage());
        }
        return reportData;
    }

@Override
    public boolean Update(Assignment assignment) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ASSIGNMENT_SQL)) {
            preparedStatement.setString(1, assignment.getDriverId());
            preparedStatement.setString(2, assignment.getBusId());
            preparedStatement.setString(3, assignment.getRouteId());
            preparedStatement.setString(4, assignment.getAssignmentDate());
            preparedStatement.setString(5, assignment.getShift());
            preparedStatement.setString(6, assignment.getId()); // Đặt ID ở cuối WHERE

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error when performing full update Assignment: " + e.getMessage());
            return false;
        }
    }
    
@Override
    public boolean Remove(String id) {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_ASSIGNMENT_SQL)) {

            statement.setString(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error when deleting Assignment: " + e.getMessage());
            return false;
        }
    }

    
    public int GetTotalAssignments() {
        int count = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(COUNT_ASSIGNMENTS_SQL);
             ResultSet rs = preparedStatement.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error when calculating total of Assignments: " + e.getMessage());
        }
        return count;
    }  

}