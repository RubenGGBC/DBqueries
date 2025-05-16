package restaurant;

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

public class UpdateMenuOrder extends JFrame {
    private static final String DB_URL = "jdbc:mysql://dif-mysql.ehu.es:23306/DBI08";
    private static final String USER = "DBI08";
    private static final String PASS = "DBI08";
    
    // Database table structure
    private static final String TABLE_NAME = "menu_order";
    private static final String COL_NUMORD = "numord";
    private static final String COL_MENU_TYPE = "menu_mtype";
    private static final String COL_MENU_ID = "menu_id";
    
    // Nuevo esquema de colores rojo/naranja
    private static final Color PASTEL_BACKGROUND = new Color(255, 245, 238); // Seashell
    private static final Color PASTEL_HEADER = new Color(255, 160, 122); // Light salmon
    private static final Color PASTEL_TEXT = new Color(139, 69, 19); // Saddle brown
    private static final Color PASTEL_BUTTON = new Color(255, 99, 71); // Tomato
    private static final Color PASTEL_BUTTON_TEXT = new Color(255, 255, 240); // Ivory
    private static final Color PASTEL_TABLE_HEADER = new Color(255, 127, 80); // Coral
    private static final Color PASTEL_SELECTION = new Color(255, 218, 185); // Peach puff
    
    private JTextField orderNumberField;
    private JTextField menuTypeField;
    private JTextField menuIdField;
    private JTable ordersTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    
    public UpdateMenuOrder() {
        setTitle("Restaurant Menu Order Manager");
        setSize(850, 650); // Tamaño más grande para mejor visualización
        getContentPane().setBackground(PASTEL_BACKGROUND);
        setLayout(new BorderLayout(10, 10));
        
        initComponents();
        
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    private void initComponents() {
        // Create a header panel with the description
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PASTEL_HEADER);
        headerPanel.setPreferredSize(new Dimension(850, 80));
        headerPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Restaurant Menu Order Database");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(PASTEL_TEXT);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JLabel subtitleLabel = new JLabel("Update orders with new menu types and IDs");
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
        formPanel.setLayout(new GridLayout(4, 2, 10, 10));
        formPanel.setBackground(PASTEL_BACKGROUND);
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PASTEL_TEXT, 1),
            "Update Menu Order",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Serif", Font.BOLD, 14),
            PASTEL_TEXT
        ));
        
        JLabel orderNumberLabel = new JLabel("Order Number:");
        orderNumberLabel.setFont(new Font("Serif", Font.PLAIN, 14));
        orderNumberLabel.setForeground(PASTEL_TEXT);
        formPanel.add(orderNumberLabel);
        
        orderNumberField = new JTextField(20);
        orderNumberField.setFont(new Font("Serif", Font.PLAIN, 14));
        formPanel.add(orderNumberField);
        
        JLabel menuTypeLabel = new JLabel("New Menu Type:");
        menuTypeLabel.setFont(new Font("Serif", Font.PLAIN, 14));
        menuTypeLabel.setForeground(PASTEL_TEXT);
        formPanel.add(menuTypeLabel);
        
        menuTypeField = new JTextField(20);
        menuTypeField.setFont(new Font("Serif", Font.PLAIN, 14));
        formPanel.add(menuTypeField);
        
        JLabel menuIdLabel = new JLabel("New Menu ID:");
        menuIdLabel.setFont(new Font("Serif", Font.PLAIN, 14));
        menuIdLabel.setForeground(PASTEL_TEXT);
        formPanel.add(menuIdLabel);
        
        menuIdField = new JTextField(20);
        menuIdField.setFont(new Font("Serif", Font.PLAIN, 14));
        formPanel.add(menuIdField);
        
        JButton updateButton = createStyledButton("Update Order");
        updateButton.addActionListener(e -> updateMenuOrder());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(PASTEL_BACKGROUND);
        buttonPanel.add(updateButton);
        
        formPanel.add(new JLabel(""));
        formPanel.add(buttonPanel);
        
        // Create table panel
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBackground(PASTEL_BACKGROUND);
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PASTEL_TEXT, 1),
            "Current Orders",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Serif", Font.BOLD, 14),
            PASTEL_TEXT
        ));
        
        // Create table
        String[] columnNames = {"Order Number", "Menu Type", "Menu ID"};
        tableModel = new DefaultTableModel(columnNames, 0);
        ordersTable = new JTable(tableModel);
        ordersTable.setFillsViewportHeight(true);
        ordersTable.setBackground(Color.WHITE);
        ordersTable.setForeground(PASTEL_TEXT);
        ordersTable.setSelectionBackground(PASTEL_SELECTION);
        ordersTable.setFont(new Font("Serif", Font.PLAIN, 14));
        ordersTable.setRowHeight(25);
        ordersTable.setIntercellSpacing(new Dimension(5, 0));
        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Add selection listener to populate form fields when a row is selected
        ordersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && ordersTable.getSelectedRow() != -1) {
                int selectedRow = ordersTable.getSelectedRow();
                orderNumberField.setText(ordersTable.getValueAt(selectedRow, 0).toString());
                menuTypeField.setText(ordersTable.getValueAt(selectedRow, 1).toString());
                menuIdField.setText(ordersTable.getValueAt(selectedRow, 2).toString());
            }
        });
        
        // Set custom renderer for alternate row colors
        ordersTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (isSelected) {
                    c.setBackground(PASTEL_SELECTION);
                    c.setForeground(PASTEL_TEXT);
                } else {
                    c.setBackground(row % 2 == 0 ? new Color(255, 235, 215) : Color.WHITE); // Antique white alternating
                    c.setForeground(PASTEL_TEXT);
                }
                
                ((JLabel) c).setHorizontalAlignment(JLabel.CENTER);
                
                return c;
            }
        });
        
        // Table header styling
        JTableHeader header = ordersTable.getTableHeader();
        header.setBackground(PASTEL_TABLE_HEADER);
        header.setForeground(Color.red);
        header.setFont(new Font("Serif", Font.BOLD, 14));
        
        JScrollPane scrollPane = new JScrollPane(ordersTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(PASTEL_TEXT, 1));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Arrange form and table with split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, formPanel, tablePanel);
        splitPane.setDividerLocation(200); // Mejor ubicación del divisor
        splitPane.setBorder(null);
        splitPane.setBackground(PASTEL_BACKGROUND);
        
        mainPanel.add(splitPane, BorderLayout.CENTER);
        
        // Create button panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controlPanel.setBackground(PASTEL_BACKGROUND);
        controlPanel.setPreferredSize(new Dimension(850, 60));
        
        // Status label
        statusLabel = new JLabel("Ready to connect", JLabel.CENTER);
        statusLabel.setForeground(PASTEL_TEXT);
        statusLabel.setFont(new Font("Serif", Font.ITALIC, 14));
        statusLabel.setPreferredSize(new Dimension(200, 30));
        controlPanel.add(statusLabel);
        
        JButton connectButton = createStyledButton("Connect");
        connectButton.addActionListener(e -> loadOrdersData());
        controlPanel.add(connectButton);
        
        JButton refreshButton = createStyledButton("Refresh");
        refreshButton.addActionListener(e -> loadOrdersData());
        controlPanel.add(refreshButton);
        
        JButton clearButton = createStyledButton("Clear");
        clearButton.addActionListener(e -> clearForm());
        controlPanel.add(clearButton);
        
        // Se ha eliminado el botón "Exit" como solicitado
        
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Load data on startup
        SwingUtilities.invokeLater(() -> loadOrdersData());
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(new Color(255, 69, 0)); // Pressed color - Orange Red
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 140, 0)); // Hover color - Dark Orange
                } else {
                    g2.setColor(PASTEL_BUTTON); // Default color
                }
                
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                g2.setColor(new Color(178, 34, 34, 80)); // Border - Firebrick with alpha
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
                return new Dimension(130, 40); // Botones un poco más anchos
            }
        };
        
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private void loadOrdersData() {
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
            String sql = "SELECT " + COL_NUMORD + ", " + COL_MENU_TYPE + ", " + COL_MENU_ID + 
                         " FROM " + TABLE_NAME + " ORDER BY " + COL_NUMORD;
            rs = stmt.executeQuery(sql);
            
            // Process results
            while (rs.next()) {
                int numOrd = rs.getInt(COL_NUMORD);
                int menuType = rs.getInt(COL_MENU_TYPE);
                int menuId = rs.getInt(COL_MENU_ID);
                
                // Add row to table model
                tableModel.addRow(new Object[]{numOrd, menuType, menuId});
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
    
    private void updateMenuOrder() {
        // Validate input
        if (orderNumberField.getText().trim().isEmpty() || 
            menuTypeField.getText().trim().isEmpty() || 
            menuIdField.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, 
                "Please enter Order Number, Menu Type, and Menu ID", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Parse input values
        int orderNumber, menuType, menuId;
        
        try {
            orderNumber = Integer.parseInt(orderNumberField.getText().trim());
            menuType = Integer.parseInt(menuTypeField.getText().trim());
            menuId = Integer.parseInt(menuIdField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Please enter valid numeric values for all fields", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            statusLabel.setText("Updating menu order...");
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            
            // Disable auto-commit
            conn.setAutoCommit(false);
            
            // Update the order with new menu information
            pstmt = conn.prepareStatement("UPDATE menu_order SET menu_mtype = ?, menu_id = ? WHERE numord = ?");
            
            pstmt.setInt(1, menuType);
            pstmt.setInt(2, menuId);
            pstmt.setInt(3, orderNumber);
            
            pstmt.executeUpdate();
            
            // Commit changes
            conn.commit();
            
           
            JOptionPane.showMessageDialog(this, 
                "Order updated successfully", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
                    
          //Refresh data in the table
            loadOrdersData();
            
        } catch (SQLException e) {
            try {
                // Rollback the transaction
                conn.rollback();
                
                JOptionPane.showMessageDialog(this, 
                    "Error updating order: " +
                    "It is likely that the introduced  menu type or id is not in the list of menus", 
                    "Database Error", 
                    JOptionPane.ERROR_MESSAGE);
                    
                statusLabel.setText("Error - Transaction rolled back");
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
    
    private void clearForm() {
        orderNumberField.setText("");
        menuTypeField.setText("");
        menuIdField.setText("");
        ordersTable.clearSelection();
    }
    
    public static void main(String[] args) {
        try {
            // Set look and feel to system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            UpdateMenuOrder app = new UpdateMenuOrder();
            app.setVisible(true);
        });
    }
}