
package project.oop.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionManager {
    private static Connection connection;
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bus_management_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Sql@Bus-2005";
    
    public static Connection GetConnection () {
        try {
            if(connection == null||connection.isClosed()){
                connection = DriverManager.getConnection (DB_URL, DB_USER, DB_PASSWORD);
                System.out.println ("Successful connected to database");
            }
        } catch (SQLException e) {
            System.err.println ("Error connect:" + e.getMessage());
            Logger.getLogger (JDBCUtils.class.getName()).log(Level.SEVERE, null, e);
        }
        return connection;
    }
    public static void CloseConnection () {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println ("Connection is closed");
            } 
        }catch (SQLException e) {
                        Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, "Error close connection.", e);
                    }
    }
    public static void main(String[] args) {
        Connection conn = ConnectionManager.GetConnection();
        if (conn != null) {
            CloseConnection();
        }
    }
}
