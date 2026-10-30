import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class HospitalManagementSystem extends Frame implements ActionListener {
    // Database connection details
    private static final String URL = "jdbc:mysql://localhost:3306/hospital"; // Change to your database name
    private static final String USER = "root"; // Change to your username
    private static final String PASSWORD = "root"; // Change to your password

    // Components for the GUI
    private TextField idField, nameField, ageField, dobField, organTypeField, hIdField, specializationField;
    private Button addPatientButton, addDonorButton, addOrganButton, addDoctorButton, displayButton, updateButton, deleteButton;
    private TextArea displayArea;

    public HospitalManagementSystem() {
        // Setting up the Frame
        setTitle("Hospital Management System");
        setSize(600, 400);
        setLayout(new FlowLayout());

        // Patient Fields
        add(new Label("ID:"));
        idField = new TextField(10);
        add(idField);
        
        add(new Label("Name:"));
        nameField = new TextField(20);
        add(nameField);

        add(new Label("Age:"));
        ageField = new TextField(3);
        add(ageField);
        
        add(new Label("DOB (YYYY-MM-DD):"));
        dobField = new TextField(10);
        add(dobField);
        
        add(new Label("Organ Type:"));
        organTypeField = new TextField(15);
        add(organTypeField);

        add(new Label("Hospital ID:"));
        hIdField = new TextField(5);
        add(hIdField);
        
        add(new Label("Specialization:"));
        specializationField = new TextField(20);
        add(specializationField);

        // Buttons for CRUD operations
        addPatientButton = new Button("Add Patient");
        addPatientButton.addActionListener(this);
        add(addPatientButton);
        
        addDonorButton = new Button("Add Donor");
        addDonorButton.addActionListener(this);
        add(addDonorButton);
        
        addOrganButton = new Button("Add Organ Registration");
        addOrganButton.addActionListener(this);
        add(addOrganButton);
        
        addDoctorButton = new Button("Add Doctor");
        addDoctorButton.addActionListener(this);
        add(addDoctorButton);

        updateButton = new Button("Update Patient");
        updateButton.addActionListener(this);
        add(updateButton);

        deleteButton = new Button("Delete Patient");
        deleteButton.addActionListener(this);
        add(deleteButton);

        displayButton = new Button("Display All Patients");
        displayButton.addActionListener(this);
        add(displayButton);

        // Text area to display results
        displayArea = new TextArea(10, 50);
        displayArea.setEditable(false);
        add(displayArea);

        // Closing the frame
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
        
        setVisible(true);
    }

    public static void main(String[] args) {
        new HospitalManagementSystem();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addPatientButton) {
            addPatient();
        } else if (e.getSource() == addDonorButton) {
            addDonor();
        } else if (e.getSource() == addOrganButton) {
            addOrganRegistration();
        } else if (e.getSource() == addDoctorButton) {
            addDoctor();
        } else if (e.getSource() == updateButton) {
            updatePatient();
        } else if (e.getSource() == deleteButton) {
            deletePatient();
        } else if (e.getSource() == displayButton) {
            displayAllPatients();
        }
    }

    private void addPatient() {
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = con.prepareStatement("INSERT INTO Patient (P_ID, Name, organ_type, DOB, Age) VALUES (?, ?, ?, ?, ?)")) {
            ps.setInt(1, Integer.parseInt(idField.getText()));
            ps.setString(2, nameField.getText());
            ps.setString(3, organTypeField.getText());
            ps.setDate(4, Date.valueOf(dobField.getText()));
            ps.setInt(5, Integer.parseInt(ageField.getText()));
            ps.executeUpdate();
            displayArea.setText("Patient added successfully!");
        } catch (SQLException ex) {
            displayArea.setText("Error: " + ex.getMessage());
        }
    }

    
    private void addDonor() {
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement checkHospitalStmt = con.prepareStatement("SELECT * FROM Hospital WHERE H_ID = ?")) {
            
            // Check if the Hospital ID exists
            checkHospitalStmt.setInt(1, Integer.parseInt(hIdField.getText()));
            ResultSet rs = checkHospitalStmt.executeQuery();
            
            if (!rs.next()) {
                displayArea.setText("Error: Hospital with ID " + hIdField.getText() + " does not exist.");
                return;
            }

            // If the hospital exists, proceed to insert the donor
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO Donor (D_ID, H_ID, Name, Age, DOB) VALUES (?, ?, ?, ?, ?)")) {
                ps.setInt(1, Integer.parseInt(idField.getText()));
                ps.setInt(2, Integer.parseInt(hIdField.getText()));
                ps.setString(3, nameField.getText());
                ps.setInt(4, Integer.parseInt(ageField.getText()));
                ps.setDate(5, Date.valueOf(dobField.getText()));
                ps.executeUpdate();
                displayArea.setText("Donor added successfully!");
            }
        } catch (SQLException ex) {
            displayArea.setText("Error: " + ex.getMessage());
        }
    }


    private void addOrganRegistration() {
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = con.prepareStatement("INSERT INTO Organ_Registration (Organ_ID, Donor_ID) VALUES (?, ?)")) {
            ps.setInt(1, Integer.parseInt(idField.getText()));
            ps.setInt(2, Integer.parseInt(idField.getText())); // Assuming Donor_ID is the same as P_ID for simplicity
            ps.executeUpdate();
            displayArea.setText("Organ registration added successfully!");
        } catch (SQLException ex) {
            displayArea.setText("Error: " + ex.getMessage());
        }
    }

    private void addDoctor() {
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = con.prepareStatement("INSERT INTO DOCTOR (D_ID, Name, H_ID, specialization) VALUES (?, ?, ?, ?)")) {
            ps.setInt(1, Integer.parseInt(idField.getText()));
            ps.setString(2, nameField.getText());
            ps.setInt(3, Integer.parseInt(hIdField.getText()));
            ps.setString(4, specializationField.getText());
            ps.executeUpdate();
            displayArea.setText("Doctor added successfully!");
        } catch (SQLException ex) {
            displayArea.setText("Error: " + ex.getMessage());
        }
    }

    private void updatePatient() {
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = con.prepareStatement("UPDATE Patient SET Name=?, organ_type=?, DOB=?, Age=? WHERE P_ID=?")) {
            ps.setString(1, nameField.getText());
            ps.setString(2, organTypeField.getText());
            ps.setDate(3, Date.valueOf(dobField.getText()));
            ps.setInt(4, Integer.parseInt(ageField.getText()));
            ps.setInt(5, Integer.parseInt(idField.getText()));
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                displayArea.setText("Patient updated successfully!");
            } else {
                displayArea.setText("No patient found with that ID.");
            }
        } catch (SQLException ex) {
            displayArea.setText("Error: " + ex.getMessage());
        }
    }

    private void deletePatient() {
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = con.prepareStatement("DELETE FROM Patient WHERE P_ID=?")) {
            ps.setInt(1, Integer.parseInt(idField.getText()));
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                displayArea.setText("Patient deleted successfully!");
            } else {
                displayArea.setText("No patient found with that ID.");
            }
        } catch (SQLException ex) {
            displayArea.setText("Error: " + ex.getMessage());
        }
    }

    private void displayAllPatients() {
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Patient")) {
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("ID: ").append(rs.getInt("P_ID"))
                  .append(", Name: ").append(rs.getString("Name"))
                  .append(", Organ Type: ").append(rs.getString("organ_type"))
                  .append(", DOB: ").append(rs.getDate("DOB"))
                  .append(", Age: ").append(rs.getInt("Age"))
                  .append("\n");
            }
            displayArea.setText(sb.toString());
        } catch (SQLException ex) {
            displayArea.setText("Error: " + ex.getMessage());
        }
    }
}
