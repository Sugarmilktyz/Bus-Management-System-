
package GUI;

import project.oop.service.*;
import javax.swing.*;
import java.awt.*;

public class RootFrame extends JFrame {
    private final BusService busService;
    private final DriverService driverService;
    private final RouteService routeService;
    private final AssignmentService assignmentService;
    
    public RootFrame(BusService busService, DriverService driverService, RouteService routeService, AssignmentService assignmentService) {
        this.busService = busService;
        this.driverService = driverService;
        this.routeService = routeService;
        this.assignmentService = assignmentService;
        
        setTitle("Bus Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 700));
        
        JTabbedPane tabbedPane = new JTabbedPane();
        
        BusUI busUI = new BusUI (busService, assignmentService); 
        tabbedPane.addTab("1. BUS", busUI);
        
        DriverUI driverUI = new DriverUI (driverService, assignmentService); 
        tabbedPane.addTab("2. DRIVER", driverUI);
        
        RouteUI routeUI = new RouteUI (routeService, assignmentService); 
        tabbedPane.addTab("3. ROUTE", routeUI);
        
        AssignmentUI assignmentUI = new AssignmentUI (assignmentService, driverService, busService, routeService); 
        tabbedPane.addTab("4. ASSIGNMENT", assignmentUI);
        
        this.add(tabbedPane);
        pack();
        setLocationRelativeTo(null);    
    }
}
