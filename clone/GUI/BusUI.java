
package GUI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import project.oop.service.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.*;
import project.oop.Bus;

public class BusUI extends JPanel {
    private final BusService busService;
    private final AssignmentService assignmentService;
    
    private DefaultTableModel busTableModel; 
    private JTable busTable;
    
    private JTextField textId;
    private JTextField textLicensePlate;
    private JTextField textCapacity;
    private JTextField textModel;
    private JTextField textYear;
    private JCheckBox checkActive;
    
    public BusUI(BusService busService, AssignmentService assignmentService) {
        this.busService = busService;
        this.assignmentService = assignmentService;
        setLayout(new BorderLayout(10, 10));
        initializeTable();
        initializeInputForm();
        
        add(new JScrollPane(busTable), BorderLayout.CENTER);
        add(createControlPanel(), BorderLayout.SOUTH);
        add(initializeInputForm(), BorderLayout.NORTH);
        setupRowSelectionListener();
        loadBusData();
    }
    
    private void initializeTable() {

        String[] columnNames = {"Id", "License Plate", "Capacity", "Model", "Purchase Year", "Status"};
        busTableModel = new DefaultTableModel(columnNames, 0) {
             @Override
             public boolean isCellEditable(int row, int column) {
                 return false; 
             }
        };
        busTable = new JTable(busTableModel);
    }
    
    private JPanel initializeInputForm() {
        JPanel panel = new JPanel (new GridLayout(6,2,10,5));
        
        textId= new JTextField();
        textId.setEditable(false);
        textLicensePlate = new JTextField();
        textCapacity = new JTextField();
        textModel = new JTextField();
        textYear = new JTextField();
        checkActive = new JCheckBox("Active", true);
        
        panel.setBorder(BorderFactory.createTitledBorder("Bus information"));
        panel.add(new JLabel("ID Bus:"));
        panel.add(textId);
        panel.add(new JLabel("License Plate:"));
        panel.add(textLicensePlate);
        panel.add(new JLabel("Capacity (Seats):"));
        panel.add(textCapacity);
        panel.add(new JLabel("Model:"));
        panel.add(textModel);
        panel.add(new JLabel("Purchase Year:"));
        panel.add(textYear);
        panel.add(new JLabel("Status:"));
        panel.add(checkActive);
        
        return panel;
    }
    
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnRemove = new JButton("Delete");
        JButton btnClear = new JButton("Refresh");

        btnAdd.addActionListener(e -> addBus());
        btnUpdate.addActionListener(e -> updateBus());
        btnRemove.addActionListener(e -> removeBus());
        btnClear.addActionListener(e -> clearForm());

        controlPanel.add(btnAdd);
        controlPanel.add(btnUpdate);
        controlPanel.add(btnRemove);
        controlPanel.add(btnClear);
        
        return controlPanel;
    }
    private Bus getBusFromForm() {
        try {
            Bus bus = new Bus();
            bus.setId (textId.getText().trim()); 
            bus.setLicensePlate( textLicensePlate.getText().trim());
            bus.setModel (textModel.getText().trim());
            
            bus.setCapacity (Integer.parseInt(textCapacity.getText().trim()));
            bus.setPurchaseYear (Integer.parseInt(textYear.getText().trim())); 
            
            bus.setActive(checkActive.isSelected());
            return bus;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Capacity and Purchase Year must be valid integers.", "Data Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    private void loadBusData() {
        busTableModel.setRowCount(0); 
        
        List<Bus> busList = busService.GetAllBuses();
        
        for (Bus bus : busList) {
            busTableModel.addRow(new Object[]{
                bus.getId(),
                bus.getLicensePlate(),
                bus.getCapacity(),
                bus.getModel(),
                bus.getPurchaseYear(),
                bus.isActive() ? "Active" : "Inactive"
            });
        }
    }
    private void setupRowSelectionListener() {
        busTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && busTable.getSelectedRow() != -1) {
                int selectedRow = busTable.getSelectedRow();
                setFormValuesFromTable(selectedRow);
            }
        });
    }
    private void setFormValuesFromTable(int selectedRow) {
        textId.setText(busTableModel.getValueAt(selectedRow, 0).toString());
        textLicensePlate.setText(busTableModel.getValueAt(selectedRow, 1).toString());
        textModel.setText(busTableModel.getValueAt(selectedRow, 2).toString());
        textCapacity.setText(busTableModel.getValueAt(selectedRow, 3).toString());
        textYear.setText(busTableModel.getValueAt(selectedRow, 4).toString());
        checkActive.setSelected(busTableModel.getValueAt(selectedRow, 5).equals("Active"));
    }
    
    private void addBus() {
        Bus bus = getBusFromForm();
        if (bus == null) return;
        
        bus.setId(null); 

        boolean success = busService.AddNewBus(bus);
        
        if (success) {

            JOptionPane.showMessageDialog(this, "Bus added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadBusData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add Bus", "Logic Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateBus() {
        String id = textId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a Bus to update from the table.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Bus bus = getBusFromForm();
        if (bus == null) return;
        
        bus.setId(id);
        
        boolean done = busService.UpdateBus(bus);
        
        if (done) {
            JOptionPane.showMessageDialog(this, "Bus updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadBusData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update Bus", "Logic Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeBus() {
        String idToRemove = textId.getText().trim();
        if (idToRemove.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a Bus to remove from the table.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure to delete Bus ID " + idToRemove + "? This operation might fail if the Bus is currently assigned.", 
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean done= busService.RemoveBus(idToRemove);

            if (done) {
                JOptionPane.showMessageDialog (this, "Bus deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadBusData();
                clearForm();
            } else {
                 JOptionPane.showMessageDialog(this, 
                     "Failed to delete Bus. This Bus might be currently assigned to a route or an error occurred.", 
                     "Business Logic Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        textId.setText ("");
        textLicensePlate.setText("");
        textModel.setText ("");
        textCapacity.setText ("");
        textYear.setText("");
        checkActive.setSelected (true);
        busTable.clearSelection();
    }
}
