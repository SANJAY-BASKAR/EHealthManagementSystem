import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class EHealthcareManagementSystem extends JFrame {
    private JTextField usernameField, passwordField;
    private JTextField patientNameField, patientAgeField, diagnosisField, newUserField, newPasswordField;
    private JTextArea viewPatientDetailsArea;
    private JComboBox<String> patientSelectComboBox;
    private JButton loginButton, addPatientButton, viewPatientButton, addDiagnosisButton, logoutButton, addUserButton;
    private JPanel mainPanel, loginPanel, formPanel, sideMenu;
    private Connection conn;

    public EHealthcareManagementSystem() {
        // Initialize the frame
        setTitle("E-Healthcare Management System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Set colors and fonts for UI styling
        Color backgroundColor = new Color(240, 240, 240);
        Color sideMenuColor = new Color(50, 50, 100);
        Font defaultFont = new Font("Arial", Font.PLAIN, 14);

        // Side menu panel
        sideMenu = new JPanel();
        sideMenu.setLayout(new BoxLayout(sideMenu, BoxLayout.Y_AXIS));
        sideMenu.setBackground(sideMenuColor);
        sideMenu.setPreferredSize(new Dimension(200, 600));
        sideMenu.setVisible(false); // Initially hide the side menu

        addPatientButton = createStyledButton("Add Patient", sideMenuColor);
        viewPatientButton = createStyledButton("View Patient", sideMenuColor);
        addDiagnosisButton = createStyledButton("Add Diagnosis", sideMenuColor);
        addUserButton = createStyledButton("Add User", sideMenuColor);
        logoutButton = createStyledButton("Logout", sideMenuColor);

        sideMenu.add(addPatientButton);
        sideMenu.add(viewPatientButton);
        sideMenu.add(addDiagnosisButton);
        sideMenu.add(addUserButton);
        sideMenu.add(Box.createVerticalGlue()); // To push Logout to the bottom
        sideMenu.add(logoutButton);

        add(sideMenu, BorderLayout.WEST);

        // Main panel for displaying forms and data
        mainPanel = new JPanel(new CardLayout());
        mainPanel.setBackground(backgroundColor);
        add(mainPanel, BorderLayout.CENTER);

        // Login form panel
        loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(backgroundColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        usernameField = new JTextField();
        passwordField = new JTextField();
        loginButton = new JButton("Login");
        loginButton.setFont(defaultFont);

        gbc.gridx = 0;
        gbc.gridy = 0;
        loginPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        loginPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        loginPanel.add(passwordField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        loginPanel.add(loginButton, gbc);

        mainPanel.add(loginPanel, "Login");

        // Add Patient form panel
        formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(backgroundColor);

        patientNameField = new JTextField();
        patientAgeField = new JTextField();
        JButton savePatientButton = new JButton("Save Patient");

        formPanel.add(new JLabel("Patient Name:"));
        formPanel.add(patientNameField);
        formPanel.add(new JLabel("Patient Age:"));
        formPanel.add(patientAgeField);
        formPanel.add(new JLabel(""));
        formPanel.add(savePatientButton);

        mainPanel.add(formPanel, "AddPatient");

        // View Patient details panel
        JPanel viewPanel = new JPanel(new BorderLayout());
        viewPatientDetailsArea = new JTextArea(10, 30);
        viewPatientDetailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(viewPatientDetailsArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        viewPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(viewPanel, "ViewPatient");

        // Add Diagnosis panel
        JPanel diagnosisPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        diagnosisPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        diagnosisPanel.setBackground(backgroundColor);

        diagnosisField = new JTextField();
        patientSelectComboBox = new JComboBox<>();
        JButton saveDiagnosisButton = new JButton("Save Diagnosis");

        diagnosisPanel.add(new JLabel("Select Patient:"));
        diagnosisPanel.add(patientSelectComboBox);
        diagnosisPanel.add(new JLabel("Diagnosis Test:"));
        diagnosisPanel.add(diagnosisField);
        diagnosisPanel.add(new JLabel(""));
        diagnosisPanel.add(saveDiagnosisButton);

        mainPanel.add(diagnosisPanel, "AddDiagnosis");

        // Add User panel
        JPanel addUserPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        addUserPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        addUserPanel.setBackground(backgroundColor);

        newUserField = new JTextField();
        newPasswordField = new JTextField();
        JButton saveUserButton = new JButton("Save User");

        addUserPanel.add(new JLabel("New Username:"));
        addUserPanel.add(newUserField);
        addUserPanel.add(new JLabel("New Password:"));
        addUserPanel.add(newPasswordField);
        addUserPanel.add(new JLabel(""));
        addUserPanel.add(saveUserButton);

        mainPanel.add(addUserPanel, "AddUser");

        // Action listeners for side menu buttons
        addPatientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCard("AddPatient");
            }
        });

        viewPatientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCard("ViewPatient");
                loadPatientDetails();
            }
        });

        addDiagnosisButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCard("AddDiagnosis");
                loadPatientsForDiagnosis();
            }
        });

        addUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCard("AddUser");
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCard("Login");
                sideMenu.setVisible(false); // Hide the side menu on logout
                usernameField.setText("");
                passwordField.setText("");
            }
        });

        // Initialize database connection
        initializeDatabase();

        // Login button action
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (authenticate(usernameField.getText(), passwordField.getText())) {
                    showCard("AddPatient");
                    sideMenu.setVisible(true); // Show the side menu on successful login
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid credentials");
                }
            }
        });

        // Save patient button action
        savePatientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePatientDetails(patientNameField.getText(), Integer.parseInt(patientAgeField.getText()));
            }
        });

        // Save diagnosis button action
        saveDiagnosisButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedPatient = (String) patientSelectComboBox.getSelectedItem();
                if (selectedPatient != null) {
                    int patientId = Integer.parseInt(selectedPatient.split(":")[0]);  // Assuming format "ID: Name"
                    saveDiagnosis(patientId, diagnosisField.getText());
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a patient.");
                }
            }
        });

        // Save user button action
        saveUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveUser(newUserField.getText(), newPasswordField.getText());
            }
        });
    }

    // Helper method to create styled side menu buttons
    private JButton createStyledButton(String text, Color sideMenuColor) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(sideMenuColor.darker());
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    private void showCard(String cardName) {
        CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
        cardLayout.show(mainPanel, cardName);
    }

    private void initializeDatabase() {
        // Initialize the PostgreSQL database connection
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ehealthcare", "postgres", "sanjay2005");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean authenticate(String username, String password) {
        // Authentication logic using PostgreSQL
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void savePatientDetails(String name, int age) {
        // Save patient details into PostgreSQL
        try {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO patients (name, age) VALUES (?, ?)");
            stmt.setString(1, name);
            stmt.setInt(2, age);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Patient saved!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadPatientDetails() {
        // Load patient details along with diagnosis history from PostgreSQL
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT p.id, p.name, p.age, d.diagnosis " +
                            "FROM patients p LEFT JOIN diagnosis d ON p.id = d.patient_id"
            );
            viewPatientDetailsArea.setText("");
            int currentPatientId = -1;
            while (rs.next()) {
                int patientId = rs.getInt("id");
                if (patientId != currentPatientId) {
                    // Print patient details
                    viewPatientDetailsArea.append("\nPatient ID: " + patientId +
                            ", Name: " + rs.getString("name") +
                            ", Age: " + rs.getInt("age") + "\n");
                    currentPatientId = patientId;
                }
                // Print diagnosis (if available)
                String diagnosis = rs.getString("diagnosis");
                if (diagnosis != null) {
                    viewPatientDetailsArea.append("  Diagnosis: " + diagnosis + "\n");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadPatientsForDiagnosis() {
        // Load patient names into the combo box for diagnosis entry
        try {
            patientSelectComboBox.removeAllItems();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, name FROM patients");
            while (rs.next()) {
                patientSelectComboBox.addItem(rs.getInt("id") + ": " + rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveDiagnosis(int patientId, String diagnosis) {
        // Save diagnosis into PostgreSQL for a specific patient
        try {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO diagnosis (patient_id, diagnosis) VALUES (?, ?)");
            stmt.setInt(1, patientId);
            stmt.setString(2, diagnosis);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Diagnosis saved!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveUser(String newUsername, String newPassword) {
        // Save new user into PostgreSQL
        try {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
            stmt.setString(1, newUsername);
            stmt.setString(2, newPassword);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "User added!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        EHealthcareManagementSystem app = new EHealthcareManagementSystem();
        app.setVisible(true);
    }
}
