package conexiones;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.geom.RoundRectangle2D;

import employee.*;

/**
 * EmployeeMenu - Navigation interface for the Employee database queries
 */
public class EmployeeMenu extends JFrame {
    private JPanel contentPane;
    
    // Color theme - blues used in employee package
    private static final Color DARK_BLUE = new Color(25, 50, 93);
    private static final Color MEDIUM_BLUE = new Color(70, 130, 180);
    private static final Color LIGHT_BLUE = new Color(173, 216, 230);
    private static final Color VERY_LIGHT_BLUE = new Color(240, 248, 255);
    
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
                    EmployeeMenu frame = new EmployeeMenu();
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
    public EmployeeMenu () {
        setTitle("Employee Database Queries");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Main panel with gradient background
        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, DARK_BLUE,
                                                  getWidth(), getHeight(), new Color(40, 75, 115));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        
        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setBounds(0, 0, 884, 100);
        contentPane.add(headerPanel);
        headerPanel.setLayout(null);
        
        // Application title
        JLabel lblTitle = new JLabel("EMPLOYEE DATABASE QUERIES");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblTitle.setBounds(10, 11, 864, 50);
        headerPanel.add(lblTitle);
        
        JLabel lblSubtitle = new JLabel("Select a query to view or modify employee data");
        lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblSubtitle.setForeground(LIGHT_BLUE);
        lblSubtitle.setFont(new Font("Segoe UI", Font.ITALIC, 18));
        lblSubtitle.setBounds(10, 60, 864, 30);
        headerPanel.add(lblSubtitle);
        
        // Main panel for buttons
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setOpaque(false);
        buttonsPanel.setBounds(50, 110, 784, 400);
        contentPane.add(buttonsPanel);
        buttonsPanel.setLayout(null);
        
        // Button 1: Employee Project Analyzer
        JButton btnEmployeeProjectAnalyzer = createStyledButton("Employee Project Analyzer", 
            "View employees with projects in their department who have dependents", 
            50, 20, 330, 110);
        btnEmployeeProjectAnalyzer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openEmployeeProjectAnalyzer();
            }
        });
        buttonsPanel.add(btnEmployeeProjectAnalyzer);
        
        // Button 2: Employee Pairs
        JButton btnEmployeePairs = createStyledButton("Department Comparison", 
            "Analyze department pairs with male/female employees working on the same projects", 
            50, 150, 330, 110);
        btnEmployeePairs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openEmployeePairs();
            }
        });
        buttonsPanel.add(btnEmployeePairs);
        
        // Button 3: Project Employee Hours
        JButton btnProjectEmployeeHours = createStyledButton("Project Hours Analysis", 
            "Find employees with highest/lowest hours worked on projects", 
            50, 280, 330, 110);
        btnProjectEmployeeHours.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openProjectEmployeeHours();
            }
        });
        buttonsPanel.add(btnProjectEmployeeHours);
        
        // Button 4: Add Employee
        JButton btnAddEmployee = createStyledButton("Add New Employee", 
            "Add a new employee to the database", 
            420, 20, 330, 110);
        btnAddEmployee.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openAddEmployee();
            }
        });
        buttonsPanel.add(btnAddEmployee);
        
        // Button 5: Update Employee
        JButton btnUpdateEmployee = createStyledButton("Update Project Department", 
            "Update a project's department number", 
            420, 150, 330, 110);
        btnUpdateEmployee.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openUpdateEmployee();
            }
        });
        buttonsPanel.add(btnUpdateEmployee);
        
        // Return to Main Menu button
        JButton btnMainMenu = createStyledButton("Return to Main Menu", 
            "Go back to the main navigation menu", 
            420, 280, 330, 110);
        btnMainMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                returnToMainMenu();
            }
        });
        buttonsPanel.add(btnMainMenu);
        
        // Footer panel
        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        footerPanel.setBounds(0, 520, 884, 40);
        contentPane.add(footerPanel);
        footerPanel.setLayout(new BorderLayout(0, 0));
        
        JLabel lblStatus = new JLabel(" Employee Database Module");
        lblStatus.setForeground(Color.WHITE);
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        footerPanel.add(lblStatus, BorderLayout.WEST);
        
        JButton btnExit = new JButton("Exit");
        btnExit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnExit.setForeground(Color.WHITE);
        btnExit.setBackground(DARK_BLUE);
        btnExit.setBorderPainted(false);
        btnExit.setFocusPainted(false);
        btnExit.addActionListener(e -> System.exit(0));
        footerPanel.add(btnExit, BorderLayout.EAST);
    }
    
    /**
     * Creates a styled button with title and description
     */
    private JButton createStyledButton(String title, String description, int x, int y, int width, int height) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(DARK_BLUE);
                } else if (getModel().isRollover()) {
                    g2.setColor(MEDIUM_BLUE);
                } else {
                    g2.setColor(new Color(40, 80, 120, 220));
                }
                
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                g2.setColor(new Color(255, 255, 255, 50));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                
                // Draw title
                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                g2.setColor(Color.WHITE);
                
                FontMetrics fm = g2.getFontMetrics();
                int titleWidth = fm.stringWidth(title);
                int titleX = (getWidth() - titleWidth) / 2;
                g2.drawString(title, titleX, 35);
                
                // Draw description
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                g2.setColor(VERY_LIGHT_BLUE);
                
                drawWrappedText(g2, description, 15, 55, getWidth() - 30);
                
                g2.dispose();
            }
            
            private void drawWrappedText(Graphics2D g2, String text, int x, int y, int width) {
                FontMetrics fm = g2.getFontMetrics();
                String[] words = text.split(" ");
                String line = "";
                int lineY = y;
                
                for (String word : words) {
                    String testLine = line.isEmpty() ? word : line + " " + word;
                    int testWidth = fm.stringWidth(testLine);
                    
                    if (testWidth > width) {
                        g2.drawString(line, x, lineY);
                        line = word;
                        lineY += fm.getHeight();
                    } else {
                        line = testLine;
                    }
                }
                
                if (!line.isEmpty()) {
                    g2.drawString(line, x, lineY);
                }
            }
        };
        
        button.setBounds(x, y, width, height);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    /**
     * Open the EmployeeProjectAnalyzer
     */
    private void openEmployeeProjectAnalyzer() {
        EmployeeProjectAnalyzer frame = new EmployeeProjectAnalyzer();
        frame.setVisible(true);
    }
    
    /**
     * Open the EmployeePairs (Department Comparison)
     */
    private void openEmployeePairs() {
        EmployyePairs frame = new EmployyePairs();
        frame.setVisible(true);
    }
    
    /**
     * Open the ProjectEmployeeHours
     */
    private void openProjectEmployeeHours() {
        ProjectEmployeeHours frame = new ProjectEmployeeHours();
        frame.setVisible(true);
    }
    
    /**
     * Open the addEmployee
     */
    private void openAddEmployee() {
        addEmployee frame = new addEmployee();
        frame.setVisible(true);
    }
    
    /**
     * Open the updateEmployee
     */
    private void openUpdateEmployee() {
        updateEmployee frame = new updateEmployee();
        frame.setVisible(true);
    }
    
    /**
     * Return to the main menu
     */
    private void returnToMainMenu() {
        MainMenuConector mainMenu = new MainMenuConector();
        mainMenu.setVisible(true);
        this.dispose();
    }
}