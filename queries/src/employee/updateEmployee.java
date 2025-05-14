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
        setBounds(100, 100, 600, 380);
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
        headerPanel.setPreferredSize(new Dimension(600, 80));
        contentPane.add(headerPanel, BorderLayout.NORTH);
        headerPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Update Project Department");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setBorder(BorderFactory.createLineBorder(DARK_GREEN, 1));
        formPanel.setBackground(VERY_LIGHT_GREEN);
        contentPane.add(formPanel, BorderLayout.CENTER);
        formPanel.setLayout(null);
        
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
        
        // Note/instructions
        JPanel instructionPanel = new JPanel();
        instructionPanel.setBackground(new Color(240, 255, 240)); // Very light green for instruction panel
        instructionPanel.setBorder(BorderFactory.createLineBorder(MEDIUM_GREEN, 1));
        instructionPanel.setBounds(50, 150, 470, 60);
        formPanel.add(instructionPanel);
        instructionPanel.setLayout(new BorderLayout());
        
        JLabel lblInstruction = new JLabel("<html>This form will update the department number of the project with the given name. Ensure both fields are filled correctly.</html>");
        lblInstruction.setFont(new Font("Arial", Font.ITALIC, 13));
        lblInstruction.setForeground(DARK_GREEN);
        lblInstruction.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        instructionPanel.add(lblInstruction, BorderLayout.CENTER);
        
        // Control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setBorder(BorderFactory.createLineBorder(DARK_GREEN, 1));
        controlPanel.setBackground(LIGHT_GREEN);
        controlPanel.setPreferredSize(new Dimension(600, 80));
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
        
        btnUpdate = createStyledButton("Update Project", DARK_GREEN, Color.WHITE);
        btnUpdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateProjectDepartment();
            }
        });
        controlPanel.add(btnUpdate);
        
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
                    
                    pstmt.executeUpdate();
                    
                    // Commit transaction
                    conn.commit();
                    
                    JOptionPane.showMessageDialog(this, 
                            "Record added successfully", 
                            "Success", 
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException | ClassNotFoundException e) {
                    try {
                        // Rollback the transaction
                        conn.rollback();
                        
                        JOptionPane.showMessageDialog(this, 
                            "Rollback has been done. " + e.getMessage(), 
                            "Database Error", 
                            JOptionPane.ERROR_MESSAGE);
                      
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