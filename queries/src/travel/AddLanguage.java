package travel;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class AddLanguage extends JFrame {
    private JPanel contentPane;
    private JTextField txtGuideId;
    private JTextField txtLanguage;
    private JTextField txtStatus;
    private JButton btnAdd;
    private JButton btnClear;
    private JButton btnExit;
    
    private static final String DB_URL = "jdbc:mysql://dif-mysql.ehu.es:23306/DBI08";
    private static final String USER = "DBI08";
    private static final String PASS = "DBI08";
    
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
                    AddLanguage frame = new AddLanguage();
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
    public AddLanguage() {
        setTitle("Add Tour Guide Language");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 500);
        setLocationRelativeTo(null);
        
        // Main panel with gradient background
        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(25, 84, 123), 
                                                 getWidth(), getHeight(), new Color(15, 54, 82));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));
        
        // Header panel
        JPanel panelHeader = new JPanel();
        panelHeader.setOpaque(false);
        panelHeader.setPreferredSize(new Dimension(800, 100));
        contentPane.add(panelHeader, BorderLayout.NORTH);
        panelHeader.setLayout(null);
        
        JLabel lblTitle = new JLabel("Add Tour Guide Language");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setBounds(10, 11, 764, 42);
        panelHeader.add(lblTitle);
        
        JLabel lblSubtitle = new JLabel("Associate a new language with a tour guide");
        lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblSubtitle.setForeground(new Color(200, 230, 255));
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtitle.setBounds(10, 50, 764, 30);
        panelHeader.add(lblSubtitle);
        
        // Form panel
        JPanel panelForm = new JPanel();
        panelForm.setOpaque(false);
        contentPane.add(panelForm, BorderLayout.CENTER);
        panelForm.setLayout(null);
        
        // Guide ID
        JLabel lblGuideId = new JLabel("Guide ID:");
        lblGuideId.setForeground(Color.WHITE);
        lblGuideId.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblGuideId.setBounds(200, 50, 150, 30);
        panelForm.add(lblGuideId);
        
        txtGuideId = new JTextField();
        txtGuideId.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtGuideId.setBounds(350, 50, 200, 35);
        txtGuideId.setBorder(null);
        panelForm.add(txtGuideId);
        
        // Language
        JLabel lblLanguage = new JLabel("Language:");
        lblLanguage.setForeground(Color.WHITE);
        lblLanguage.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblLanguage.setBounds(200, 110, 150, 30);
        panelForm.add(lblLanguage);
        
        txtLanguage = new JTextField();
        txtLanguage.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtLanguage.setBounds(350, 110, 200, 35);
        txtLanguage.setBorder(null);
        panelForm.add(txtLanguage);
        
        // Information panel
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(70, 130, 180, 120));
        infoPanel.setBounds(200, 170, 350, 80);
        infoPanel.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 100)));
        panelForm.add(infoPanel);
        infoPanel.setLayout(new BorderLayout());
        
        JLabel lblInfo = new JLabel("<html>Add a new language that a tour guide can speak. " +
                "Both the Guide ID and Language name are required. Guide must exist in the database.</html>");
        lblInfo.setForeground(Color.WHITE);
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoPanel.add(lblInfo, BorderLayout.CENTER);
        
        // Footer panel with controls
        JPanel panelFooter = new JPanel();
        panelFooter.setOpaque(false);
        panelFooter.setPreferredSize(new Dimension(800, 100));
        contentPane.add(panelFooter, BorderLayout.SOUTH);
        panelFooter.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 30));
        
        // Add status field
        txtStatus = new JTextField("Ready to add language");
        txtStatus.setEditable(false);
        txtStatus.setForeground(new Color(200, 255, 200));
        txtStatus.setBackground(new Color(25, 84, 123));
        txtStatus.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtStatus.setBorder(null);
        txtStatus.setHorizontalAlignment(SwingConstants.CENTER);
        txtStatus.setPreferredSize(new Dimension(250, 35));
        panelFooter.add(txtStatus);
        
        // Add button
        btnAdd = createStyledButton("Add Language");
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addLanguageToDatabase();
            }
        });
        panelFooter.add(btnAdd);
        
        // Clear button
        btnClear = createStyledButton("Clear Form");
        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
        panelFooter.add(btnClear);
        
        // Exit button
        btnExit = createStyledButton("Exit");
        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        panelFooter.add(btnExit);
    }
    
    /**
     * Creates a styled button with visual effects
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(new Color(30, 144, 255));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(70, 130, 180));
                } else {
                    g2.setColor(new Color(51, 102, 153));
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                g2.setColor(new Color(255, 255, 255, 50));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                
                FontMetrics fm = g2.getFontMetrics();
                Rectangle2D r = fm.getStringBounds(getText(), g2);
                int x = (getWidth() - (int)r.getWidth()) / 2;
                int y = (getHeight() - (int)r.getHeight()) / 2 + fm.getAscent();
                
                g2.setColor(Color.WHITE);
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setPreferredSize(new Dimension(130, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    /**
     * Clear the form fields
     */
    private void clearForm() {
        txtGuideId.setText("");
        txtLanguage.setText("");
        txtStatus.setText("Form cleared");
    }
    
    /**
     * Add language to database
     */
    private void addLanguageToDatabase() {
        // Validate input
        if (txtGuideId.getText().trim().isEmpty() || txtLanguage.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Both Guide ID and Language fields are required.", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int guideId;
        try {
            guideId = Integer.parseInt(txtGuideId.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Guide ID must be a valid number.", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String language = txtLanguage.getText().trim();
        
        // Update status
        txtStatus.setText("Adding language...");
        
        // Use SwingWorker to perform database operations in background
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private boolean success = false;
            private String errorMessage = "";
            
            @Override
            protected Void doInBackground() throws Exception {
                Connection conn = null;
                PreparedStatement pstmt = null;
                
                try {
                    // First check if the guide exists
                    conn = DriverManager.getConnection(DB_URL, USER, PASS);
                    pstmt = conn.prepareStatement("SELECT COUNT(*) FROM tourguide WHERE GuideId = ?");
                    pstmt.setInt(1, guideId);
                    ResultSet rs = pstmt.executeQuery();
                    rs.next();
                    int count = rs.getInt(1);
                    
                    if (count == 0) {
                        errorMessage = "Tour guide with ID " + guideId + " does not exist.";
                        success = false;
                        return null;
                    }
                    
                    // Check if this language is already assigned to this guide
                    pstmt = conn.prepareStatement("SELECT COUNT(*) FROM languages WHERE GuideId = ? AND Lang = ?");
                    pstmt.setInt(1, guideId);
                    pstmt.setString(2, language);
                    rs = pstmt.executeQuery();
                    rs.next();
                    count = rs.getInt(1);
                    
                    if (count > 0) {
                        errorMessage = "This language is already assigned to this guide.";
                        success = false;
                        return null;
                    }
                    
                    // Add the language
                    pstmt = conn.prepareStatement("INSERT INTO languages (GuideId, Lang) VALUES (?, ?)");
                    pstmt.setInt(1, guideId);
                    pstmt.setString(2, language);
                    pstmt.executeUpdate();
                    
                    success = true;
                } catch (SQLException e) {
                    e.printStackTrace();
                    errorMessage = e.getMessage();
                    success = false;
                } finally {
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
                    txtStatus.setText("Language added successfully");
                    txtStatus.setForeground(new Color(200, 255, 200));
                    JOptionPane.showMessageDialog(AddLanguage.this,
                        "The language has been successfully added to the guide.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                } else {
                    txtStatus.setText("Error adding language");
                    txtStatus.setForeground(new Color(255, 150, 150));
                    JOptionPane.showMessageDialog(AddLanguage.this,
                        "Error adding language: " + errorMessage,
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
}