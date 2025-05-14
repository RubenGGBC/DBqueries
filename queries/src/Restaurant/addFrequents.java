package Restaurant;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.text.NumberFormat;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.util.Locale;
import java.awt.geom.RoundRectangle2D;

public class addFrequents extends JFrame {
    private static final String DB_URL = "jdbc:mysql://dif-mysql.ehu.es:23306/DBI08";
    private static final String USER = "DBI08";
    private static final String PASS = "DBI08";
    
    // Database table structure
    private static final String TABLE_NAME = "frequents";
    private static final String COL_NAME_ID = "nameId";
    private static final String COL_RESTAUR_NAME = "restaurname";
    
    // Pastel color theme consistent with Mondial frames
    private static final Color PASTEL_BACKGROUND = new Color(253, 245, 230); // Soft peach
    private static final Color PASTEL_HEADER = new Color(255, 228, 196); // Bisque
    private static final Color PASTEL_TEXT = new Color(119, 136, 153); // Slate gray
    private static final Color PASTEL_BUTTON = new Color(221, 160, 221); // Plum
    private static final Color PASTEL_BUTTON_TEXT = new Color(75, 0, 130); // Indigo
    private static final Color PASTEL_TABLE_HEADER = new Color(255, 222, 173); // Light orange
    private static final Color PASTEL_SELECTION = new Color(176, 224, 230); // Powder blue
    
    private JTextField nameIdField;
    private JTextField restaurantNameField;
    private JTable frequentsTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    
    public addFrequents() {
        setTitle("Restaurant Frequents Manager");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(PASTEL_BACKGROUND);
        setLayout(new BorderLayout(10, 10));
        
        initComponents();
        
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        // Create a header panel with the description
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PASTEL_HEADER);
        headerPanel.setPreferredSize(new Dimension(800, 80));
        headerPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Restaurant Frequents Database");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(PASTEL_TEXT);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JLabel subtitleLabel = new JLabel("Retrieve data about customers and the restaurants they frequent");
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleLabel.setForeground(PASTEL_TEXT);
        subtitleLabel.setFont(new Font("Serif", Font.ITALIC, 16));
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Create main content panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBackground(PASTEL_BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(3, 2, 10, 10));
        formPanel.setBackground(PASTEL_BACKGROUND);
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PASTEL_TEXT, 1),
            "Add New Frequent",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Serif", Font.BOLD, 14),
            PASTEL_TEXT
        ));
        
        JLabel nameIdLabel = new JLabel("Name ID:");
        nameIdLabel.setFont(new Font("Serif", Font.PLAIN, 14));
        nameIdLabel.setForeground(PASTEL_TEXT);
        formPanel.add(nameIdLabel);
        
        nameIdField = new JTextField(20);
        nameIdField.setFont(new Font("Serif", Font.PLAIN, 14));
        formPanel.add(nameIdField);
        
        JLabel restaurantNameLabel = new JLabel("Restaurant Name:");
        restaurantNameLabel.setFont(new Font("Serif", Font.PLAIN, 14));
        restaurantNameLabel.setForeground(PASTEL_TEXT);
        formPanel.add(restaurantNameLabel);
        
        restaurantNameField = new JTextField(20);
        restaurantNameField.setFont(new Font("Serif", Font.PLAIN, 14));
        formPanel.add(restaurantNameField);
        
        JButton addButton = createStyledButton("Add Frequent");
        addButton.addActionListener(e -> addFrequent());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(PASTEL_BACKGROUND);
        buttonPanel.add(addButton);
        
        formPanel.add(new JLabel(""));
        formPanel.add(buttonPanel);
        
        // Create table panel
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBackground(PASTEL_BACKGROUND);
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PASTEL_TEXT, 1),
            "Frequents Data",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Serif", Font.BOLD, 14),
            PASTEL_TEXT
        ));
        
        // Create table
        String[] columnNames = {"Name ID", "Restaurant Name"};
        tableModel = new DefaultTableModel(columnNames, 0);
        frequentsTable = new JTable(tableModel);
        frequentsTable.setFillsViewportHeight(true);
        frequentsTable.setBackground(Color.WHITE);
        frequentsTable.setForeground(PASTEL_TEXT);
        frequentsTable.setSelectionBackground(PASTEL_SELECTION);
        frequentsTable.setFont(new Font("Serif", Font.PLAIN, 14));
        frequentsTable.setRowHeight(25);
        frequentsTable.setIntercellSpacing(new Dimension(5, 0));
        frequentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Set custom renderer for alternate row colors
        frequentsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (isSelected) {
                    c.setBackground(PASTEL_SELECTION);
                    c.setForeground(PASTEL_TEXT);
                } else {
                    c.setBackground(row % 2 == 0 ? new Color(240, 248, 255) : Color.WHITE);
                    c.setForeground(PASTEL_TEXT);
                }
                
                ((JLabel) c).setHorizontalAlignment(column == 0 ? JLabel.LEFT : JLabel.CENTER);
                
                return c;
            }
        });
        
        // Table header styling
        JTableHeader header = frequentsTable.getTableHeader();
        header.setBackground(PASTEL_TABLE_HEADER);
        header.setForeground(PASTEL_TEXT);
        header.setFont(new Font("Serif", Font.BOLD, 14));
        
        JScrollPane scrollPane = new JScrollPane(frequentsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(PASTEL_TEXT, 1));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Arrange form and table with split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, formPanel, tablePanel);
        splitPane.setDividerLocation(150);
        splitPane.setBorder(null);
        splitPane.setBackground(PASTEL_BACKGROUND);
        
        mainPanel.add(splitPane, BorderLayout.CENTER);
        
        // Create button panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controlPanel.setBackground(PASTEL_BACKGROUND);
        controlPanel.setPreferredSize(new Dimension(800, 60));
        
        // Status label
        statusLabel = new JLabel("Ready to connect", JLabel.CENTER);
        statusLabel.setForeground(PASTEL_TEXT);
        statusLabel.setFont(new Font("Serif", Font.ITALIC, 14));
        statusLabel.setPreferredSize(new Dimension(200, 30));
        controlPanel.add(statusLabel);
        
        JButton connectButton = createStyledButton("Connect");
        connectButton.addActionListener(e -> loadFrequentsData());
        controlPanel.add(connectButton);
        
        JButton refreshButton = createStyledButton("Refresh");
        refreshButton.addActionListener(e -> loadFrequentsData());
        controlPanel.add(refreshButton);
        
        JButton exitButton = createStyledButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));
        controlPanel.add(exitButton);
        
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Load data on startup
        SwingUtilities.invokeLater(() -> loadFrequentsData());
    }
    
    private JButton createStyledButton(String text) {
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
                
                // Draw text
                g2.setFont(new Font("Serif", Font.BOLD, 14));
                g2.setColor(PASTEL_BUTTON_TEXT);
                
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int x = (getWidth() - textWidth) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(text, x, y);
                
                g2.dispose();
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(120, 40);
            }
        };
        
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private void loadFrequentsData() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            statusLabel.setText("Connecting to database...");
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
            
            // Clear existing table data
            tableModel.setRowCount(0);
            
            // Execute SELECT query
            String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL_NAME_ID;
            rs = stmt.executeQuery(sql);
            
            // Process results
            while (rs.next()) {
                String nameId = rs.getString(COL_NAME_ID);
                String restaurName = rs.getString(COL_RESTAUR_NAME);
                
                // Add row to table model
                tableModel.addRow(new Object[]{nameId, restaurName});
            }
            
            statusLabel.setText("Connected - Data loaded successfully");
        } catch (SQLException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading data: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            statusLabel.setText("Error connecting to database");
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void addFrequent() {
        String nameId = nameIdField.getText().trim();
        String restaurantName = restaurantNameField.getText().trim();
        
        // Validate input
        if (nameId.isEmpty() || restaurantName.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter both Name ID and Restaurant Name", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            statusLabel.setText("Adding new frequent...");
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            
            // Disable auto-commit
            conn.setAutoCommit(false);
            
            // Add new row in frequents table
            String sql = "INSERT INTO " + TABLE_NAME + " VALUES (?, ?)";
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, nameId);
            pstmt.setString(2, restaurantName);
            pstmt.executeUpdate();
            
            // Commit changes
            conn.commit();
            
            // Clear form fields
            nameIdField.setText("");
            restaurantNameField.setText("");
            
            // Refresh table data
            loadFrequentsData();
            
            JOptionPane.showMessageDialog(this, 
                "Record added successfully", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
                
            statusLabel.setText("Connected - Record added successfully");
        } catch (SQLException | ClassNotFoundException e) {
            try {
                // Rollback the transaction
                conn.rollback();
                
                JOptionPane.showMessageDialog(this, 
                    "Rollback has been done. " + e.getMessage(), 
                    "Database Error", 
                    JOptionPane.ERROR_MESSAGE);
                    
                statusLabel.setText("Error - Transaction rolled back");
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
    
    public static void main(String[] args) {
        try {
            // Set look and feel to system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            addFrequents app = new addFrequents();
            app.setVisible(true);
        });
    }
}