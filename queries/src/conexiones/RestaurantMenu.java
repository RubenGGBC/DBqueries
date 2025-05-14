package conexiones;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import Restaurant.UpdateMenuOrder;
import Restaurant.addFrequents;

import java.awt.geom.RoundRectangle2D;

import conexiones.MainMenuConector;

/**
 * RestaurantMenu - Navigation interface for the Restaurant database queries
 * Standardized with pastel color theme similar to MondialDb
 */
public class RestaurantMenu extends JFrame {
    private JPanel contentPane;
    
    // Color theme - pastel colors like in the Mondial package
    private static final Color PASTEL_BACKGROUND = new Color(253, 245, 230); // Soft peach
    private static final Color PASTEL_HEADER = new Color(255, 228, 196); // Bisque
    private static final Color PASTEL_TEXT = new Color(119, 136, 153); // Slate gray
    private static final Color PASTEL_BUTTON = new Color(221, 160, 221); // Plum
    private static final Color PASTEL_BUTTON_TEXT = new Color(75, 0, 130); // Indigo
    
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
                    RestaurantMenu frame = new RestaurantMenu();
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
    public RestaurantMenu() {
        setTitle("Restaurant Database Queries");
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
                GradientPaint gp = new GradientPaint(0, 0, new Color(253, 245, 230),
                                                  getWidth(), getHeight(), new Color(255, 222, 173));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        
        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PASTEL_HEADER);
        headerPanel.setBounds(0, 0, 900, 100);
        contentPane.add(headerPanel);
        headerPanel.setLayout(null);
        
        // Application title
        JLabel lblTitle = new JLabel("RESTAURANT DATABASE QUERIES");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setForeground(PASTEL_TEXT);
        lblTitle.setFont(new Font("Serif", Font.BOLD, 32));
        lblTitle.setBounds(10, 11, 864, 50);
        headerPanel.add(lblTitle);
        
        JLabel lblSubtitle = new JLabel("Manage restaurant orders, menu items and customer preferences");
        lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblSubtitle.setForeground(PASTEL_TEXT);
        lblSubtitle.setFont(new Font("Serif", Font.ITALIC, 18));
        lblSubtitle.setBounds(10, 60, 864, 30);
        headerPanel.add(lblSubtitle);
        
        // Main panel for buttons
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setOpaque(false);
        buttonsPanel.setBounds(50, 120, 784, 380);
        contentPane.add(buttonsPanel);
        buttonsPanel.setLayout(null);
        
        // Button 1: Update Menu Order
        JButton btnUpdateMenuOrder = createMenuButton("Update Menu Order", 
            "Update existing menu orders with new menu types and IDs", 
            50, 30, 330, 140);
        btnUpdateMenuOrder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openUpdateMenuOrder();
            }
        });
        buttonsPanel.add(btnUpdateMenuOrder);
        
        // Button 2: Add Frequents
        JButton btnAddFrequents = createMenuButton("Customer Frequents Manager", 
            "Manage restaurants frequented by customers", 
            50, 210, 330, 140);
        btnAddFrequents.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openAddFrequents();
            }
        });
        buttonsPanel.add(btnAddFrequents);
        
        // Button 3: Return to Main Menu
        JButton btnMainMenu = createMenuButton("Return to Main Menu", 
            "Go back to the main navigation menu", 
            420, 210, 330, 140);
        btnMainMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                returnToMainMenu();
            }
        });
        buttonsPanel.add(btnMainMenu);
        
        // Footer panel
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(PASTEL_HEADER);
        footerPanel.setBounds(0, 520, 900, 40);
        contentPane.add(footerPanel);
        footerPanel.setLayout(new BorderLayout(0, 0));
        
        JLabel lblStatus = new JLabel(" Restaurant Database Module");
        lblStatus.setForeground(PASTEL_TEXT);
        lblStatus.setFont(new Font("Serif", Font.BOLD, 14));
        footerPanel.add(lblStatus, BorderLayout.WEST);
        
        JButton btnExit = new JButton("Exit");
        btnExit.setFont(new Font("Serif", Font.BOLD, 14));
        btnExit.setForeground(PASTEL_BUTTON_TEXT);
        btnExit.setBackground(PASTEL_BUTTON);
        btnExit.setBorderPainted(false);
        btnExit.setFocusPainted(false);
        btnExit.addActionListener(e -> System.exit(0));
        footerPanel.add(btnExit, BorderLayout.EAST);
    }
    
    /**
     * Creates a styled button with title and description
     */
    private JButton createMenuButton(String title, String description, int x, int y, int width, int height) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(new Color(216, 191, 216)); // Pressed color
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(238, 130, 238)); // Hover color
                } else {
                    g2.setColor(PASTEL_BUTTON); // Default color
                }
                
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                g2.setColor(new Color(139, 0, 139, 50)); // Border
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                
                // Draw title
                g2.setFont(new Font("Serif", Font.BOLD, 20));
                g2.setColor(PASTEL_BUTTON_TEXT);
                
                FontMetrics fm = g2.getFontMetrics();
                int titleWidth = fm.stringWidth(title);
                int titleX = (getWidth() - titleWidth) / 2;
                g2.drawString(title, titleX, 40);
                
                // Draw description
                g2.setFont(new Font("Serif", Font.PLAIN, 14));
                g2.setColor(new Color(85, 26, 139)); // Dark purple
                
                drawWrappedText(g2, description, 20, 70, getWidth() - 40);
                
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
     * Creates a smaller styled button for controls
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Serif", Font.BOLD, 14));
        button.setForeground(PASTEL_BUTTON_TEXT);
        button.setBackground(PASTEL_BUTTON);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        return button;
    }
    
    /**
     * Open UpdateMenuOrder
     */
    private void openUpdateMenuOrder() {
        UpdateMenuOrder frame = new UpdateMenuOrder();
        frame.setVisible(true);
    }
    
    /**
     * Open AddFrequents
     */
    private void openAddFrequents() {
        addFrequents frame = new addFrequents();
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