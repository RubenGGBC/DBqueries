package conexions;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.geom.RoundRectangle2D;

import travel.*;

/**
 * TravelMenu - Navigation interface for the Travel database queries
 * Standardized with blue/black theme
 */
public class TravelMenu extends JFrame {
    private JPanel contentPane;
    
    // Color theme for Travel package
    private static final Color DARK_BLUE = new Color(15, 35, 60);
    private static final Color MEDIUM_BLUE = new Color(25, 84, 123);
    private static final Color LIGHT_BLUE = new Color(70, 130, 180);
    private static final Color VERY_LIGHT_BLUE = new Color(240, 248, 255);
    private static final Color ACCENT_BLUE = new Color(30, 144, 255);
    
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
                    TravelMenu frame = new TravelMenu();
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
    public TravelMenu() {
        setTitle("Travel Database Queries");
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
                GradientPaint gp = new GradientPaint(0, 0, MEDIUM_BLUE,
                                                  getWidth(), getHeight(), DARK_BLUE);
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
        JLabel lblTitle = new JLabel("TRAVEL DATABASE QUERIES");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitle.setBounds(10, 11, 864, 50);
        headerPanel.add(lblTitle);
        
        JLabel lblSubtitle = new JLabel("Analyze hotel occupancy, excursions, and tour guide data");
        lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblSubtitle.setForeground(new Color(200, 230, 255));
        lblSubtitle.setFont(new Font("Segoe UI", Font.ITALIC, 18));
        lblSubtitle.setBounds(10, 60, 864, 30);
        headerPanel.add(lblSubtitle);
        
        // Main panel for buttons
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setOpaque(false);
        buttonsPanel.setBounds(50, 120, 784, 380);
        contentPane.add(buttonsPanel);
        buttonsPanel.setLayout(null);
        
        // Button 1: Hotel Occupancy Analysis
        JButton btnHotelOccupancy = createMenuButton("Hotel Occupancy Analysis", 
            "View detailed statistics on hotel utilization rates and capacity metrics", 
            50, 30, 330, 100);
        btnHotelOccupancy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openHotelOccupancy();
            }
        });
        buttonsPanel.add(btnHotelOccupancy);
        
        // Button 2: Excursion Participation Analysis
        JButton btnExcursionParticipation = createMenuButton("Excursion Participation Analysis", 
            "Analyze excursion participation rates, revenue metrics, and customer engagement", 
            50, 150, 330, 100);
        btnExcursionParticipation.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openExcursionParticipation();
            }
        });
        buttonsPanel.add(btnExcursionParticipation);
        
        // Button 3: Tour Guide Revenue
        JButton btnTourGuideRevenue = createMenuButton("Tour Guide Revenue Analysis", 
            "View comprehensive analysis of tour guide performance metrics and revenue", 
            50, 270, 330, 100);
        btnTourGuideRevenue.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openTourGuideRevenue();
            }
        });
        buttonsPanel.add(btnTourGuideRevenue);
        
        // Button 4: Add Language
        JButton btnAddLanguage = createMenuButton("Add Tour Guide Language", 
            "Associate a new language with a tour guide", 
            420, 30, 330, 100);
        btnAddLanguage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openAddLanguage();
            }
        });
        buttonsPanel.add(btnAddLanguage);
        
        // Button 5: Update Trip
        JButton btnUpdateTrip = createMenuButton("Update Trip Details", 
            "Modify price and guide information for existing trips", 
            420, 150, 330, 100);
        btnUpdateTrip.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openUpdateTrip();
            }
        });
        buttonsPanel.add(btnUpdateTrip);
        
        // Button 6: Return to Main Menu
        JButton btnMainMenu = createMenuButton("Return to Main Menu", 
            "Go back to the main navigation menu", 
            420, 270, 330, 100);
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
        
        JLabel lblStatus = new JLabel(" Travel Database Module");
        lblStatus.setForeground(Color.WHITE);
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblStatus.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        footerPanel.add(lblStatus, BorderLayout.WEST);
        
        JButton btnExit = createStyledButton("Exit");
        btnExit.setPreferredSize(new Dimension(100, 30));
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
                    g2.setColor(new Color(30, 144, 255)); // Pressed
                } else if (getModel().isRollover()) {
                    g2.setColor(LIGHT_BLUE); // Hover
                } else {
                    g2.setColor(new Color(51, 102, 153)); // Normal
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                g2.setColor(new Color(255, 255, 255, 50)); // Subtle border
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                
                // Draw title
                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                g2.setColor(Color.WHITE);
                
                FontMetrics fm = g2.getFontMetrics();
                int titleWidth = fm.stringWidth(title);
                int titleX = (getWidth() - titleWidth) / 2;
                g2.drawString(title, titleX, 30);
                
                // Draw description
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                g2.setColor(new Color(200, 230, 255));
                
                drawWrappedText(g2, description, 15, 50, getWidth() - 30);
                
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
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(new Color(30, 144, 255));
                } else if (getModel().isRollover()) {
                    g2.setColor(LIGHT_BLUE);
                } else {
                    g2.setColor(new Color(51, 102, 153));
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                g2.setColor(new Color(255, 255, 255, 50));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int x = (getWidth() - textWidth) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                
                g2.setColor(Color.WHITE);
                g2.drawString(text, x, y);
                g2.dispose();
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    /**
     * Open Hotel Occupancy Analysis
     */
    private void openHotelOccupancy() {
        travel.Query1 frame = new travel.Query1();
        frame.setVisible(true);
    }
    
    /**
     * Open Excursion Participation Analysis
     */
    private void openExcursionParticipation() {
        travel.Query2 frame = new travel.Query2();
        frame.setVisible(true);
    }
    
    /**
     * Open Tour Guide Revenue Analysis
     */
    private void openTourGuideRevenue() {
        Query3 frame = new Query3();
        frame.setVisible(true);
    }
    
    /**
     * Open Add Language form
     */
    private void openAddLanguage() {
        AddLanguage frame = new AddLanguage();
        frame.setVisible(true);
    }
    
    /**
     * Open Update Trip form
     */
    private void openUpdateTrip() {
        UpdateTrip frame = new UpdateTrip();
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