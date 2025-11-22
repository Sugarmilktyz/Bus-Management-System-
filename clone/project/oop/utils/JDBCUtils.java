
package project.oop.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JDBCUtils {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bus_management_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "__________"; // tui xóa mật khẩu r cần thì nhắn nha
    
    public static Connection getConnection () {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection (DB_URL, DB_USER, DB_PASSWORD);
            System.out.println ("Successful connected to database");
            return connection;
        } catch (SQLException e) {
            System.err.println ("Error connect:" + e.getMessage());
            Logger.getLogger (JDBCUtils.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }
    public static void closeConnection (Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println ("Connection is closed");
            } 
        }catch (SQLException e) {
                        Logger.getLogger(JDBCUtils.class.getName()).log(Level.SEVERE, "Error close connection.", e);
                    }
    }
    public static void main(String[] args) {
        Connection conn = getConnection();
        if (conn != null) {
            closeConnection(conn);
        }
    }
}
