package GUI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import project.oop.service.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import project.oop.Route;

public class RouteUI extends JPanel{
    private final RouteService routeService;
    private final AssignmentService assignmentService;
    
    private DefaultTableModel routeTableModel; 
    private JTable routeTable;
    
    private JTextField textId;
    private JTextField textName;
    private JTextField textStartPoint;
    private JTextField textEndPoint;
    private JTextField textDistance;
    
    public RouteUI(RouteService routeService, AssignmentService assignmentService) {
        this.routeService = routeService;
        this.assignmentService = assignmentService;
        setLayout(new BorderLayout(10, 10));
        initializeTable();
        initializeInputForm();
        
        add(new JScrollPane(routeTable), BorderLayout.CENTER);
        add(createControlPanel(), BorderLayout.SOUTH);
        add(initializeInputForm(), BorderLayout.NORTH);
        setupRowSelectionListener();
        loadRouteData();
    }
    
    private void initializeTable() {

        String[] columnNames = {"ID", "Route Name", "Departure Point", "Destination", "Distance (km)"};
        routeTableModel = new DefaultTableModel(columnNames, 0) {
             @Override
             public boolean isCellEditable(int row, int column) {
                 return false; 
             }
        };
        routeTable = new JTable(routeTableModel);
    }
    
    private JPanel initializeInputForm() {
        JPanel panel = new JPanel (new GridLayout(8,2,10,5));
        
        textId= new JTextField();
        textId.setEditable(false); 
        textName= new JTextField();
        textStartPoint= new JTextField();
        textEndPoint= new JTextField();
        textDistance= new JTextField();
        
        panel.setBorder(BorderFactory.createTitledBorder("Route information"));
        panel.add(new JLabel("ID route:"));
        panel.add(textId);
        panel.add(new JLabel("Name:"));
        panel.add(textName);
        panel.add(new JLabel("Departure Point:"));
        panel.add(textStartPoint);
        panel.add(new JLabel("Destination Point:"));
        panel.add(textEndPoint);
        panel.add(new JLabel("Distance (km):"));
        panel.add(textDistance);
        
        return panel;
    }
    
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnRemove = new JButton("Delete");
        JButton btnClear = new JButton("Refresh");

        btnAdd.addActionListener(e -> addRoute());
        btnUpdate.addActionListener(e -> updateRoute());
        btnRemove.addActionListener(e -> removeRoute());
        btnClear.addActionListener(e -> clearForm());

        controlPanel.add(btnAdd);
        controlPanel.add(btnUpdate);
        controlPanel.add(btnRemove);
        controlPanel.add(btnClear);
        
        return controlPanel;
    }
    private Route getRouteFromForm() {
        try {
            Route route = new Route();
            route.setId(textId.getText().trim());
            route.setName(textName.getText().trim());
            route.setStartPoint(textStartPoint.getText().trim());
            route.setEndPoint(textEndPoint.getText().trim());
            
            route.setDistance(Double.parseDouble(textDistance.getText().trim()));        
            return route;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Wrong format", "Data Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    private void loadRouteData() {
        routeTableModel.setRowCount(0); 
        
        List<Route> routeList = routeService.GetAllRoutes();
        
        for (Route route : routeList) {
            routeTableModel.addRow(new Object[]{
                route.getId(),
                route.getName(),
                route.getStartPoint(),
                route.getEndPoint(),
                String.format("%,.2f", route.getDistance())
            });
        }
    }
    private void setupRowSelectionListener() {
        routeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && routeTable.getSelectedRow() != -1) {
                int selectedRow = routeTable.getSelectedRow();
                setFormValuesFromTable(selectedRow);
            }
        });
    }
    private void setFormValuesFromTable(int selectedRow) {
        textId.setText(routeTableModel.getValueAt(selectedRow, 0).toString());
        textName.setText(routeTableModel.getValueAt(selectedRow, 1).toString());
        textStartPoint.setText(routeTableModel.getValueAt(selectedRow, 2).toString());
        textEndPoint.setText(routeTableModel.getValueAt(selectedRow, 3).toString());
        String formattedDistance = routeTableModel.getValueAt(selectedRow, 4).toString().replace(",", "");
        textDistance.setText(formattedDistance);
    }
    
    private void addRoute() {
        Route route = getRouteFromForm();
        if (route == null) return;
        
        route.setId(null); 

        boolean done = routeService.AddNewRoute(route);
        
        if (done) {

            JOptionPane.showMessageDialog(this, "Route added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadRouteData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add Route", "Logic Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateRoute() {
        String id = textId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a Route to update from the table.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Route route = getRouteFromForm();
        if (route == null) return;
        
        route.setId(id);
        
        boolean done = routeService.UpdateRoute(route);
        
        if (done) {
            JOptionPane.showMessageDialog(this, "Route updated successfully!", "DONE", JOptionPane.INFORMATION_MESSAGE);
            loadRouteData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update route", "Logic Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeRoute() {
        String idToRemove = textId.getText().trim();
        if (idToRemove.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a Route to remove from the table.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure to delete Route ID " + idToRemove + "? This operation might fail if the Route is currently assigned.", 
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean done= routeService.RemoveRoute(idToRemove);

            if (done) {
                JOptionPane.showMessageDialog (this, "Route deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadRouteData();
                clearForm();
            } else {
                 JOptionPane.showMessageDialog(this, 
                     "Failed to delete Route. This Route might be currently assigned to a route or an error occurred.", 
                     "Logic Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        textId.setText("");
        textName.setText("");
        textStartPoint.setText("");
        textEndPoint.setText("");
        textDistance.setText("");
        routeTable.clearSelection();
    }
}
