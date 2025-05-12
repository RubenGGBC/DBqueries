package queryes;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Dimension;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;
import java.awt.SystemColor;
import javax.swing.UIManager;

public class QueryMenu extends JFrame {
    private JPanel contentPane;

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
                    QueryMenu frame = new QueryMenu();
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
    public QueryMenu() {
        setTitle("Query Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 500);
        setLocationRelativeTo(null); // Center on screen
        setResizable(false);
       
        // Custom panel with gradient background
        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(40, 60, 134),
                                                  getWidth(), getHeight(), new Color(69, 162, 158));
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
        headerPanel.setBounds(0, 0, 784, 80);
        contentPane.add(headerPanel);
        headerPanel.setLayout(null);
       
        // Application title
        JLabel lblTitle = new JLabel("QUERY MANAGEMENT SYSTEM");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setBounds(10, 11, 764, 58);
        headerPanel.add(lblTitle);
       
        // Main panel for buttons
        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(false);
        mainPanel.setBounds(50, 100, 684, 340);
        contentPane.add(mainPanel);
        mainPanel.setLayout(null);
       
        // Company queries button with modern styling
        JButton btnCompany = createStyledButton("Company Queries", 20, 30, 300, 60);
        btnCompany.setIcon(createIcon("company"));
        mainPanel.add(btnCompany);
       
        // Travel queries button with modern styling
        JButton btnTravel = createStyledButton("Travel Queries", 20, 120, 300, 60);
        btnTravel.setIcon(createIcon("travel"));
        mainPanel.add(btnTravel);
       
        // Personnel queries button with modern styling
        JButton btnPersonnel = createStyledButton("Personnel Queries", 20, 210, 300, 60);
        btnPersonnel.setIcon(createIcon("personnel"));
        mainPanel.add(btnPersonnel);
       
        // Right panel with information
        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setBounds(360, 30, 300, 240);
        infoPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(255, 255, 255, 100), 1),
                new EmptyBorder(15, 15, 15, 15)));
        mainPanel.add(infoPanel);
        infoPanel.setLayout(null);
       
        JLabel lblInfo = new JLabel("Welcome to Query System");
        lblInfo.setForeground(Color.WHITE);
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblInfo.setBounds(15, 15, 270, 30);
        infoPanel.add(lblInfo);
       
        JLabel lblInfoDesc = new JLabel("<html>Select a query type from the menu to begin. This system allows you to manage different types of queries related to company operations, travel management, and personnel information.</html>");
        lblInfoDesc.setForeground(new Color(240, 240, 240));
        lblInfoDesc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblInfoDesc.setBounds(15, 55, 270, 120);
        infoPanel.add(lblInfoDesc);
       
        // Status bar at bottom
        JPanel statusBar = new JPanel();
        statusBar.setBackground(new Color(30, 30, 30, 150));
        statusBar.setBounds(0, 450, 784, 20);
        contentPane.add(statusBar);
        statusBar.setLayout(new BorderLayout(0, 0));
       
        JLabel lblStatus = new JLabel(" Ready");
        lblStatus.setForeground(Color.WHITE);
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusBar.add(lblStatus, BorderLayout.WEST);
       
        JLabel lblVersion = new JLabel("v1.0 ");
        lblVersion.setHorizontalAlignment(SwingConstants.RIGHT);
        lblVersion.setForeground(Color.WHITE);
        lblVersion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusBar.add(lblVersion, BorderLayout.EAST);
    }
   
    /**
     * Creates a styled button with hover effects
     */
    private JButton createStyledButton(String text, int x, int y, int width, int height) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
               
                if (getModel().isPressed()) {
                    g2.setColor(new Color(70, 130, 180, 200));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(100, 180, 255, 200));
                } else {
                    g2.setColor(new Color(60, 100, 160, 200));
                }
               
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
               
                g2.setColor(new Color(255, 255, 255, 50));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
               
                super.paintComponent(g2);
                g2.dispose();
            }
        };
       
        button.setBounds(x, y, width, height);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(15);
       
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(new Color(255, 255, 150));
            }
           
            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(Color.WHITE);
            }
        });
       
        return button;
    }
   
    /**
     * Creates an icon placeholder (in a real app, you would load actual icons)
     */
    private ImageIcon createIcon(String type) {
        // This is a placeholder. In a real application, you would load actual icons
        // For example: return new ImageIcon(getClass().getResource("/images/" + type + ".png"));
        return null;
    }
}