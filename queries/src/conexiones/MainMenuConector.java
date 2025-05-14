package conexiones;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.geom.RoundRectangle2D;

import employee.*;
import travel.*;
import MondialDB.*;
import Restaurant.*;


public class MainMenuConector extends JFrame {
    private JPanel contentPane;
    
    // Color theme based on QueryMenu
    private static final Color BG_START_COLOR = new Color(40, 60, 134);
    private static final Color BG_END_COLOR = new Color(69, 162, 158);
    private static final Color BUTTON_BG = new Color(60, 100, 160, 200);
    private static final Color BUTTON_HOVER_BG = new Color(100, 180, 255, 200);
    private static final Color BUTTON_PRESSED_BG = new Color(70, 130, 180, 200);
    private static final Color TITLE_COLOR = Color.WHITE;
    private static final Color SUBTITLE_COLOR = new Color(220, 220, 255);
    
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
                    MainMenuConector frame = new MainMenuConector();
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
    public MainMenuConector() {
        setTitle("Database Query Navigation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Custom panel with gradient background
        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, BG_START_COLOR,
                                                  getWidth(), getHeight(), BG_END_COLOR);
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
        JLabel lblTitle = new JLabel("DATABASE QUERY SYSTEM");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setForeground(TITLE_COLOR);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitle.setBounds(10, 11, 864, 50);
        headerPanel.add(lblTitle);
        
        JLabel lblSubtitle = new JLabel("Navigation Hub - Select a Database Category");
        lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblSubtitle.setForeground(SUBTITLE_COLOR);
        lblSubtitle.setFont(new Font("Segoe UI", Font.ITALIC, 18));
        lblSubtitle.setBounds(10, 60, 864, 30);
        headerPanel.add(lblSubtitle);
        
        // Main navigation buttons panel
        JPanel navPanel = new JPanel();
        navPanel.setOpaque(false);
        navPanel.setBounds(50, 120, 784, 380);
        contentPane.add(navPanel);
        navPanel.setLayout(null);
        
        // Employee Database button
        JButton btnEmployee = createStyledButton("Employee Database", "Manage employee projects, relationships, and department data", 50, 30, 330, 140);
        btnEmployee.setIcon(createIcon("employee"));
        btnEmployee.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openEmployeeMenu();
            }
        });
        navPanel.add(btnEmployee);
        
        // Mondial Database button
        JButton btnMondial = createStyledButton("Mondial Database", "Explore geographical, political, and economic data worldwide", 50, 210, 330, 140);
        btnMondial.setIcon(createIcon("mondial"));
        btnMondial.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openMondialMenu();
            }
        });
        navPanel.add(btnMondial);
        
        // Travel Database button
        JButton btnTravel = createStyledButton("Travel Database", "Analyze hotel occupancy, tour guides, and excursion data", 420, 30, 330, 140);
        btnTravel.setIcon(createIcon("travel"));
        btnTravel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openTravelMenu();
            }
        });
        navPanel.add(btnTravel);
        
        // Restaurant Database button
        JButton btnRestaurant = createStyledButton("Restaurant Database", "Manage restaurant orders, menus, and customer preferences", 420, 210, 330, 140);
        btnRestaurant.setIcon(createIcon("restaurant"));
        btnRestaurant.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openRestaurantMenu();
            }
        });
        navPanel.add(btnRestaurant);
        
        // Footer panel
        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        footerPanel.setBounds(0, 520, 884, 40);
        contentPane.add(footerPanel);
        footerPanel.setLayout(new BorderLayout(0, 0));
        
        JLabel lblStatus = new JLabel(" Database Navigation System v1.0");
        lblStatus.setForeground(Color.WHITE);
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        footerPanel.add(lblStatus, BorderLayout.WEST);
        
        JButton btnExit = new JButton("Exit");
        btnExit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnExit.setForeground(Color.WHITE);
        btnExit.setBackground(new Color(60, 60, 100, 200));
        btnExit.setBorderPainted(false);
        btnExit.setFocusPainted(false);
        btnExit.addActionListener(e -> System.exit(0));
        footerPanel.add(btnExit, BorderLayout.EAST);
    }
    
    /**
     * Creates a styled navigation button with title and description
     */
    private JButton createStyledButton(String title, String description, int x, int y, int width, int height) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(BUTTON_PRESSED_BG);
                } else if (getModel().isRollover()) {
                    g2.setColor(BUTTON_HOVER_BG);
                } else {
                    g2.setColor(BUTTON_BG);
                }
                
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                g2.setColor(new Color(255, 255, 255, 50));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                
                // Draw title and description
                g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
                g2.setColor(Color.WHITE);
                
                FontMetrics fm = g2.getFontMetrics();
                int titleWidth = fm.stringWidth(title);
                int titleX = (getWidth() - titleWidth) / 2;
                g2.drawString(title, titleX, 50);
                
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                g2.setColor(new Color(220, 220, 255));
                
                // Draw description as wrapped text
                drawWrappedText(g2, description, 10, 80, getWidth() - 20);
                
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
    
   
    private ImageIcon createIcon(String type) {
        // This method is kept as a placeholder for potential future icon implementation
        return null;
    }
    
    /**
     * Opens the Employee menu
     */
    private void openEmployeeMenu() {
        EmployeeMenu menu = new EmployeeMenu();
        menu.setVisible(true);
        this.setVisible(false);
    }
    
    /**
     * Opens the Mondial menu
     */
    private void openMondialMenu() {
        MondialMenu menu = new MondialMenu();
        menu.setVisible(true);
        this.setVisible(false);
    }
    
    /**
     * Opens the Travel menu
     */
    private void openTravelMenu() {
        TravelMenu menu = new TravelMenu();
        menu.setVisible(true);
        this.setVisible(false);
    }
    
    /**
     * Opens the Restaurant menu
     */
    private void openRestaurantMenu() {
        RestaurantMenu menu = new RestaurantMenu();
        menu.setVisible(true);
        this.setVisible(false);
    }
}