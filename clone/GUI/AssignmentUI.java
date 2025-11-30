
package GUI;

import project.oop.Assignment;
import project.oop.Driver;
import project.oop.Bus;
import project.oop.Route;
import project.oop.service.AssignmentService;
import project.oop.service.DriverService;
import project.oop.service.BusService;
import project.oop.service.RouteService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.time.LocalDate;

public class AssignmentUI extends JPanel {
    private final AssignmentService assignmentService;
    private final DriverService driverService;
    private final BusService busService;
    private final RouteService routeService;
    
    private DefaultTableModel assignmentTableModel;
    private JTable assignmentTable;
    private JTextField textId;
    private JTextField textAssignmentDate;
    
    private JComboBox<String> cmbDriver;
    private JComboBox<String> cmbBus;
    private JComboBox<String> cmbRoute;
    private JComboBox<String> cmbShift;
    
    private List<Driver> allDrivers;
    private List<Bus> allBuses;
    private List<Route> allRoutes;
    
    public AssignmentUI(AssignmentService assignmentService, DriverService driverService, BusService busService, RouteService routeService) {
        this.assignmentService = assignmentService;
        this.driverService = driverService;
        this.busService = busService;
        this.routeService = routeService;

        setLayout(new BorderLayout(10, 10));

        loadReferenceData();
        
        initializeTable();
        
        add(new JScrollPane(assignmentTable), BorderLayout.CENTER);
        add(initializeInputForm(), BorderLayout.NORTH);
        add(createControlPanel(), BorderLayout.SOUTH);
        
        setupRowSelectionListener();
        loadAssignmentData();
    }
    
    private void loadReferenceData() {
        allDrivers = driverService.GetAllDrivers();
        allBuses = busService.GetAllBuses();
        allRoutes = routeService.GetAllRoutes();
    }
    
    private void populateComboBoxes() {
        cmbDriver.removeAllItems ();
        cmbBus.removeAllItems ();
        cmbRoute.removeAllItems ();
        
        for (Driver driver : allDrivers) {
            cmbDriver.addItem(driver.getName() + " (" + driver.getId() + ")");
        }
        
        for (Bus bus : allBuses) {
            if (bus.isActive()) { 
                cmbBus.addItem(bus.getLicensePlate() + " (" + bus.getId() + ")");
            }
        }
        
        for (Route route : allRoutes) {
            cmbRoute.addItem(route.getName() + " (" + route.getId() + ")");
        }
        
        cmbShift.addItem ("Morning"); 
        cmbShift.addItem ("Afternoon"); 
        cmbShift.addItem ("Night"); 
    }

    private String extractIdFromComboBox(JComboBox<String> comboBox) {
        String selectedItem = (String) comboBox.getSelectedItem();
        if (selectedItem != null && selectedItem.contains("(")) {
            int start = selectedItem.lastIndexOf("(") + 1;
            int end = selectedItem.lastIndexOf(")");
            return selectedItem.substring(start, end);
        }
        return null;
    }
    
    private void initializeTable() {
        String[] columnNames = {"ID ASN", "ID DRIVER", "ID BUS", "ID ROUTE", "DATE", "SHIFT"};
        assignmentTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        assignmentTable = new JTable(assignmentTableModel);
    }
    
    private JPanel initializeInputForm() {
        JPanel Panel = new JPanel(new GridLayout(6, 2, 10, 5));
        
        textId = new JTextField();
        textId.setEditable(false); 
        textAssignmentDate = new JTextField(LocalDate.now().toString()); 
        
        cmbDriver = new JComboBox<>();
        cmbBus = new JComboBox<>();
        cmbRoute = new JComboBox<>();
        cmbShift = new JComboBox<>();
        
        populateComboBoxes(); 

        Panel.setBorder(BorderFactory.createTitledBorder("Assignment information"));
        
        Panel.add(new JLabel("ID ASN:"));
        Panel.add(textId);
        Panel.add(new JLabel("Driver:"));
        Panel.add(cmbDriver);
        Panel.add(new JLabel("Bus:"));
        Panel.add(cmbBus);
        Panel.add(new JLabel("Route:"));
        Panel.add(cmbRoute);
        Panel.add(new JLabel("Date (YYYY-MM-DD):"));
        Panel.add(textAssignmentDate);
        Panel.add(new JLabel("Shift:"));
        Panel.add(cmbShift);

        return Panel;
    }
    
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnRemove = new JButton("Delete");
        JButton btnClear = new JButton("Refresh");

        btnAdd.addActionListener(e -> addAssignment());
        btnUpdate.addActionListener(e -> updateAssignment());
        btnRemove.addActionListener(e -> removeAssignment());
        btnClear.addActionListener(e -> clearForm());

        controlPanel.add(btnAdd);
        controlPanel.add(btnUpdate);
        controlPanel.add(btnRemove);
        controlPanel.add(btnClear);
        
        return controlPanel;
    }
    
    private Assignment getAssignmentFromForm() {
        Assignment assignment = new Assignment();
        
        String driverId= extractIdFromComboBox(cmbDriver);
        String busId= extractIdFromComboBox(cmbBus);
        String routeId= extractIdFromComboBox(cmbRoute);
        
        if (driverId== null || busId== null || routeId== null) {
            JOptionPane.showMessageDialog (this, "Select a Driver, Bus, and Route.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }


        assignment.setId (textId.getText().trim()); 
        assignment.setDriverId(driverId);
        assignment.setBusId (busId);
        assignment.setRouteId (routeId);
        assignment.setAssignmentDate(textAssignmentDate.getText().trim());
        assignment.setShift((String) cmbShift.getSelectedItem());

        return assignment;
    }
    
    private void loadAssignmentData() {
        assignmentTableModel.setRowCount(0); 
        
        List<Assignment> assignmentList= assignmentService.GetAllAssignments();

        for (Assignment assignment : assignmentList) {
            assignmentTableModel.addRow(new Object[]{
                assignment.getId(),
                assignment.getDriverId(),
                assignment.getBusId(),
                assignment.getRouteId(),
                assignment.getAssignmentDate(),
                assignment.getShift()
            });
        }
    }
    
    private void setupRowSelectionListener() {
        assignmentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && assignmentTable.getSelectedRow() != -1) {
                int selectedRow = assignmentTable.getSelectedRow();
                setFormValuesFromTable(selectedRow);
            }
        });
    }

    private void setFormValuesFromTable(int selectedRow) {
        
        textId.setText(assignmentTableModel.getValueAt(selectedRow, 0).toString());
        textAssignmentDate.setText(assignmentTableModel.getValueAt(selectedRow, 4).toString());
        
        String selectedDriverId = assignmentTableModel.getValueAt(selectedRow, 1).toString();
        String selectedBusId = assignmentTableModel.getValueAt(selectedRow, 2).toString();
        String selectedRouteId = assignmentTableModel.getValueAt(selectedRow, 3).toString();
        String selectedShift = assignmentTableModel.getValueAt(selectedRow, 5).toString();

        for (int i = 0; i < cmbDriver.getItemCount(); i++) {
            if (cmbDriver.getItemAt(i).contains("(" + selectedDriverId + ")")) {
                cmbDriver.setSelectedIndex(i);
                break;
            }
        }
        
        for (int i = 0; i < cmbBus.getItemCount(); i++) {
             if (cmbBus.getItemAt(i).contains("(" + selectedBusId + ")")) {
                cmbBus.setSelectedIndex(i);
                break;
            }
        }
        
        for (int i = 0; i < cmbRoute.getItemCount(); i++) {
             if (cmbRoute.getItemAt(i).contains("(" + selectedRouteId + ")")) {
                cmbRoute.setSelectedIndex(i);
                break;
            }
        }
        
        cmbShift.setSelectedItem(selectedShift);
    }
    
    private void addAssignment() {
        Assignment assignment = getAssignmentFromForm();
        if (assignment== null) return;
        
        assignment.setId(null); 
        
        boolean success = assignmentService.AddNewAssignment(assignment);

        if (success) {
            JOptionPane.showMessageDialog(this, "Assignment added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAssignmentData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add Assignment. Check console for details (e.g., Overlap, Inactive Bus, Wrong Date Format).", "Business Logic Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateAssignment() {
        String id= textId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select an Assignment to update from the table.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Assignment assignment = getAssignmentFromForm();
        if (assignment== null) return;
        
        assignment.setId(id);
        
        boolean success= assignmentService.UpdateAssignment(assignment);
        
        if (success) {
            JOptionPane.showMessageDialog(this, "Assignment updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAssignmentData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update Assignment. Check console for details (e.g., Overlap, Inactive Bus, Wrong Date Format).", "Business Logic Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeAssignment() {
        String idToRemove= textId.getText().trim();
        if (idToRemove.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select an Assignment to remove from the table.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete Assignment ID " + idToRemove + "?", 
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm== JOptionPane.YES_OPTION) {
            boolean done= assignmentService.RemoveAssignment(idToRemove);

            if (done) {
                JOptionPane.showMessageDialog(this, "Assignment deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAssignmentData();
                clearForm();
            } else {
                 JOptionPane.showMessageDialog(this, 
                     "Failed to delete Assignment. Check console for errors.", "Logic Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        textId.setText("");
        if (cmbDriver.getItemCount() > 0) cmbDriver.setSelectedIndex(0);
        if (cmbBus.getItemCount() > 0) cmbBus.setSelectedIndex(0);
        if (cmbRoute.getItemCount() > 0) cmbRoute.setSelectedIndex(0);
        cmbShift.setSelectedIndex(0);
        textAssignmentDate.setText(LocalDate.now().toString());
        assignmentTable.clearSelection();
    }
}
