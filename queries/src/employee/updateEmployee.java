package employee;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.event.*;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class updateEmployee extends JFrame {
    private JPanel contentPane;
    private JTextField txtProjectName;
    private JTextField txtNewDno;
    private JTextField txtStatus;
    private JButton btnUpdate;
    private JButton btnClear;
    private JButton btnExit;
    private JButton btnShowProjects;
    private JTable projectsTable;
    private DefaultTableModel projectsTableModel;
    
    private static final String DB_URL = "jdbc:mysql://dif-mysql.ehu.es:23306/DBI08";
    private static final String USER = "DBI08";
    private static final String PASS = "DBI08";
    
    private static final Color DARK_GREEN = new Color(25, 93, 50);
    private static final Color MEDIUM_GREEN = new Color(70, 180, 100);
    private static final Color LIGHT_GREEN = new Color(173, 230, 188);
    private static final Color VERY_LIGHT_GREEN = new Color(240, 255, 245);
    
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            // Set look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    updateEmployee frame = new updateEmployee();
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
    public updateEmployee() {
        setTitle("Update Project Department");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 600);
        setLocationRelativeTo(null);
        
        // Simple panel with border
        contentPane = new JPanel();
        contentPane.setBorder(BorderFactory.createLineBorder(DARK_GREEN, 2));
        contentPane.setBackground(VERY_LIGHT_GREEN);
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));
        
        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBorder(BorderFactory.createLineBorder(DARK_GREEN, 1));
        headerPanel.setBackground(MEDIUM_GREEN);
        headerPanel.setPreferredSize(new Dimension(800, 80));
        contentPane.add(headerPanel, BorderLayout.NORTH);
        headerPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Update Project Department");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Main content panel with split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(180);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);
        contentPane.add(splitPane, BorderLayout.CENTER);
        
        // Form panel (top of split pane)
        JPanel formPanel = new JPanel();
        formPanel.setBorder(BorderFactory.createLineBorder(DARK_GREEN, 1));
        formPanel.setBackground(VERY_LIGHT_GREEN);
        formPanel.setLayout(null);
        splitPane.setTopComponent(formPanel);
        
        // Project Name
        JLabel lblProjectName = new JLabel("Project Name:");
        lblProjectName.setFont(new Font("Arial", Font.BOLD, 14));
        lblProjectName.setForeground(DARK_GREEN);
        lblProjectName.setBounds(50, 40, 150, 25);
        formPanel.add(lblProjectName);
        
        txtProjectName = new JTextField();
        txtProjectName.setFont(new Font("Arial", Font.PLAIN, 14));
        txtProjectName.setBounds(200, 40, 320, 30);
        txtProjectName.setBorder(BorderFactory.createLineBorder(MEDIUM_GREEN, 1));
        formPanel.add(txtProjectName);
        
        // Department Number
        JLabel lblNewDno = new JLabel("New Department No:");
        lblNewDno.setFont(new Font("Arial", Font.BOLD, 14));
        lblNewDno.setForeground(DARK_GREEN);
        lblNewDno.setBounds(50, 90, 150, 25);
        formPanel.add(lblNewDno);
        
        txtNewDno = new JTextField();
        txtNewDno.setFont(new Font("Arial", Font.PLAIN, 14));
        txtNewDno.setBounds(200, 90, 320, 30);
        txtNewDno.setBorder(BorderFactory.createLineBorder(MEDIUM_GREEN, 1));
        formPanel.add(txtNewDno);
        
        // Button panel within form panel
        JPanel formButtonPanel = new JPanel();
        formButtonPanel.setOpaque(false);
        formButtonPanel.setBounds(200, 130, 320, 40);
        formButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        formPanel.add(formButtonPanel);
        
        btnUpdate = createStyledButton("Update Project", DARK_GREEN, Color.WHITE);
        btnUpdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateProjectDepartment();
            }
        });
        formButtonPanel.add(btnUpdate);
        
        btnShowProjects = createStyledButton("Show Projects", DARK_GREEN, Color.WHITE);
        btnShowProjects.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadProjectsData();
            }
        });
        formButtonPanel.add(btnShowProjects);
        
        // Projects table panel (bottom of split pane)
        JPanel projectsPanel = new JPanel();
        projectsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(DARK_GREEN, 1),
            "Projects Database",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            DARK_GREEN
        ));
        projectsPanel.setBackground(VERY_LIGHT_GREEN);
        projectsPanel.setLayout(new BorderLayout());
        splitPane.setBottomComponent(projectsPanel);
        
        // Create table model for projects
        String[] columnNames = {"Project Number", "Project Name", "Department Number", "Department Location", "Controlling Department"};
        projectsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        projectsTable = new JTable(projectsTableModel);
        projectsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        projectsTable.setRowHeight(25);
        projectsTable.setGridColor(MEDIUM_GREEN);
        projectsTable.getTableHeader().setBackground(MEDIUM_GREEN);
        projectsTable.getTableHeader().setForeground(Color.green.darker());
        projectsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
        // Set alternating row colors
        projectsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (isSelected) {
                    c.setBackground(LIGHT_GREEN);
                    c.setForeground(DARK_GREEN);
                } else {
                    c.setBackground(row % 2 == 0 ? VERY_LIGHT_GREEN : Color.WHITE);
                    c.setForeground(DARK_GREEN);
                }
                
                return c;
            }
        });
        
        // Add selection listener to populate form fields
        projectsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && projectsTable.getSelectedRow() >= 0) {
                String projectName = (String) projectsTable.getValueAt(projectsTable.getSelectedRow(), 1);
                txtProjectName.setText(projectName);
            }
        });
        
        // Create scroll pane for table
        JScrollPane scrollPane = new JScrollPane(projectsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Style the scrollbars
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = MEDIUM_GREEN;
                this.trackColor = VERY_LIGHT_GREEN;
            }
        });
        
        projectsPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setBorder(BorderFactory.createLineBorder(DARK_GREEN, 1));
        controlPanel.setBackground(LIGHT_GREEN);
        controlPanel.setPreferredSize(new Dimension(800, 60));
        contentPane.add(controlPanel, BorderLayout.SOUTH);
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 15));
        
        txtStatus = new JTextField("Ready to update project");
        txtStatus.setEditable(false);
        txtStatus.setFont(new Font("Arial", Font.BOLD, 12));
        txtStatus.setForeground(DARK_GREEN);
        txtStatus.setBackground(VERY_LIGHT_GREEN);
        txtStatus.setPreferredSize(new Dimension(200, 25));
        txtStatus.setBorder(BorderFactory.createLineBorder(MEDIUM_GREEN));
        controlPanel.add(txtStatus);
        
        btnClear = createStyledButton("Clear", DARK_GREEN, Color.WHITE);
        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
        controlPanel.add(btnClear);
        
        btnExit = createStyledButton("Exit", DARK_GREEN, Color.WHITE);
        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        controlPanel.add(btnExit);
        
        // Load projects data initially
        loadProjectsData();
    }
    
    /**
     * Load projects data from database
     */
    private void loadProjectsData() {
        txtStatus.setText("Loading projects data...");
        
        // Clear existing data
        projectsTableModel.setRowCount(0);
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
            
            // Comprehensive query to get project details with department info
            String sql = "SELECT p.Pnumber, p.Pname, p.Dnum, d.Dlocation, d.Dname " +
                         "FROM project p " +
                         "JOIN department d ON p.Dnum = d.Dnumber " +
                         "ORDER BY p.Pnumber";
            
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                int projectNumber = rs.getInt("Pnumber");
                String projectName = rs.getString("Pname");
                int deptNumber = rs.getInt("Dnum");
                String deptLocation = rs.getString("Dlocation");
                String deptName = rs.getString("Dname");
                
                projectsTableModel.addRow(new Object[] {
                    projectNumber, projectName, deptNumber, deptLocation, deptName
                });
            }
            
            txtStatus.setText("Projects data loaded successfully");
            
        } catch (Exception e) {
            txtStatus.setText("Error loading projects data");
            JOptionPane.showMessageDialog(this, 
                "Error loading projects data: " + e.getMessage(), 
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
     * Method to create buttons with consistent style
     */
    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(120, 30));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        return button;
    }
    
    /**
     * Clear all form fields
     */
    private void clearForm() {
        txtProjectName.setText("");
        txtNewDno.setText("");
        txtStatus.setText("Form cleared");
    }
    
    /**
     * Update project department in database
     */
    private void updateProjectDepartment() {
        String projectName = txtProjectName.getText().trim();
        String dnoStr = txtNewDno.getText().trim();
        
        // Validate input
        if (projectName.isEmpty() || dnoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please fill in all fields", 
                "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int newDno;
        try {
            newDno = Integer.parseInt(dnoStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Department number must be a valid integer", 
                "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Update status
        txtStatus.setText("Updating project...");
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            
            // Disable auto-commit
            conn.setAutoCommit(false);
            
            // Update project department
            String sql = "UPDATE project SET Dnum = ? WHERE Pname = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, newDno);
            pstmt.setString(2, projectName);
            
            int rowsUpdated = pstmt.executeUpdate();
            
            // Commit transaction
            conn.commit();
            
            if (rowsUpdated > 0) {
                success = true;
                txtStatus.setText("Project updated successfully");
                
                JOptionPane.showMessageDialog(this, 
                        "Project '" + projectName + "' updated successfully", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh the projects table to show the updated data
                loadProjectsData();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Project '" + projectName + "' not found", 
                    "Update Failed", 
                    JOptionPane.WARNING_MESSAGE);
                    
                txtStatus.setText("No project was updated");
            }
        } catch (SQLException | ClassNotFoundException e) {
            try {
                // Rollback the transaction
                if (conn != null) {
                    conn.rollback();
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Rollback has been done. " + e.getMessage(), 
                    "Database Error", 
                    JOptionPane.ERROR_MESSAGE);
                      
                txtStatus.setText("Transaction rolled back");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error during rollback: " + ex.getMessage(), 
                    "Database Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
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