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
import java.util.Vector;

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
    private JTable tblTrips;
    private JScrollPane scrollPane;
    private DefaultTableModel tableModel;
    
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
        setBounds(100, 100, 1000, 800); // Increased size to accommodate table
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
        panelHeader.setPreferredSize(new Dimension(1000, 100));
        contentPane.add(panelHeader, BorderLayout.NORTH);
        panelHeader.setLayout(null);
        
        JLabel lblTitle = new JLabel("Update Trip Details");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setBounds(10, 11, 964, 42);
        panelHeader.add(lblTitle);
        
        JLabel lblSubtitle = new JLabel("Modify price and guide information for existing trips");
        lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblSubtitle.setForeground(new Color(200, 230, 255));
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtitle.setBounds(10, 50, 964, 30);
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
        identificationPanel.setBounds(50, 20, 900, 120);
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
        
        // Load data button
        JButton btnLoadData = createStyledButton("Load All Trips");
        btnLoadData.setBounds(450, 70, 150, 35);
        btnLoadData.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadAllTrips();
            }
        });
        identificationPanel.add(btnLoadData);
        
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
        updatePanel.setBounds(50, 150, 900, 150);
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
        
        // Table Panel
        JPanel tablePanel = new JPanel();
        tablePanel.setOpaque(false);
        tablePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 150), 1),
                "Trip Database",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                Color.WHITE));
        tablePanel.setBounds(50, 320, 900, 330);
        panelForm.add(tablePanel);
        tablePanel.setLayout(new BorderLayout());
        
        // Create table model with column names
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        tableModel.addColumn("Trip To");
        tableModel.addColumn("Departure Date");
        tableModel.addColumn("Price/Day ($)");
        tableModel.addColumn("Guide ID");
        
        // Create and configure the table
        tblTrips = new JTable(tableModel);
        tblTrips.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblTrips.setRowHeight(25);
        tblTrips.setShowGrid(true);
        tblTrips.setGridColor(new Color(200, 200, 200));
        tblTrips.setSelectionBackground(new Color(70, 130, 180));
        tblTrips.setSelectionForeground(Color.WHITE);
        tblTrips.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tblTrips.getTableHeader().setBackground(new Color(51, 102, 153));
        tblTrips.getTableHeader().setForeground(Color.BLACK);
        
        // Add row selection listener to populate fields when a row is selected
        tblTrips.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblTrips.getSelectedRow() != -1) {
                int row = tblTrips.getSelectedRow();
                txtTripTo.setText(tblTrips.getValueAt(row, 0).toString());
                txtDepartureDate.setText(tblTrips.getValueAt(row, 1).toString());
                
                // Store current values
                currentPrice = Double.parseDouble(tblTrips.getValueAt(row, 2).toString().replace("$", "").replace(",", ""));
                currentGuideId = Integer.parseInt(tblTrips.getValueAt(row, 3).toString());
                
                // Set tooltips
                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
                txtNewPrice.setToolTipText("Current: " + currencyFormat.format(currentPrice));
                txtNewGuideId.setToolTipText("Current: " + currentGuideId);
                
                tripFound = true;
                btnUpdate.setEnabled(true);
                txtStatus.setText("Trip selected. Ready to update.");
                txtStatus.setForeground(new Color(200, 255, 200));
            }
        });
        
        // Create scroll pane for the table
        scrollPane = new JScrollPane(tblTrips);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        // Style the scroll bars
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(70, 130, 180);
                this.trackColor = new Color(25, 84, 123);
            }
        });
        
        scrollPane.getHorizontalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(70, 130, 180);
                this.trackColor = new Color(25, 84, 123);
            }
        });
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Footer panel with controls
        JPanel panelFooter = new JPanel();
        panelFooter.setOpaque(false);
        panelFooter.setPreferredSize(new Dimension(1000, 100));
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
        
        // Load all trips when the application starts
        loadAllTrips();
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
     * Load all trips from the database into the table
     */
    private void loadAllTrips() {
        // Update status
        txtStatus.setText("Loading trips...");
        
        // Clear the table
        tableModel.setRowCount(0);
        
        // Use SwingWorker to perform database operations in background
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                Connection conn = null;
                Statement stmt = null;
                ResultSet rs = null;
                
                try {
                    // Connect to database
                    conn = DriverManager.getConnection(DB_URL, USER, PASS);
                    
                    // Query all trips
                    String query = "SELECT TripTo, DepartureDate, Ppday, GuideId FROM trip";
                    stmt = conn.createStatement();
                    rs = stmt.executeQuery(query);
                    
                    // Format for currency
                    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
                    
                    // Add rows to the table
                    while (rs.next()) {
                        Vector<Object> row = new Vector<>();
                        row.add(rs.getString("TripTo"));
                        row.add(rs.getString("DepartureDate"));
                        row.add(currencyFormat.format(rs.getDouble("Ppday")));
                        row.add(rs.getInt("GuideId"));
                        tableModel.addRow(row);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(UpdateTrip.this,
                                "Error loading trips: " + e.getMessage(),
                                "Database Error",
                                JOptionPane.ERROR_MESSAGE);
                    });
                } finally {
                    try {
                        if (rs != null) rs.close();
                        if (stmt != null) stmt.close();
                        if (conn != null) conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                
                return null;
            }
            
            @Override
            protected void done() {
                txtStatus.setText("Trips loaded. " + tableModel.getRowCount() + " trips found.");
                txtStatus.setForeground(new Color(200, 255, 200));
            }
        };
        
        worker.execute();
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
        
        // Clear table selection
        tblTrips.clearSelection();
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
            private int foundRow = -1;
            
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
                        
                        // Try to find the row in the table
                        for (int i = 0; i < tableModel.getRowCount(); i++) {
                            if (tableModel.getValueAt(i, 0).equals(tripTo) && 
                                tableModel.getValueAt(i, 1).equals(departureDate)) {
                                foundRow = i;
                                break;
                            }
                        }
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
                    
                    // Select the row in the table if found
                    if (foundRow != -1) {
                        tblTrips.setRowSelectionInterval(foundRow, foundRow);
                        tblTrips.scrollRectToVisible(tblTrips.getCellRect(foundRow, 0, true));
                    }
                    
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
        
        // Get new values from fields
        String newPriceText = txtNewPrice.getText().trim();
        String newGuideIdText = txtNewGuideId.getText().trim();
        
   
        // Parse and validate input values
        Double newPrice = null;
        Integer newGuideId = null;
        
        try {
            newPrice = Double.parseDouble(newPriceText);
            System.out.println(newPrice);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Invalid price format. Please enter a valid number.", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            newGuideId = Integer.parseInt(newGuideIdText);
            System.out.println(newGuideId);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Invalid Guide ID format. Please enter a valid number.", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
       
        // Update status
        txtStatus.setText("Updating trip...");
            
        Connection conn = null;
        PreparedStatement pstmt = null;
                
        try {
        	Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
                    
            // Set auto-commit to false for transaction
            conn.setAutoCommit(false);
                    
            // Prepare update query
            pstmt = conn.prepareStatement("UPDATE trip SET Ppday = ?, GuideId = ? WHERE TripTo = ? AND DepartureDate = ?");
                    
            pstmt.setDouble(1, newPrice);
            pstmt.setInt(2, newGuideId);
            pstmt.setString(3, txtTripTo.getText().trim());
            pstmt.setString(4, txtDepartureDate.getText().trim());
            
            //Execute Update
            pstmt.executeUpdate();
                    
            conn.commit();
            
            JOptionPane.showMessageDialog(this, 
                    "Record updated successfully", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            
            //Refresh the data of the table
            loadAllTrips();
            
        } catch (SQLException e) {
            try {
            	// Rollback the transaction
                conn.rollback();
                                
                JOptionPane.showMessageDialog(this, 
                    "Rollback has been done: " +
                    "It is likely that the introduced guideId is not in the list of guides" , 
                    "Database Error", 
                    JOptionPane.ERROR_MESSAGE);
                                    
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error during rollback: " + ex.getMessage(), 
                    "Database Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
        	JOptionPane.showMessageDialog(this, 
        			"There has been an error: " + e.getMessage(), 
                    "Database Error", 
                    JOptionPane.ERROR_MESSAGE);
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