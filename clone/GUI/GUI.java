
package GUI;

import project.oop.dao.DriverDAO;
import project.oop.dao.BusDAO;
import project.oop.dao.RouteDAO;
import project.oop.dao.AssignmentDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;
import java.sql.SQLException;
import java.sql.Connection;
import project.oop.Assignment;
import project.oop.Bus;
import project.oop.Driver;
import project.oop.Route;
import project.oop.utils.ConnectionManager;

public class GUI extends JFrame {
    Connection conn= ConnectionManager.GetConnection();
    private final DriverDAO driverDAO = new DriverDAO(conn);
    private final AssignmentDAO assignmentDAO = new AssignmentDAO(conn);
    private final BusDAO busDAO = new BusDAO(conn); 
    private final RouteDAO routeDAO = new RouteDAO(conn);
    
    private JTable driverTable;
    private DefaultTableModel driverTableModel;
    private JTable assignmentTable;
    private DefaultTableModel assignmentTableModel;

    private static final int FRAME_WIDTH = 1000;
    private static final int FRAME_HEIGHT = 650;

    public GUI() {
        setTitle("Bus Management System");
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        
        
        tabbedPane.addTab("Driver", createDriverPanel());
        tabbedPane.addTab("Assignment", createAssignmentPanel());

        
        add(tabbedPane, BorderLayout.CENTER);
        
        initializeData();
        
        loadDriverData();
        loadAssignmentData();
    }


    private JPanel createDriverPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] driverColumns = {"Id", "name", "phone_number", "address", "license_number", "salary", "experience_years"};
        driverTableModel = new DefaultTableModel(driverColumns, 0);
        driverTable = new JTable(driverTableModel);
        driverTable.setFont(new Font("Arial", Font.PLAIN, 12));
        driverTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(driverTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        JButton addButton = new JButton("➕ Add new Driver");
        JButton deleteButton = new JButton("❌ Delete Driver");
        JButton updateSalaryButton = new JButton("💰 Update Salary");
        JButton refreshButton = new JButton("🔄 Refresh");

        addButton.addActionListener(e -> showAddDriverDialog());
        deleteButton.addActionListener(e -> deleteSelectedDriver());
        updateSalaryButton.addActionListener(e -> showUpdateSalaryDialog());
        refreshButton.addActionListener(e -> loadDriverData());
        
        controlPanel.add(addButton);
        controlPanel.add(deleteButton);
        controlPanel.add(updateSalaryButton);
        controlPanel.add(refreshButton);
        
        panel.add(controlPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private void loadDriverData() {
        driverTableModel.setRowCount(0); 
        List<Driver> drivers = driverDAO.ListAllDriver();
        for (Driver d : drivers) {
            Vector<Object> row = new Vector<>();
            row.add(d.getId());
            row.add(d.getName());
            row.add(d.getPhoneNumber());
            row.add(d.getAddress());
            row.add(d.getLicenseNumber());
            row.add(String.format("%,.0f", d.getSalary())); 
            row.add(d.getExperienceYears());
            driverTableModel.addRow(row);
        }
    }
    
    private void showAddDriverDialog() {
        JTextField nameField = new JTextField(10);
        JTextField phoneField = new JTextField(10);
        JTextField addressField = new JTextField(15);
        JTextField licenseField = new JTextField(10);
        JTextField salaryField = new JTextField(10);
        JTextField experienceField = new JTextField(5);

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Phone No:"));
        panel.add(phoneField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("License:"));
        panel.add(licenseField);
        panel.add(new JLabel("Salary (VND):"));
        panel.add(salaryField);
        panel.add(new JLabel("Experience (year):"));
        panel.add(experienceField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add new driver", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                Driver newDriver = new Driver(
                    nameField.getText().trim(),
                    phoneField.getText().trim(),
                    addressField.getText().trim(),
                    licenseField.getText().trim(),
                    Double.parseDouble(salaryField.getText().trim()),
                    Integer.parseInt(experienceField.getText().trim())
                );
                driverDAO.AddDriver(newDriver);
                loadDriverData();
                JOptionPane.showMessageDialog(this, "Add driver Successful", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Salary and experience_year must be a valid number", "error when entering data", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error when ADDING: " + e.getMessage(), "Error DB/Logic", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void deleteSelectedDriver() {
        int selectedRow = driverTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Choose 1 rows.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String driverId = driverTableModel.getValueAt(selectedRow, 0).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete driver ID?: " + driverId + "?", "Delete confirm", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            if (driverDAO.RemoveDriver(driverId)) {
                JOptionPane.showMessageDialog(this, "Delete successful.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadDriverData(); 
                loadAssignmentData(); 
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting driver. Driver may be in assignment.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showUpdateSalaryDialog() {
        int selectedRow = driverTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a driver to update.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String driverId = driverTableModel.getValueAt(selectedRow, 0).toString();
        String currentSalaryStr = driverTableModel.getValueAt(selectedRow, 5).toString().replace(",", "");

        JTextField salaryField = new JTextField(currentSalaryStr);
        
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("ID:"));
        panel.add(new JLabel(driverId));
        panel.add(new JLabel("salary:"));
        panel.add(salaryField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Salary update", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                double newSalary = Double.parseDouble(salaryField.getText().trim());
                if (driverDAO.updateDriverSalary(driverId, newSalary)) {
                    JOptionPane.showMessageDialog(this, "Salary update successful.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadDriverData();
                } else {
                    JOptionPane.showMessageDialog(this, "Salary update failed. Check ID.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "New salary must be valid.", "Input error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    // ----------------------------------------------------
    // PHẦN 2: PANEL BÁO CÁO ASSIGNMENT
    // ----------------------------------------------------
    
    private JPanel createAssignmentPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] assignmentColumns = {"ID ASSN", "DriverId", "License Plate", "Route Name", "Date", "Shift"};
        assignmentTableModel = new DefaultTableModel(assignmentColumns, 0);
        assignmentTable = new JTable(assignmentTableModel);
        assignmentTable.setFont(new Font("Arial", Font.PLAIN, 12));
        assignmentTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(assignmentTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // 2. Cấu hình Vùng nút bấm
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        JButton addButton = new JButton("📝 Add Assignment");
        JButton deleteButton = new JButton("❌ Delete Assignment");
        JButton refreshButton = new JButton("🔄 Refresh Report");

        addButton.addActionListener(e -> showAddAssignmentDialog());
        deleteButton.addActionListener(e -> deleteSelectedAssignment());
        refreshButton.addActionListener(e -> loadAssignmentData());
        
        controlPanel.add(addButton);
        controlPanel.add(deleteButton);
        controlPanel.add(refreshButton);
        
        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
    }
    

    private void loadAssignmentData() {
        assignmentTableModel.setRowCount(0); 
        
        List<Vector<String>> reportData = assignmentDAO.GetReportDataForGUI(); 
        
        if (reportData != null) {
            for (Vector<String> row : reportData) {
                assignmentTableModel.addRow(row);
            }
        } else {
        }
    }

    private void showAddAssignmentDialog() {
        List<Driver> drivers = driverDAO.ListAllDriver();
        List<Bus> buses = busDAO.ListAllBuses();
        List<Route> routes = routeDAO.ListAllRoutes();


        String[] driverIds = drivers.stream().map(Driver::getId).toArray(String[]::new);
        String[] busIds = buses.stream().map(Bus::getId).toArray(String[]::new);
        String[] routeIds = routes.stream().map(Route::getId).toArray(String[]::new);
        String[] shifts = {"morning", "evening", "night"};

        JTextField idField = new JTextField(5);
        JComboBox<String> driverCombo = new JComboBox<>(driverIds);
        JComboBox<String> busCombo = new JComboBox<>(busIds);
        JComboBox<String> routeCombo = new JComboBox<>(routeIds);
        JTextField dateField = new JTextField("YYYY-MM-DD");
        JComboBox<String> shiftCombo = new JComboBox<>(shifts);

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("ID:"));
        panel.add(idField);
        panel.add(new JLabel("Driver (ID):"));
        panel.add(driverCombo);
        panel.add(new JLabel("Bus (ID):"));
        panel.add(busCombo);
        panel.add(new JLabel("Route (ID):"));
        panel.add(routeCombo);
        panel.add(new JLabel("Date (YYYY-MM-DD):"));
        panel.add(dateField);
        panel.add(new JLabel("Shift:"));
        panel.add(shiftCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Schedule Assignment", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                Assignment newAsn = new Assignment(
                    idField.getText().trim(),
                    driverCombo.getSelectedItem().toString(),
                    busCombo.getSelectedItem().toString(),
                    routeCombo.getSelectedItem().toString(),
                    dateField.getText().trim(),
                    shiftCombo.getSelectedItem().toString()
                );
                
                assignmentDAO.AddAssignment(newAsn); 

                loadAssignmentData();
                JOptionPane.showMessageDialog(this, "Try adding a Completed Assignment. Check Console for Validation errors (Duplicate Schedule/Vehicle Inactive).", "Assignment Results", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error adding Assignment: " + e.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void deleteSelectedAssignment() {
        int selectedRow = assignmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String assignmentId = assignmentTableModel.getValueAt(selectedRow, 0).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete the ID Assignment: " + assignmentId + "?", "confirm deletion", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            if (assignmentDAO.RemoveAssignment(assignmentId)) {
                JOptionPane.showMessageDialog(this, "Delete assignment successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAssignmentData();
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting assignment.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    // ----------------------------------------------------
    // PHẦN 3: KHỞI TẠO DỮ LIỆU VÀ CHẠY APP
    // ----------------------------------------------------
    
    private void initializeData() {
        // Dữ liệu Driver
        Driver d1 = new Driver ("D001", "Nguyen Van A", "012345678", "123 P.Nam", "B2-2020-A", 28000000.0, 7);
        Driver d5 = new Driver ("D005", "Le Thi B", "876543210", "345 P.Bac", "B2-2018-B", 30000000.0, 10);
        driverDAO.AddDriver(d1);
        driverDAO.AddDriver(d5);
        
        // Dữ liệu Bus
        Bus b1 = new Bus("B001", "51H-123.45", 45, "Hyundai Universe", 2020, true);
        Bus b2 = new Bus("B002", "50F-999.88", 29, "Samco Felix", 2018, false); // INACTIVE
        busDAO.AddBus(b1);
        busDAO.AddBus(b2);

        // Dữ liệu Route
        Route r1 = new Route("R01", "Ben Thanh - Suoi Tien", "Ben Thanh", "Suoi Tien", 30.5);
        Route r2 = new Route("R02", "CV 23/9 - Bach Khoa", "Cong vien 23/9", "Đai hoc Bach Khoa", 15.0);
        routeDAO.AddRoute(r1);
        routeDAO.AddRoute(r2);
        
        Assignment asn1 = new Assignment("ASN001", "D001", "B001", "R01", "2025-12-01", "Morning");
        assignmentDAO.AddAssignment(asn1);
        Assignment asn6 = new Assignment("ASN006", "D002", "B001", "R01", "2025-12-01", "Evening");
        assignmentDAO.AddAssignment(asn6);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GUI app = new GUI();
            app.setVisible(true);
        });
    }
    
}
