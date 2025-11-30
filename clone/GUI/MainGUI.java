
package GUI;

import project.oop.dao.*;
import project.oop.service.*;
import project.oop.utils.ConnectionManager;
import javax.swing.SwingUtilities;
import java.sql.Connection;

public class MainGUI {
    public static void main(String[] args) {
        
        Connection conn = ConnectionManager.GetConnection();
        BusDAO busDAO = new BusDAO(conn);
        DriverDAO driverDAO = new DriverDAO(conn);
        RouteDAO routeDAO = new RouteDAO(conn);
        AssignmentDAO assignmentDAO = new AssignmentDAO(conn);
        
        BusService busService = new BusService(busDAO, assignmentDAO);
        DriverService driverService = new DriverService(driverDAO, assignmentDAO);
        RouteService routeService = new RouteService(routeDAO, assignmentDAO);
        AssignmentService assignmentService = new AssignmentService(assignmentDAO, busDAO, driverDAO, routeDAO);


        SwingUtilities.invokeLater(() -> {
            new RootFrame(busService, driverService, routeService, assignmentService).setVisible(true);
        });
    }
}
