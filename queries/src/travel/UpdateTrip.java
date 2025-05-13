package travel;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.text.NumberFormat;
import java.util.Locale;

public class UpdateTrip extends JFrame {
    private JPanel contentPane;
    private JTextField txtTripTo;
    private JTextField txtDepartureDate;
    private JTextField txtNewPrice;
    private JTextField txtNewGuideId;
    private JTextField txtStatus;
    private JButton btnUpdate;
    private JButton btnClear;
    private JButton btnExit;
    private JButton btnSearch;
    
    private static final String DB_URL = "jdbc:mysql://dif-mysql.ehu.es:23306/DBI08";
    private static final String USER = "DBI08";
    private static final String PASS = "DBI08";
    
    // Store current trip details
    private double currentPrice = 0.0;
    private int currentGuideId = 0;
    private boolean tripFound = false;
    
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
                    UpdateTrip frame = new UpdateTrip();
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
    public UpdateTrip() {
        setTitle("Update Trip Details");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 600);
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
        
        JLabel lblTitle = new JLabel("Update Trip Details");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setBounds(10, 11, 764, 42);
        panelHeader.add(lblTitle);
        
        JLabel lblSubtitle = new JLabel("Modify price and guide information for existing trips");
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
        
        // Trip ID Fields (for identification)
        JPanel identificationPanel = new JPanel();
        identificationPanel.setOpaque(false);
        identificationPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 150), 1),
                "Trip Identification",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                Color.WHITE));
        identificationPanel.setBounds(50, 20, 700, 120);
        panelForm.add(identificationPanel);
        identificationPanel.setLayout(null);
        
        // Trip To
        JLabel lblTripTo = new JLabel("Trip To:");
        lblTripTo.setForeground(Color.WHITE);
        lblTripTo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTripTo.setBounds(20, 30, 120, 25);
        identificationPanel.add(lblTripTo);
        
        txtTripTo = new JTextField();
        txtTripTo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTripTo.setBounds(140, 30, 200, 30);
        txtTripTo.setBorder(null);
        identificationPanel.add(txtTripTo);
        
        // Departure Date
        JLabel lblDepartureDate = new JLabel("Departure Date:");
        lblDepartureDate.setForeground(Color.WHITE);
        lblDepartureDate.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblDepartureDate.setBounds(350, 30, 120, 25);
        identificationPanel.add(lblDepartureDate);
        
        txtDepartureDate = new JTextField();
        txtDepartureDate.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDepartureDate.setBounds(470, 30, 200, 30);
        txtDepartureDate.setBorder(null);
        identificationPanel.add(txtDepartureDate);
        
        // Search button
        btnSearch = createStyledButton("Search Trip");
        btnSearch.setBounds(270, 70, 150, 35);
        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchTrip();
            }
        });
        identificationPanel.add(btnSearch);
        
        // Update Fields Panel
        JPanel updatePanel = new JPanel();
        updatePanel.setOpaque(false);
        updatePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 150), 1),
                "Update Information",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                Color.WHITE));
        updatePanel.setBounds(50, 150, 700, 150);
        panelForm.add(updatePanel);
        updatePanel.setLayout(null);
        
        // New Price Per Day
        JLabel lblNewPrice = new JLabel("New Price Per Day ($):");
        lblNewPrice.setForeground(Color.WHITE);
        lblNewPrice.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNewPrice.setBounds(20, 40, 150, 25);
        updatePanel.add(lblNewPrice);
        
        txtNewPrice = new JTextField();
        txtNewPrice.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNewPrice.setBounds(170, 40, 150, 30);
        txtNewPrice.setBorder(null);
        updatePanel.add(txtNewPrice);
        
        // New Guide ID
        JLabel lblNewGuideId = new JLabel("New Guide ID:");
        lblNewGuideId.setForeground(Color.WHITE);
        lblNewGuideId.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNewGuideId.setBounds(350, 40, 120, 25);
        updatePanel.add(lblNewGuideId);
        
        txtNewGuideId = new JTextField();
        txtNewGuideId.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNewGuideId.setBounds(470, 40, 150, 30);
        txtNewGuideId.setBorder(null);
        updatePanel.add(txtNewGuideId);
        
        // Help text
        JLabel lblHelp = new JLabel("<html>Enter new values only for the fields you want to update. Leave fields blank to keep current values.</html>");
        lblHelp.setForeground(new Color(200, 230, 255));
        lblHelp.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblHelp.setBounds(20, 90, 660, 40);
        updatePanel.add(lblHelp);
        
        // Information panel
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(70, 130, 180, 120));
        infoPanel.setBounds(50, 320, 700, 80);
        infoPanel.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 100)));
        panelForm.add(infoPanel);
        infoPanel.setLayout(new BorderLayout());
        
        JLabel lblInfo = new JLabel("<html>First search for an existing trip using 'Trip To' destination and 'Departure Date'. " +
                "Once found, enter the new price per day and/or new guide ID to update the trip details. " +
                "Both the trip destination and departure date must be entered to identify the trip.</html>");
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
        
        // Status field
        txtStatus = new JTextField("Ready to update trip");
        txtStatus.setEditable(false);
        txtStatus.setForeground(new Color(200, 255, 200));
        txtStatus.setBackground(new Color(25, 84, 123));
        txtStatus.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtStatus.setBorder(null);
        txtStatus.setHorizontalAlignment(SwingConstants.CENTER);
        txtStatus.setPreferredSize(new Dimension(250, 35));
        panelFooter.add(txtStatus);
        
        // Update button
        btnUpdate = createStyledButton("Update Trip");
        btnUpdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateTripDetails();
            }
        });
        btnUpdate.setEnabled(false); // Initially disabled until trip is found
        panelFooter.add(btnUpdate);
        
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
                
                if (!isEnabled()) {
                    g2.setColor(new Color(100, 100, 100));
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                g2.setColor(new Color(255, 255, 255, 50));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                
                FontMetrics fm = g2.getFontMetrics();
                Rectangle2D r = fm.getStringBounds(getText(), g2);
                int x = (getWidth() - (int)r.getWidth()) / 2;
                int y = (getHeight() - (int)r.getHeight()) / 2 + fm.getAscent();
                
                g2.setColor(isEnabled() ? Color.WHITE : new Color(200, 200, 200));
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
        txtTripTo.setText("");
        txtDepartureDate.setText("");
        txtNewPrice.setText("");
        txtNewGuideId.setText("");
        txtStatus.setText("Form cleared");
        txtStatus.setForeground(new Color(200, 255, 200));
        btnUpdate.setEnabled(false);
        tripFound = false;
    }
    
    /**
     * Search for a trip in the database
     */
    private void searchTrip() {
        // Validate input
        if (txtTripTo.getText().trim().isEmpty() || txtDepartureDate.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Both Trip To and Departure Date fields are required to identify a trip.", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String tripTo = txtTripTo.getText().trim();
        String departureDate = txtDepartureDate.getText().trim();
        
        // Update status
        txtStatus.setText("Searching for trip...");
        
        // Use SwingWorker to perform database operations in background
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private boolean success = false;
            private String errorMessage = "";
            
            @Override
            protected Void doInBackground() throws Exception {
                Connection conn = null;
                PreparedStatement pstmt = null;
                ResultSet rs = null;
                
                try {
                    // Connect to database
                    conn = DriverManager.getConnection(DB_URL, USER, PASS);
                    
                    // Search for the trip
                    String query = "SELECT ppday, GuideId FROM trip WHERE TripTo = ? AND DepartureDate = ?";
                    pstmt = conn.prepareStatement(query);
                    pstmt.setString(1, tripTo);
                    pstmt.setString(2, departureDate);
                    rs = pstmt.executeQuery();
                    
                    if (rs.next()) {
                        currentPrice = rs.getDouble("ppday");
                        currentGuideId = rs.getInt("GuideId");
                        success = true;
                        tripFound = true;
                    } else {
                        errorMessage = "Trip not found. Please check the destination and departure date.";
                        success = false;
                        tripFound = false;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    errorMessage = e.getMessage();
                    success = false;
                    tripFound = false;
                } finally {
                    try {
                        if (rs != null) rs.close();
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
                    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
                    txtStatus.setText("Trip found. Ready to update.");
                    txtStatus.setForeground(new Color(200, 255, 200));
                    
                    // Show current values as placeholders
                    txtNewPrice.setText("");
                    txtNewPrice.setToolTipText("Current: " + currencyFormat.format(currentPrice));
                    
                    txtNewGuideId.setText("");
                    txtNewGuideId.setToolTipText("Current: " + currentGuideId);
                    
                    btnUpdate.setEnabled(true);
                    
                    JOptionPane.showMessageDialog(UpdateTrip.this,
                        "Trip found!\nCurrent Price Per Day: " + currencyFormat.format(currentPrice) + 
                        "\nCurrent Guide ID: " + currentGuideId,
                        "Trip Found",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    txtStatus.setText("Trip not found");
                    txtStatus.setForeground(new Color(255, 150, 150));
                    btnUpdate.setEnabled(false);
                    
                    JOptionPane.showMessageDialog(UpdateTrip.this,
                        errorMessage,
                        "Search Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Update trip details in the database
     */
    private void updateTripDetails() {
        if (!tripFound) {
            JOptionPane.showMessageDialog(this, 
                "Please search for a trip first.", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check if at least one field has a new value
        if (txtNewPrice.getText().trim().isEmpty() && txtNewGuideId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter at least one field to update (price or guide ID).", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validate new price if provided
        Double newPrice = null;
        if (!txtNewPrice.getText().trim().isEmpty()) {
            try {
                newPrice = Double.parseDouble(txtNewPrice.getText().trim());
                if (newPrice <= 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Price must be a positive number.", 
                        "Validation Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "Price must be a valid number.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        // Validate new guide ID if provided
        Integer newGuideId = null;
        if (!txtNewGuideId.getText().trim().isEmpty()) {
            try {
                newGuideId = Integer.parseInt(txtNewGuideId.getText().trim());
                if (newGuideId <= 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Guide ID must be a positive number.", 
                        "Validation Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "Guide ID must be a valid number.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        final Double finalNewPrice = newPrice;
        final Integer finalNewGuideId = newGuideId;
        
        // Update status
        txtStatus.setText("Updating trip...");
        
        // Use SwingWorker to perform database operations in background
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private boolean success = false;
            private String errorMessage = "";
            
            @Override
            protected Void doInBackground() throws Exception {
                Connection conn = null;
                PreparedStatement pstmt = null;
                
                try {
                    // Connect to database
                    conn = DriverManager.getConnection(DB_URL, USER, PASS);
                    
                    // If updating guide ID, check if it exists
                    if (finalNewGuideId != null) {
                        pstmt = conn.prepareStatement("SELECT COUNT(*) FROM tourguide WHERE GuideId = ?");
                        pstmt.setInt(1, finalNewGuideId);
                        ResultSet rs = pstmt.executeQuery();
                        rs.next();
                        int count = rs.getInt(1);
                        
                        if (count == 0) {
                            errorMessage = "Guide with ID " + finalNewGuideId + " does not exist.";
                            success = false;
                            return null;
                        }
                        pstmt.close();
                    }
                    
                    // Prepare update query based on which fields are being updated
                    StringBuilder queryBuilder = new StringBuilder("UPDATE trip SET");
                    boolean needsComma = false;
                    
                    if (finalNewPrice != null) {
                        queryBuilder.append(" ppday = ?");
                        needsComma = true;
                    }
                    
                    if (finalNewGuideId != null) {
                        if (needsComma) {
                            queryBuilder.append(",");
                        }
                        queryBuilder.append(" GuideId = ?");
                    }
                    
                    queryBuilder.append(" WHERE TripTo = ? AND DepartureDate = ?");
                    
                    pstmt = conn.prepareStatement(queryBuilder.toString());
                    
                    int paramIndex = 1;
                    if (finalNewPrice != null) {
                        pstmt.setDouble(paramIndex++, finalNewPrice);
                    }
                    
                    if (finalNewGuideId != null) {
                        pstmt.setInt(paramIndex++, finalNewGuideId);
                    }
                    
                    pstmt.setString(paramIndex++, txtTripTo.getText().trim());
                    pstmt.setString(paramIndex, txtDepartureDate.getText().trim());
                    
                    int rowsUpdated = pstmt.executeUpdate();
                    
                    if (rowsUpdated > 0) {
                        success = true;
                    } else {
                        errorMessage = "Update failed. Trip may no longer exist.";
                        success = false;
                    }
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
                    txtStatus.setText("Trip updated successfully");
                    txtStatus.setForeground(new Color(200, 255, 200));
                    
                    JOptionPane.showMessageDialog(UpdateTrip.this,
                        "Trip has been updated successfully!",
                        "Update Successful",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Update current values
                    if (finalNewPrice != null) {
                        currentPrice = finalNewPrice;
                    }
                    
                    if (finalNewGuideId != null) {
                        currentGuideId = finalNewGuideId;
                    }
                    
                    // Clear the fields but keep the trip search fields
                    txtNewPrice.setText("");
                    txtNewGuideId.setText("");
                    
                    // Update tooltips with new values
                    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
                    txtNewPrice.setToolTipText("Current: " + currencyFormat.format(currentPrice));
                    txtNewGuideId.setToolTipText("Current: " + currentGuideId);
                } else {
                    txtStatus.setText("Update failed");
                    txtStatus.setForeground(new Color(255, 150, 150));
                    
                    JOptionPane.showMessageDialog(UpdateTrip.this,
                        "Error updating trip: " + errorMessage,
                        "Update Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
}