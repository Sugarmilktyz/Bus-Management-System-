
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

public class AssignmentDAO {
    private Connection connection;
    public AssignmentDAO(Connection connection){
        this.connection= connection;
    }
    private static final String INSERT_ASSIGNMENT_SQL = "INSERT INTO assignment (id, driver_id, bus_id, route_id, assignment_date, shift) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SELECT_ALL_ASSIGNMENTS = "SELECT * FROM assignment";
    private static final String DELETE_ASSIGNMENT_SQL = "DELETE FROM assignment WHERE id = ?";
    private static final String COUNT_ASSIGNMENTS_SQL = "SELECT COUNT(*) FROM assignment";
    
    private static final String CHECK_OVERLAP_SQL = "SELECT COUNT(*) FROM assignment " + "WHERE (driver_id = ? OR bus_id = ?) " + "AND assignment_date = ? AND shift = ?";
    
    private static final String CHECK_BUS_ACTIVE_SQL = "SELECT is_active FROM bus WHERE id = ?";
    private static final String GET_LAST_ID_SQL = "SELECT id FROM assignment ORDER BY id DESC LIMIT 1";

    
    private static final String REPORT_ASSIGNMENTS_SQL =
            "SELECT a.id, d.name AS driver_name, b.license_plate, r.name AS route_name, a.assignment_date, a.shift " +
            "FROM assignment a " +
            "JOIN driver d ON a.driver_id = d.id " +
            "JOIN bus b ON a.bus_id = b.id " +
            "JOIN route r ON a.route_id = r.id " +
            "ORDER BY a.assignment_date, a.shift";
    
    private boolean isBusActive(String busId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(CHECK_BUS_ACTIVE_SQL)) {
            preparedStatement.setString(1, busId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("is_active"); 
                }
                return false; 
            }
        }
    }
    
    private boolean isAssignmentOverlapped(String driverId, String busId, String date, String shift) throws SQLException {
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
    

    public void AddAssignment(Assignment assignment) {
        String NewId = GetNextAssignmentId();
        assignment.setId(NewId);

        try {
            
            if (!isBusActive(assignment.getBusId())) {
                System.err.println("Error: Bus ID " + assignment.getBusId() + " is INACTIVE. Cannot assign.");
                return;
            }

            if (isAssignmentOverlapped(assignment.getDriverId(), assignment.getBusId(), assignment.getAssignmentDate(), assignment.getShift())) {
                System.err.println("Error: Driver " + assignment.getDriverId() + " OR Bus " + assignment.getBusId() + " is already assigned on " + assignment.getAssignmentDate() + " Shift: " + assignment.getShift());
                return;
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_ASSIGNMENT_SQL)) {
                preparedStatement.setString(1, NewId);
                preparedStatement.setString(2, assignment.getDriverId());
                preparedStatement.setString(3, assignment.getBusId());
                preparedStatement.setString(4, assignment.getRouteId());
                preparedStatement.setString(5, assignment.getAssignmentDate());
                preparedStatement.setString(6, assignment.getShift());

                preparedStatement.executeUpdate();
                System.out.println("Assignment " + assignment.getId() + " added successfully.");
            }

        } catch (SQLException e) {
            System.err.println("Error when adding Assignment: " + e.getMessage());
        }
    }
    
    public Assignment FindAssignmentById(String id) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM assignment WHERE id = ?")) {

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
    
    public boolean RemoveAssignment(String id) {
        boolean rowDeleted = false;
        try (PreparedStatement statement = connection.prepareStatement(DELETE_ASSIGNMENT_SQL)) {

            statement.setString(1, id);
            rowDeleted = statement.executeUpdate() > 0;

            if (rowDeleted) {
                System.out.println("Delete Assignment ID " + id + " Successful.");
            } else {
                System.err.println("Cannot find this Assignment ID " + id + " To Delete");
            }
        } catch (SQLException e) {
            System.err.println("Error when deleting Assignment: " + e.getMessage());
        }
        return rowDeleted;
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