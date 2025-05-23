package company;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.util.Vector;

/**
 * Modernized graphical interface for adding a new employee
 * With database table showing current employees
 */
public class AddEmployee extends JFrame {
    private JPanel contentPane;
    private JTextField txtFname;
    private JTextField txtLname;
    private JTextField txtSsn;
    private JTextField txtDno;
    private JTextField txtStatus;
    private JButton btnAdd;
    private JButton btnClear;
    private JButton btnShowDepartments;
    private JButton btnRefresh;
    private JButton btnExit;
    private JTable employeesTable;
    private DefaultTableModel employeesTableModel;
    
    private static final String DB_URL = "jdbc:mysql://dif-mysql.ehu.es:23306/DBI08";
    private static final String USER = "DBI08";
    private static final String PASS = "DBI08";
    
    // Green color theme for Employee package
    private static final Color DARK_GREEN = new Color(25, 80, 45);
    private static final Color MEDIUM_GREEN = new Color(46, 125, 50);
    private static final Color LIGHT_GREEN = new Color(129, 199, 132);
    private static final Color VERY_LIGHT_GREEN = new Color(232, 245, 233);
    private static final Color TEXT_COLOR = new Color(33, 33, 33);
    private static final Color ACCENT_COLOR = new Color(76, 175, 80);
    
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    AddEmployee frame = new AddEmployee();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public AddEmployee() {
        setTitle("Add New Employee");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 900, 700);
        setLocationRelativeTo(null);
        
        // Create gradient panel
        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, DARK_GREEN,
                                                  getWidth(), getHeight(), new Color(40, 110, 60));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        contentPane.setBorder(BorderFactory.createLineBorder(DARK_GREEN, 2));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));
        
        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setPreferredSize(new Dimension(900, 80));
        headerPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Add New Employee");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JLabel subtitleLabel = new JLabel("Add a new employee to the database");
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleLabel.setForeground(new Color(220, 255, 220));
        subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        contentPane.add(headerPanel, BorderLayout.NORTH);
        
        // Main split pane to divide form and table
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(250);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);
        contentPane.add(splitPane, BorderLayout.CENTER);
        
        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(null);
        splitPane.setTopComponent(formPanel);
        
        // First Name
        JLabel lblFname = new JLabel("First Name:");
        lblFname.setForeground(Color.WHITE);
        lblFname.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFname.setBounds(150, 30, 100, 30);
        formPanel.add(lblFname);
        
        txtFname = new JTextField();
        txtFname.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtFname.setBounds(250, 30, 250, 30);
        txtFname.setBackground(VERY_LIGHT_GREEN);
        txtFname.setForeground(TEXT_COLOR);
        txtFname.setBorder(BorderFactory.createLineBorder(MEDIUM_GREEN));
        formPanel.add(txtFname);
        
        // Last Name
        JLabel lblLname = new JLabel("Last Name:");
        lblLname.setForeground(Color.WHITE);
        lblLname.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblLname.setBounds(150, 70, 100, 30);
        formPanel.add(lblLname);
        
        txtLname = new JTextField();
        txtLname.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtLname.setBounds(250, 70, 250, 30);
        txtLname.setBackground(VERY_LIGHT_GREEN);
        txtLname.setForeground(TEXT_COLOR);
        txtLname.setBorder(BorderFactory.createLineBorder(MEDIUM_GREEN));
        formPanel.add(txtLname);
        
        // SSN
        JLabel lblSsn = new JLabel("SSN:");
        lblSsn.setForeground(Color.WHITE);
        lblSsn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSsn.setBounds(150, 110, 100, 30);
        formPanel.add(lblSsn);
        
        txtSsn = new JTextField();
        txtSsn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSsn.setBounds(250, 110, 250, 30);
        txtSsn.setBackground(VERY_LIGHT_GREEN);
        txtSsn.setForeground(TEXT_COLOR);
        txtSsn.setBorder(BorderFactory.createLineBorder(MEDIUM_GREEN));
        formPanel.add(txtSsn);
        
        // Department Number
        JLabel lblDno = new JLabel("Department:");
        lblDno.setForeground(Color.WHITE);
        lblDno.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblDno.setBounds(150, 150, 100, 30);
        formPanel.add(lblDno);
        
        txtDno = new JTextField();
        txtDno.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDno.setBounds(250, 150, 250, 30);
        txtDno.setBackground(VERY_LIGHT_GREEN);
        txtDno.setForeground(TEXT_COLOR);
        txtDno.setBorder(BorderFactory.createLineBorder(MEDIUM_GREEN));
        formPanel.add(txtDno);
        
        // Form Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setBounds(140, 190, 460, 50);
        formPanel.add(buttonPanel);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        // Add button
        btnAdd = createStyledButton("Add Employee");
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addEmployeeToDatabase();
            }
        });
        buttonPanel.add(btnAdd);
        
        // Show Departments button
        btnShowDepartments = createStyledButton("Show Departments");
        btnShowDepartments.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showDepartments();
            }
        });
        buttonPanel.add(btnShowDepartments);
        
        // Clear button
        btnClear = createStyledButton("Clear Form");
        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
        buttonPanel.add(btnClear);
        
        // Employees database table panel
        JPanel tablePanel = new JPanel();
        tablePanel.setBackground(VERY_LIGHT_GREEN);
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(DARK_GREEN, 1),
            "Current Employees Database",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            DARK_GREEN
        ));
        tablePanel.setLayout(new BorderLayout(0, 0));
        splitPane.setBottomComponent(tablePanel);
        
        // Create table for employees
        String[] columnNames = {"SSN", "First Name", "Last Name","Department"};
        employeesTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4) return Double.class; // Salary column
                return String.class;
            }
        };
        
        employeesTable = new JTable(employeesTableModel);
        employeesTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        employeesTable.setRowHeight(25);
        employeesTable.setGridColor(LIGHT_GREEN);
        
        // Header styling
        JTableHeader header = employeesTable.getTableHeader();
        header.setBackground(MEDIUM_GREEN);
        header.setForeground(Color.green.darker());
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // Set alternating row colors
        employeesTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (isSelected) {
                    c.setBackground(LIGHT_GREEN);
                    c.setForeground(DARK_GREEN);
                } else {
                    c.setBackground(row % 2 == 0 ? VERY_LIGHT_GREEN : Color.WHITE);
                    c.setForeground(TEXT_COLOR);
                }

                return c;
            }
        });
        
        // Create scrollpane and style scrollbars
        JScrollPane scrollPane = new JScrollPane(employeesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = MEDIUM_GREEN;
                this.trackColor = VERY_LIGHT_GREEN;
            }
        });
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Footer panel with controls
        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        footerPanel.setPreferredSize(new Dimension(900, 100));
        contentPane.add(footerPanel, BorderLayout.SOUTH);
        footerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 30));
        
        // Status field
        txtStatus = new JTextField("Ready to add employee");
        txtStatus.setEditable(false);
        txtStatus.setForeground(new Color(220, 255, 220));
        txtStatus.setBackground(new Color(25, 80, 45));
        txtStatus.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtStatus.setBorder(null);
        txtStatus.setHorizontalAlignment(SwingConstants.CENTER);
        txtStatus.setPreferredSize(new Dimension(250, 35));
        footerPanel.add(txtStatus);
        
        // Refresh button
        btnRefresh = createStyledButton("Refresh Data");
        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadEmployeesData();
            }
        });
        footerPanel.add(btnRefresh);
        
        // Exit button
        btnExit = createStyledButton("Exit");
        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        footerPanel.add(btnExit);
        
        // Load initial employee data
        loadEmployeesData();
    }
    
    /**
     * Creates a styled button
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(DARK_GREEN);
                } else if (getModel().isRollover()) {
                    g2.setColor(ACCENT_COLOR);
                } else {
                    g2.setColor(MEDIUM_GREEN);
                }
                
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(new Color(255, 255, 255, 50));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                
                // Draw text
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                g2.setColor(Color.WHITE);
                
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int x = (getWidth() - textWidth) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(text, x, y);
                
                g2.dispose();
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(150, 35);
            }
        };
        
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    /**
     * Load employees data from database
     */
    private void loadEmployeesData() {
        txtStatus.setText("Loading employees data...");
        
        // Clear existing data
        employeesTableModel.setRowCount(0);
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
            
            // Query to get employee details with department and supervisor info
            String sql = "SELECT e.Ssn, e.Fname, e.Lname, e.Sex, e.Salary, d.Dname, " +
                        "(SELECT CONCAT(s.Fname, ' ', s.Lname) FROM employee s WHERE s.Ssn = e.Super_ssn) AS Supervisor " +
                        "FROM employee e " +
                        "LEFT JOIN department d ON e.Dno = d.Dnumber " +
                        "ORDER BY e.Ssn";
            
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                String ssn = rs.getString("Ssn");
                String fname = rs.getString("Fname");
                String lname = rs.getString("Lname");
            
                String deptName = rs.getString("Dname");
                
                employeesTableModel.addRow(new Object[] {
                    ssn, fname, lname, deptName
                });
            }
            
            txtStatus.setText("Employees data loaded successfully");
            
        } catch (Exception e) {
            txtStatus.setText("Error loading employees data");
            JOptionPane.showMessageDialog(this, 
                "Error loading employees data: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Clear form fields
     */
    private void clearForm() {
        txtFname.setText("");
        txtLname.setText("");
        txtSsn.setText("");
        txtDno.setText("");
        txtStatus.setText("Form cleared");
    }
    
    /**
     * Show available departments
     */
    private void showDepartments() {
        SwingWorker<JTable, Void> worker = new SwingWorker<JTable, Void>() {
            @Override
            protected JTable doInBackground() throws Exception {
                Connection conn = null;
                Statement stmt = null;
                ResultSet rs = null;
                
                try {
                    conn = DriverManager.getConnection(DB_URL, USER, PASS);
                    stmt = conn.createStatement();
                    rs = stmt.executeQuery("SELECT Dnumber, Dname, Mgr_ssn FROM department ORDER BY Dnumber");
                    
                    // Create table model
                    String[] columnNames = {"Dept No", "Department Name", "Manager SSN"};
                    DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
                        @Override
                        public boolean isCellEditable(int row, int column) {
                            return false;
                        }
                    };
                    
                    // Populate model
                    while (rs.next()) {
                        model.addRow(new Object[] {
                            rs.getInt("Dnumber"),
                            rs.getString("Dname"),
                            rs.getString("Mgr_ssn")
                        });
                    }
                    
                    // Create and configure table
                    JTable departmentsTable = new JTable(model);
                    departmentsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    departmentsTable.setRowHeight(25);
                    departmentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    
                    // Set colors
                    departmentsTable.setBackground(VERY_LIGHT_GREEN);
                    departmentsTable.setForeground(TEXT_COLOR);
                    departmentsTable.setSelectionBackground(LIGHT_GREEN);
                    departmentsTable.setSelectionForeground(DARK_GREEN);
                    
                    // Set header style
                    JTableHeader header = departmentsTable.getTableHeader();
                    header.setBackground(MEDIUM_GREEN);
                    header.setForeground(Color.green.darker());
                    header.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    
                    // Add selection listener
                    departmentsTable.getSelectionModel().addListSelectionListener(e -> {
                        if (!e.getValueIsAdjusting() && departmentsTable.getSelectedRow() != -1) {
                            int deptNo = (int) departmentsTable.getValueAt(departmentsTable.getSelectedRow(), 0);
                            txtDno.setText(String.valueOf(deptNo));
                        }
                    });
                    
                    return departmentsTable;
                    
                } finally {
                    try {
                        if (rs != null) rs.close();
                        if (stmt != null) stmt.close();
                        if (conn != null) conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            
            @Override
            protected void done() {
                try {
                    JTable departmentsTable = get();
                    
                    // Create scrollpane
                    JScrollPane scrollPane = new JScrollPane(departmentsTable);
                    scrollPane.setPreferredSize(new Dimension(500, 300));
                    
                    // Show in dialog
                    JOptionPane.showMessageDialog(
                        AddEmployee.this,
                        scrollPane,
                        "Available Departments",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                        AddEmployee.this,
                        "Error retrieving departments: " + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Add employee to database
     */
    private void addEmployeeToDatabase() {
        
        String fname = txtFname.getText().trim();
        String lname = txtLname.getText().trim();
        String ssn = txtSsn.getText().trim();
        
        int dno;
        try {
            dno = Integer.parseInt(txtDno.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Department Number must be a valid number.", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validate input
        if (fname.isEmpty() || lname.isEmpty() || ssn.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please fill in all required fields.", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Update status
        txtStatus.setText("Adding employee...");
          
        Connection conn = null;
        PreparedStatement pstmt = null;
                
        try {
                    
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            
            // Disable auto-commit for transaction
            conn.setAutoCommit(false);
            
            // Add employee
            pstmt = conn.prepareStatement(
                "INSERT INTO employee (Fname, Minit, Lname, Ssn, Bdate, Address, Sex, Salary, Super_ssn, Dno) " +
                "VALUES (?, NULL, ?, ?, NULL, NULL, NULL, NULL, NULL, ?)"
            );
            pstmt.setString(1, fname);
            pstmt.setString(2, lname);
            pstmt.setString(3, ssn);
            pstmt.setInt(4, dno);
            
            pstmt.executeUpdate();
            
            // Commit transaction
            conn.commit();
            
            JOptionPane.showMessageDialog(this, 
                    "Employee added successfully", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            
            // Clear form
            clearForm();
            
            // Refresh table to show new employee
            loadEmployeesData();
        
        } catch (SQLException e) {
            try {
                // Rollback the transaction
                if (conn != null) {
                    conn.rollback();
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Rollback has been done: " +
                    "It is likely that the Ssn you have entered is already in the database or that the department you have entered is not on the list of departments ", 
                    "Database Error", 
                    JOptionPane.ERROR_MESSAGE);
                      
                txtStatus.setText("Error - Transaction rolled back");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error during rollback: " + ex.getMessage(), 
                    "Database Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
        	JOptionPane.showMessageDialog(this, 
                    "There has been an error: " + e.getMessage(), 
                    "Database Error", 
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            // Close resources
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}