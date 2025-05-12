package employee;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.text.NumberFormat;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.util.Locale;

public class addEmployee extends JFrame {
    private static final String DB_URL = "jdbc:mysql://dif-mysql.ehu.es:23306/DBI08";
    private static final String USER = "DBI08";
    private static final String PASS = "DBI08";
    
    // UI Components
    private JTextField txtFname, txtLname, txtSsn;
    private JComboBox<Integer> cboDno  ;
    private JButton btnConnect, btnAdd, btnRefresh, btnExit;
    private JTable tblEmployees;
    private DefaultTableModel tableModel;
    private JLabel lblStatus;
    private Connection conn = null; 
    
    public addEmployee() {
        // Set up JFrame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        
        // Create components
        initComponents();
        
        // Set layout
        layoutComponents();
        
        // Add event listeners
        addEventListeners();
        
        // Show the frame
        setVisible(true);
    }
    
    private void initComponents() {
        // Create input fields
        txtFname = new JTextField(20);
        txtLname = new JTextField(20);
        txtSsn = new JTextField(11);
        
        // Create department dropdown
        cboDno = new JComboBox<>();
        // Populate with some example departments
        for (int i = 1; i <= 8; i++) {
            cboDno.addItem(i);
        }
        
        // Create buttons with modern look
        btnConnect = createStyledButton("Connect", new Color(45, 62, 80));
        btnAdd = createStyledButton("Add Employee", new Color(46, 125, 50));
        btnRefresh = createStyledButton("Refresh", new Color(3, 155, 229));
        btnExit = createStyledButton("Exit", new Color(211, 47, 47));
        
        // Create status label
        lblStatus = new JLabel("Not connected to database");
        lblStatus.setForeground(new Color(211, 47, 47));
        lblStatus.setFont(new Font("Arial", Font.BOLD, 12));
        
        // Create table model
        String[] columnNames = {"First Name", "Last Name", "Department", "Dept No", "Projects", "Dependents"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        
        // Create table
        tblEmployees = new JTable(tableModel);
        tblEmployees.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblEmployees.setRowHeight(25);
        tblEmployees.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Set column widths
        TableColumnModel columnModel = tblEmployees.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(100);
        columnModel.getColumn(1).setPreferredWidth(100);
        columnModel.getColumn(2).setPreferredWidth(200);
        columnModel.getColumn(3).setPreferredWidth(60);
        columnModel.getColumn(4).setPreferredWidth(60);
        columnModel.getColumn(5).setPreferredWidth(80);
        
        // Style the table header
        JTableHeader header = tblEmployees.getTableHeader();
        header.setBackground(new Color(66, 96, 130));
        header.setForeground(Color.black);
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(42, 54, 80)));
    }
     
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(bgColor.darker());
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private void layoutComponents() {
        // Set main layout
        setLayout(new BorderLayout());
        
        // Create statement panel at top
        JPanel pnlStatement = new JPanel(new BorderLayout());
        pnlStatement.setBackground(new Color(10, 100, 150));
        JTextArea txtStatement = new JTextArea(
        		"1- Retrieve the employee Fname and Lname, its department name and number, the number of projects " +
        	            "they work on and the number of dependents they have, of all the employees that all the projects they " +
        	            "work on corresponds to their department and where those employees have at least a son or a daughter " +
        	            "as a dependent. The result Will be sorted descently by the number of projects and in case of tie, by the " +
        	            "number of dependents.");
        txtStatement.setEditable(false);
        txtStatement.setLineWrap(true);
        txtStatement.setWrapStyleWord(true);
        txtStatement.setBackground(new Color(10, 100, 150));
        txtStatement.setForeground(Color.WHITE);
        txtStatement.setFont(new Font("Arial", Font.BOLD, 12));
        txtStatement.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlStatement.add(txtStatement, BorderLayout.CENTER);
        
        // Create input panel
        JPanel pnlInput = new JPanel(new GridBagLayout());
        pnlInput.setBackground(new Color(240, 248, 255));
        pnlInput.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // First Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        pnlInput.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        pnlInput.add(txtFname, gbc);
        
        // Last Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        pnlInput.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        pnlInput.add(txtLname, gbc);
        
        // SSN
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        pnlInput.add(new JLabel("SSN:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        pnlInput.add(txtSsn, gbc);
        
        // Department
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        pnlInput.add(new JLabel("Department No:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        pnlInput.add(cboDno, gbc);
        
        // Add button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15, 5, 5, 5);
        pnlInput.add(btnAdd, gbc);
        
        // Create table panel (center)
        JPanel pnlTable = new JPanel(new BorderLayout());
        pnlTable.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        pnlTable.add(new JLabel("Result:"), BorderLayout.NORTH);
        
        // Create custom scroll pane with styled scrollbar
        JScrollPane scrollPane = new JScrollPane(tblEmployees);
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(66, 96, 130);
                this.trackColor = new Color(240, 240, 240);
            }
            
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });
        
        pnlTable.add(scrollPane, BorderLayout.CENTER);
        
        // Create bottom panel
        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setBackground(new Color(176, 224, 230));
        
        // Status panel on left
        JPanel pnlStatus = new JPanel();
        pnlStatus.setBackground(new Color(176, 224, 230));
        pnlStatus.add(lblStatus);
        pnlBottom.add(pnlStatus, BorderLayout.WEST);
        
        // Buttons panel on right
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlButtons.setBackground(new Color(176, 224, 230));
        pnlButtons.add(btnConnect);
        pnlButtons.add(btnRefresh);
        pnlButtons.add(btnExit);
        pnlBottom.add(pnlButtons, BorderLayout.EAST);
        
        // Add all panels to the frame
        add(pnlStatement, BorderLayout.NORTH);
        add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pnlInput, pnlTable), BorderLayout.CENTER);
        add(pnlBottom, BorderLayout.SOUTH);
    }
    
    private void addEventListeners() {
        // Connect button action
        btnConnect.addActionListener(e -> connectToDatabase());
        
        // Add Employee button action
        btnAdd.addActionListener(e -> addEmployee());
        
        // Refresh button action
        btnRefresh.addActionListener(e -> refreshEmployeeData());
        
        // Exit button action
        btnExit.addActionListener(e -> System.exit(0));
    }
    
    private void connectToDatabase() {
        try {
            // Close existing connection if any
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
            
            // Create new connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            
            // Update status
            lblStatus.setText("Connected to database");
            lblStatus.setForeground(new Color(46, 125, 50));
            
            // Refresh employee data
            refreshEmployeeData();
            
            // Load departments into combo box
            loadDepartments();
            
        } catch (ClassNotFoundException | SQLException ex) {
            lblStatus.setText("Connection failed: " + ex.getMessage());
            lblStatus.setForeground(new Color(211, 47, 47));
            JOptionPane.showMessageDialog(this, 
                "Database connection error: " + ex.getMessage(), 
                "Connection Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadDepartments() {
        try {
            cboDno.removeAllItems();
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Dnumber FROM department ORDER BY Dnumber");
            
            while (rs.next()) {
                cboDno.addItem(rs.getInt("Dnumber"));
            }
            
            rs.close();
            stmt.close();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error loading departments: " + ex.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addEmployee() {
        // Validate inputs
        if (txtFname.getText().trim().isEmpty() || 
            txtLname.getText().trim().isEmpty() || 
            txtSsn.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please fill in all fields", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Check if connected to database
        if (conn == null) {
            JOptionPane.showMessageDialog(this, 
                "Please connect to the database first", 
                "Not Connected", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Disable auto-commit
            conn.setAutoCommit(false);
            
            // Prepare statement
            PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO employee VALUES (?, null, ?, ?, null, null, null, null, null, ?)");
            
            // Set parameters
            pstmt.setString(1, txtFname.getText().trim());
            pstmt.setString(2, txtLname.getText().trim());
            pstmt.setString(3, txtSsn.getText().trim());
            pstmt.setInt(4, (Integer) cboDno.getSelectedItem());
            
            // Execute the insert
            pstmt.executeUpdate();
            
            // Commit the transaction
            conn.commit();
            
            // Show success message
            JOptionPane.showMessageDialog(this, 
                "Employee added successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Clear the form
            txtFname.setText("");
            txtLname.setText("");
            txtSsn.setText("");
            
            // Refresh the employee data
            refreshEmployeeData();
            
        } catch (SQLException ex) {
            // Rollback on error
            try {
                conn.rollback();
            } catch (SQLException e) {
                System.out.println("Error during rollback: " + e.getMessage());
            }
            
            JOptionPane.showMessageDialog(this, 
                "Error adding employee: " + ex.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void refreshEmployeeData() {
        // Clear the table
        tableModel.setRowCount(0);
        
        // Check if connected
        if (conn == null) {
            return;
        }
        
        try {
            Statement stmt = conn.createStatement();
            
            // Modified query to show ALL employees
            ResultSet rs = stmt.executeQuery(
                "SELECT e.Fname, e.Lname, d.Dname, e.Dno, " +
                "COUNT(DISTINCT w.Pno) AS ProjectCount, " +
                "COUNT(DISTINCT de.Essn) AS DependentCount " +
                "FROM employee e " +
                "LEFT JOIN department d ON e.Dno = d.Dnumber " +
                "LEFT JOIN works_on w ON e.Ssn = w.Essn " +
                "LEFT JOIN dependent de ON e.Ssn = de.Essn " +
                "GROUP BY e.Ssn, e.Fname, e.Lname, d.Dname, e.Dno " +
                "ORDER BY e.Lname, e.Fname");
            
            // Populate the table
            while (rs.next()) {
                Object[] row = {
                    rs.getString("Fname"),
                    rs.getString("Lname"),
                    rs.getString("Dname"),
                    rs.getInt("Dno"),
                    rs.getInt("ProjectCount"),
                    rs.getInt("DependentCount")
                };
                tableModel.addRow(row);
            }
            
            rs.close();
            stmt.close();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error refreshing data: " + ex.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Create the GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new addEmployee());
    }
}