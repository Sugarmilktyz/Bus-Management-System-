package GUI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import project.oop.service.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import project.oop.Driver;

public class DriverUI extends JPanel {
    private final DriverService driverService;
    private final AssignmentService assignmentService;
    
    private DefaultTableModel driverTableModel; 
    private JTable driverTable;
    
    private JTextField textId;
    private JTextField textName;
    private JTextField textPhone;
    private JTextField textAddress;
    private JTextField textLicense;
    private JTextField textSalary;
    private JTextField textExperience;
    
    public DriverUI(DriverService driverService, AssignmentService assignmentService) {
        this.driverService = driverService;
        this.assignmentService = assignmentService;
        setLayout(new BorderLayout(10, 10));
        initializeTable();
        initializeInputForm();
        
        add(new JScrollPane(driverTable), BorderLayout.CENTER);
        add(createControlPanel(), BorderLayout.SOUTH);
        add(initializeInputForm(), BorderLayout.NORTH);
        setupRowSelectionListener();
        loadDriverData();
    }
    
    private void initializeTable() {

        String[] columnNames = {"Id", "name", "phone_number", "address", "license_number", "salary", "experience_years"};
        driverTableModel = new DefaultTableModel(columnNames, 0) {
             @Override
             public boolean isCellEditable(int row, int column) {
                 return false; 
             }
        };
        driverTable = new JTable(driverTableModel);
    }
    
    private JPanel initializeInputForm() {
        JPanel panel = new JPanel (new GridLayout(8,2,10,5));
        
        textId= new JTextField();
        textId.setEditable(false);
        textName= new JTextField();
        textPhone= new JTextField();
        textAddress= new JTextField();
        textLicense= new JTextField();
        textSalary= new JTextField();
        textExperience= new JTextField();
        
        panel.setBorder(BorderFactory.createTitledBorder("Driver information"));
        panel.add(new JLabel("ID Bus:"));
        panel.add(textId);
        panel.add(new JLabel("Name:"));
        panel.add(textName);
        panel.add(new JLabel ("Phone Number:"));
        panel.add(textPhone);
        panel.add(new JLabel("Address:"));
        panel.add(textAddress);
        panel.add(new JLabel("License number:"));
        panel.add(textLicense);
        panel.add(new JLabel("Salary:"));
        panel.add(textSalary);
        panel.add (new JLabel("Experience years:"));
        panel.add(textExperience);
        
        return panel;
    }
    
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnRemove = new JButton("Delete");
        JButton btnClear = new JButton("Refresh");

        btnAdd.addActionListener(e -> addDriver());
        btnUpdate.addActionListener(e -> updateDriver());
        btnRemove.addActionListener(e -> removeDriver());
        btnClear.addActionListener(e -> clearForm());

        controlPanel.add(btnAdd);
        controlPanel.add(btnUpdate);
        controlPanel.add(btnRemove);
        controlPanel.add(btnClear);
        
        return controlPanel;
    }
    private Driver getDriverFromForm() {
        try {
            Driver driver = new Driver();
            driver.setId (textId.getText().trim()); 
            driver.setName( textName.getText().trim());
            driver.setPhoneNumber (textPhone.getText().trim());
            driver.setAddress (textAddress.getText().trim());
            
            driver.setLicenseNumber(textLicense.getText().trim());
            driver.setSalary(Double.parseDouble(textSalary.getText().trim()));
            driver.setExperienceYears(Integer.parseInt(textExperience.getText().trim()));            
            return driver;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Wrong format", "Data Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    private void loadDriverData() {
        driverTableModel.setRowCount(0); 
        
        List<Driver> driverList = driverService.GetAllDrivers();
        
        for (Driver driver : driverList) {
            driverTableModel.addRow(new Object[]{
                driver.getId(),
                driver.getName(),
                driver.getPhoneNumber(),
                driver.getAddress(),
                driver.getLicenseNumber(),
                String.format("%,.0f", driver.getSalary()), 
                driver.getExperienceYears()
            });
        }
    }
    private void setupRowSelectionListener() {
        driverTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && driverTable.getSelectedRow() != -1) {
                int selectedRow = driverTable.getSelectedRow();
                setFormValuesFromTable(selectedRow);
            }
        });
    }
    private void setFormValuesFromTable(int selectedRow) {
        textId.setText(driverTableModel.getValueAt(selectedRow, 0).toString());
        textName.setText(driverTableModel.getValueAt(selectedRow, 1).toString());
        textPhone.setText(driverTableModel.getValueAt(selectedRow, 2).toString());
        textAddress.setText(driverTableModel.getValueAt(selectedRow, 3).toString());
        textLicense.setText(driverTableModel.getValueAt(selectedRow, 4).toString());
        String Salary_formatted = driverTableModel.getValueAt(selectedRow, 5).toString().replace(",", "");
        textSalary.setText(Salary_formatted);     
        textExperience.setText(driverTableModel.getValueAt(selectedRow, 6).toString());
    }
    
    private void addDriver() {
        Driver driver = getDriverFromForm();
        if (driver == null) return;
        
        driver.setId(null); 

        boolean done = driverService.AddNewDriver(driver);
        
        if (done) {

            JOptionPane.showMessageDialog(this, "Driver added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadDriverData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add Bus", "Logic Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateDriver() {
        String id = textId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a Driver to update from the table.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Driver driver = getDriverFromForm();
        if (driver == null) return;
        
        driver.setId(id);
        
        boolean done = driverService.UpdateDriver(driver);
        
        if (done) {
            JOptionPane.showMessageDialog(this, "Driver updated successfully!", "DONE", JOptionPane.INFORMATION_MESSAGE);
            loadDriverData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update Driver", "Logic Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeDriver() {
        String idToRemove = textId.getText().trim();
        if (idToRemove.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a Driver to remove from the table.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure to delete Driver ID " + idToRemove + "? This operation might fail if the Driver is currently assigned.", 
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean done= driverService.RemoveDriver(idToRemove);

            if (done) {
                JOptionPane.showMessageDialog (this, "Driver deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadDriverData();
                clearForm();
            } else {
                 JOptionPane.showMessageDialog(this, 
                     "Failed to delete Bus. This Driver might be currently assigned to a route or an error occurred.", 
                     "Logic Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        textId.setText("");
        textName.setText("");
        textPhone.setText("");
        textAddress.setText("");
        textLicense.setText("");
        textSalary.setText("");
        textExperience.setText("");
        driverTable.clearSelection();
    }
}
