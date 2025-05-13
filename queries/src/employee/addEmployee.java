package employee;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.util.Scanner;

/**
 * Modernized graphical interface for adding a new employee
 * Replaces the console-based implementation
 */
public class addEmployee extends JFrame {
    private JPanel contentPane;
    private JTextField txtFname;
    private JTextField txtLname;
    private JTextField txtSsn;
    private JTextField txtDno;
    private JTextField txtStatus;
    private JButton btnAdd;
    private JButton btnClear;
    private JButton btnShowDepartments;
    private JButton btnExit;
    
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
                    addEmployee frame = new addEmployee();
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
    public addEmployee() {
        setTitle("Add New Employee");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 700, 500);
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
        headerPanel.setPreferredSize(new Dimension(700, 80));
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
        
        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        contentPane.add(formPanel, BorderLayout.CENTER);
        formPanel.setLayout(null);
        
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
        
        // Information panel
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(46, 125, 50, 120));
        infoPanel.setBounds(150, 200, 350, 100);
        infoPanel.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 100)));
        formPanel.add(infoPanel);
        infoPanel.setLayout(new BorderLayout());
        
        JLabel lblInfo = new JLabel("<html><div style='text-align: center; margin: 10px;'>" + 
                "<b>Instructions:</b><br/>" +
                "Add a new employee to the database. All fields are required.<br/><br/>" +
                "Press 'Show Departments' to see a list of available departments." +
                "</div></html>");
        lblInfo.setForeground(Color.WHITE);
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoPanel.add(lblInfo, BorderLayout.CENTER);
        
        // Footer panel
        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        footerPanel.setPreferredSize(new Dimension(700, 100));
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
        txtStatus.setPreferredSize(new Dimension(200, 35));
        footerPanel.add(txtStatus);
        
        // Add button
        btnAdd = createStyledButton("Add Employee");
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addEmployeeToDatabase();
            }
        });
        footerPanel.add(btnAdd);
        
        // Show Departments button
        btnShowDepartments = createStyledButton("Show Departments");
        btnShowDepartments.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showDepartments();
            }
        });
        footerPanel.add(btnShowDepartments);
        
        // Clear button
        btnClear = createStyledButton("Clear Form");
        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
        footerPanel.add(btnClear);
        
        // Exit button
        btnExit = createStyledButton("Exit");
        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        footerPanel.add(btnExit);
    }
    
    /**
     * Create a styled button
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
                    header.setForeground(Color.WHITE);
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
                        addEmployee.this,
                        scrollPane,
                        "Available Departments",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                        addEmployee.this,
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
        // Validate input
        if (txtFname.getText().trim().isEmpty() || 
            txtLname.getText().trim().isEmpty() || 
            txtSsn.getText().trim().isEmpty() || 
            txtDno.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, 
                "All fields are required.", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
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
        
        // Update status
        txtStatus.setText("Adding employee...");
        
        // Use SwingWorker to perform database operations in background
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private boolean success = false;
            private String errorMessage = "";
            
            @Override
            protected Void doInBackground() throws Exception {
                Connection conn = null;
                PreparedStatement pstmt = null;
                
                try {
                    // Check if department exists
                    conn = DriverManager.getConnection(DB_URL, USER, PASS);
                    pstmt = conn.prepareStatement("SELECT COUNT(*) FROM department WHERE Dnumber = ?");
                    pstmt.setInt(1, dno);
                    ResultSet rs = pstmt.executeQuery();
                    rs.next();
                    int count = rs.getInt(1);
                    
                    if (count == 0) {
                        errorMessage = "Department " + dno + " does not exist.";
                        success = false;
                        return null;
                    }
                    
                    // Check if SSN already exists
                    pstmt = conn.prepareStatement("SELECT COUNT(*) FROM employee WHERE Ssn = ?");
                    pstmt.setString(1, ssn);
                    rs = pstmt.executeQuery();
                    rs.next();
                    count = rs.getInt(1);
                    
                    if (count > 0) {
                        errorMessage = "Employee with SSN " + ssn + " already exists.";
                        success = false;
                        return null;
                    }
                    
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
                    
                    int rowsInserted = pstmt.executeUpdate();
                    
                    // Commit transaction
                    conn.commit();
                    
                    success = (rowsInserted > 0);
                } catch (SQLException e) {
                    // Rollback transaction on error
                    if (conn != null) {
                        try {
                            conn.rollback();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                    e.printStackTrace();
                    errorMessage = e.getMessage();
                    success = false;
                } finally {
                    // Close resources
                    try {
                        if (pstmt != null) pstmt.close();
                        if (conn != null) conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                
                return null;
            }
            
            @Override
            protected void done() {
                if (success) {
                    txtStatus.setText("Employee added successfully");
                    txtStatus.setForeground(new Color(220, 255, 220));
                    JOptionPane.showMessageDialog(addEmployee.this,
                        "The employee has been successfully added to the database.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                } else {
                    txtStatus.setText("Error adding employee");
                    txtStatus.setForeground(new Color(255, 150, 150));
                    JOptionPane.showMessageDialog(addEmployee.this,
                        "Error adding employee: " + errorMessage,
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
}