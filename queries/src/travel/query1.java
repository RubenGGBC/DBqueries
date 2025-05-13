package travel;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.text.NumberFormat;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.util.Locale;

public class query1 extends JFrame {
    private JPanel contentPane;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JTextField txtStatus;
    private JButton btnConnect;
    private JButton btnRefresh;
    private JButton btnShowSQL;
    private JButton btnExport;
    
    private static final String DB_URL = "jdbc:mysql://dif-mysql.ehu.es:23306/DBI08";
    private static final String USER = "DBI08";
    private static final String PASS = "DBI08";
    
    // The SQL query
    private static final String HOTEL_OCCUPANCY_QUERY =
    		"SELECT " +
    				" ht.TripTo, " +
    				" ht.DepartureDate, " +
    				" h.HotelId, " +
    				" h.hotelname, " +
    				" h.hotelcity, " +
    				" h.hotelcapacity AS TotalCapacity, " +
    				" COUNT(DISTINCT htc.CustomerId) AS AssignedCustomers, " +
    				" h.hotelcapacity - COUNT(DISTINCT htc.CustomerId) AS AvailableCapacity, " +
    				" ROUND(COUNT(DISTINCT htc.CustomerId) / h.hotelcapacity * 100, 2) AS OccupancyRate, " +
    				" SUM(htc.NumNights) AS TotalNights, " +
    				" ROUND(AVG(htc.NumNights), 2) AS AvgStayLength, " +
    				" MAX(t.Numdays) AS TripDuration, " +
    				" CASE " +
    				" WHEN COUNT(DISTINCT htc.CustomerId) > h.hotelcapacity THEN 'OVERBOOKING' " +
    				" WHEN COUNT(DISTINCT htc.CustomerId) = h.hotelcapacity THEN 'FULL' " +
    				" WHEN COUNT(DISTINCT htc.CustomerId) >= h.hotelcapacity * 0.8 THEN 'OPTIMAL' " +
    				" WHEN COUNT(DISTINCT htc.CustomerId) >= h.hotelcapacity * 0.5 THEN 'ACCEPTABLE' " +
    				" ELSE 'UNDERUSED' " +
    				" END AS HotelUtilization " +
    				"FROM " +
    				" hotel_trip ht " +
    				" JOIN hotel h ON ht.HotelId = h.HotelId " +
    				" JOIN hotel_trip_customer htc ON ht.TripTo = htc.TripTo " +
    				" AND ht.DepartureDate = htc.DepartureDate " +
    				" AND ht.HotelId = htc.HotelId " +
    				" JOIN trip t ON ht.TripTo = t.TripTo AND ht.DepartureDate = t.DepartureDate " +
    				"GROUP BY " +
    				" ht.TripTo, ht.DepartureDate, h.HotelId, h.hotelname, h.hotelcity, h.hotelcapacity, t.Numdays " +
    				"ORDER BY " +
    				" OccupancyRate DESC, TripTo, DepartureDate";

    // Blue and black color theme for Travel package
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
            // Set look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    query1 frame = new query1();
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
    public query1() {
        setTitle("Hotel Occupancy Analysis");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 1300, 700);
        setLocationRelativeTo(null);
        
        // Create gradient panel
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
        contentPane.setLayout(new BorderLayout(0, 0));
        
        // Header panel
        JPanel panelHeader = new JPanel();
        panelHeader.setOpaque(false);
        panelHeader.setPreferredSize(new Dimension(1300, 100));
        contentPane.add(panelHeader, BorderLayout.NORTH);
        panelHeader.setLayout(null);
        
        JLabel lblTitle = new JLabel("Hotel Occupancy Dashboard");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setBounds(10, 11, 1264, 42);
        panelHeader.add(lblTitle);
        
        JLabel lblSubtitle = new JLabel("Detailed analysis of hotel utilization rates and capacity metrics");
        lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblSubtitle.setForeground(new Color(200, 230, 255));
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtitle.setBounds(10, 50, 1264, 30);
        panelHeader.add(lblSubtitle);
        
        // Statement panel
        JPanel statementPanel = new JPanel();
        statementPanel.setBorder(BorderFactory.createLineBorder(LIGHT_BLUE, 1));
        statementPanel.setBackground(new Color(25, 55, 85));
        statementPanel.setPreferredSize(new Dimension(1300, 60));
        contentPane.add(statementPanel, BorderLayout.NORTH);
        statementPanel.setLayout(new BorderLayout());
        
        JLabel statementLabel = new JLabel(" Query Description:");
        statementLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statementLabel.setForeground(Color.WHITE);
        statementPanel.add(statementLabel, BorderLayout.WEST);
        
        JTextArea statementText = new JTextArea(
            "This query analyzes hotel occupancy rates, showing capacity metrics, customer allocation, and hotel utilization categories."
        );
        statementText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statementText.setForeground(Color.WHITE);
        statementText.setBackground(new Color(25, 55, 85));
        statementText.setWrapStyleWord(true);
        statementText.setLineWrap(true);
        statementText.setEditable(false);
        statementText.setMargin(new Insets(5, 20, 5, 10));
        statementPanel.add(statementText, BorderLayout.CENTER);
        
        add(statementPanel, BorderLayout.NORTH);
        
        // Table panel
        JPanel panelTable = new JPanel();
        panelTable.setOpaque(false);
        contentPane.add(panelTable, BorderLayout.CENTER);
        panelTable.setLayout(new BorderLayout(0, 0));
        
        // Set up table model
        String[] columnNames = {"Trip To", "Departure Date", "Hotel ID", "Hotel Name", "City", 
                "Capacity", "Assigned", "Available", "Occupancy %", "Total Nights", 
                "Avg Stay", "Trip Duration", "Utilization"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) return Integer.class;
                if (columnIndex == 5) return Integer.class;
                if (columnIndex == 6) return Integer.class;
                if (columnIndex == 7) return Integer.class;
                if (columnIndex == 8) return Double.class;
                if (columnIndex == 9) return Integer.class;
                if (columnIndex == 10) return Double.class;
                if (columnIndex == 11) return Integer.class;
                return String.class;
            }
        };
        
        resultTable = new JTable(tableModel);
        resultTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resultTable.setRowHeight(25);
        resultTable.setIntercellSpacing(new Dimension(10, 5));
        resultTable.setFillsViewportHeight(true);
        resultTable.setShowVerticalLines(false);
        resultTable.setGridColor(new Color(230, 240, 250));
        resultTable.setSelectionBackground(LIGHT_BLUE);
        resultTable.setSelectionForeground(Color.WHITE);
        
        // Percent renderer for occupancy rate
        DefaultTableCellRenderer percentRenderer = new DefaultTableCellRenderer() {
            private final NumberFormat percentFormat = NumberFormat.getPercentInstance();
            
            {
                percentFormat.setMaximumFractionDigits(2);
            }
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Number) {
                    value = percentFormat.format(((Number)value).doubleValue() / 100);
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };
        percentRenderer.setHorizontalAlignment(JLabel.RIGHT);
        resultTable.getColumnModel().getColumn(8).setCellRenderer(percentRenderer);
        
        // Custom renderer for utilization status
        DefaultTableCellRenderer utilizationRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (value != null) {
                    String status = value.toString();
                    if (status.equals("OVERBOOKING")) {
                        c.setForeground(new Color(255, 50, 50));
                        setFont(new Font("Segoe UI", Font.BOLD, 14));
                    } else if (status.equals("FULL")) {
                        c.setForeground(new Color(0, 200, 0));
                        setFont(new Font("Segoe UI", Font.BOLD, 14));
                    } else if (status.equals("OPTIMAL")) {
                        c.setForeground(new Color(30, 180, 255));
                        setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    } else if (status.equals("ACCEPTABLE")) {
                        c.setForeground(new Color(255, 180, 30));
                        setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    } else {
                        c.setForeground(new Color(200, 200, 200));
                        setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    }
                }
                
                if (isSelected) {
                    c.setForeground(Color.WHITE);
                }
                
                return c;
            }
        };
        utilizationRenderer.setHorizontalAlignment(JLabel.CENTER);
        resultTable.getColumnModel().getColumn(12).setCellRenderer(utilizationRenderer);
        
        // Default cell renderer for consistent alternating rows
        resultTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(table.getSelectionForeground());
                } else {
                    c.setBackground(row % 2 == 0 ? VERY_LIGHT_BLUE : Color.WHITE);
                    c.setForeground(DARK_BLUE);
                }
                
                // Center align for most columns
                if (column != 3 && column != 4 && column != 12) { // Skip name, city, and utilization
                    ((JLabel) c).setHorizontalAlignment(JLabel.CENTER);
                }
                
                return c;
            }
        });
        
        // Table header styling
        JTableHeader header = resultTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(MEDIUM_BLUE);
        header.setForeground(Color.blue);
        header.setPreferredSize(new Dimension(100, 35));
        
        // Create scroll pane with styled scrollbars
        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Custom scrollbar styling
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = LIGHT_BLUE;
                this.trackColor = DARK_BLUE;
            }
            
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });
        
        panelTable.add(scrollPane, BorderLayout.CENTER);
        
        // Footer panel with legend and controls
        JPanel panelFooter = new JPanel();
        panelFooter.setOpaque(false);
        panelFooter.setPreferredSize(new Dimension(1300, 100));
        contentPane.add(panelFooter, BorderLayout.SOUTH);
        panelFooter.setLayout(new BorderLayout());
        
        // Legend panel
        JPanel panelLegend = new JPanel();
        panelLegend.setOpaque(false);
        panelLegend.setPreferredSize(new Dimension(1300, 40));
        panelFooter.add(panelLegend, BorderLayout.NORTH);
        panelLegend.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        
        JLabel lblLegend = new JLabel("Legend: ");
        lblLegend.setForeground(Color.WHITE);
        lblLegend.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelLegend.add(lblLegend);
        
        addLegendItem(panelLegend, "OVERBOOKING", new Color(255, 50, 50));
        addLegendItem(panelLegend, "FULL", new Color(0, 200, 0));
        addLegendItem(panelLegend, "OPTIMAL", new Color(30, 180, 255));
        addLegendItem(panelLegend, "ACCEPTABLE", new Color(255, 180, 30));
        addLegendItem(panelLegend, "UNDERUSED", new Color(200, 200, 200));
        
        // Controls panel
        JPanel panelControls = new JPanel();
        panelControls.setOpaque(false);
        panelFooter.add(panelControls, BorderLayout.SOUTH);
        panelControls.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        txtStatus = new JTextField("Not connected to database");
        txtStatus.setEditable(false);
        txtStatus.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtStatus.setBorder(null);
        txtStatus.setOpaque(false);
        txtStatus.setForeground(new Color(255, 200, 200));
        txtStatus.setPreferredSize(new Dimension(250, 25));
        panelControls.add(txtStatus);
        
        btnConnect = createStyledButton("Connect");
        btnConnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fetchData();
            }
        });
        panelControls.add(btnConnect);
        
        btnRefresh = createStyledButton("Refresh");
        btnRefresh.setEnabled(false);
        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fetchData();
            }
        });
        panelControls.add(btnRefresh);
        
        btnShowSQL = createStyledButton("Show SQL");
        btnShowSQL.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showSQLStatement();
            }
        });
        panelControls.add(btnShowSQL);
        
        btnExport = createStyledButton("Export Data");
        btnExport.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exportData();
            }
        });
        panelControls.add(btnExport);
        
        JButton btnExit = createStyledButton("Exit");
        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        panelControls.add(btnExit);
    }
    
    /**
     * Add a legend item with color indicator
     */
    private void addLegendItem(JPanel panel, String text, Color color) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(label);
    }
    
    /**
     * Create a styled button with hover effects
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
                
                if (!isEnabled()) {
                    g2.setColor(new Color(100, 100, 100));
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                g2.setColor(new Color(255, 255, 255, 50));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int x = (getWidth() - textWidth) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                
                g2.setColor(isEnabled() ? Color.WHITE : new Color(200, 200, 200));
                g2.drawString(text, x, y);
                g2.dispose();
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(130, 35);
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
     * Show SQL Statement in a dialog
     */
    private void showSQLStatement() {
        JTextArea textArea = new JTextArea(HOTEL_OCCUPANCY_QUERY);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setBackground(VERY_LIGHT_BLUE);
        textArea.setForeground(DARK_BLUE);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(800, 500));
        
        JOptionPane.showMessageDialog(this, scrollPane, "SQL Statement", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Export data to a file (placeholder)
     */
    private void exportData() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, 
                "No data to export. Please execute the query first.", 
                "Export Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // This is a placeholder for export functionality
        JOptionPane.showMessageDialog(this, 
            "Data would be exported to CSV/Excel here.", 
            "Export Data", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Connect to database and fetch data
     */
    private void fetchData() {
        // Clear existing data
        tableModel.setRowCount(0);
        txtStatus.setText("Connecting to database...");
        txtStatus.setForeground(new Color(255, 255, 200));
        
        // Use SwingWorker to perform database operations in background
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private boolean success = false;
            
            @Override
            protected Void doInBackground() throws Exception {
                Connection conn = null;
                Statement stmt = null;
                ResultSet rs = null;
                
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    conn = DriverManager.getConnection(DB_URL, USER, PASS);
                    stmt = conn.createStatement();
                    rs = stmt.executeQuery(HOTEL_OCCUPANCY_QUERY);
                    
                    while (rs.next()) {
                        String tripTo = rs.getString("TripTo");
                        String departureDate = rs.getString("DepartureDate");
                        int hotelId = rs.getInt("HotelId");
                        String hotelName = rs.getString("hotelname");
                        String hotelCity = rs.getString("hotelcity");
                        int totalCapacity = rs.getInt("TotalCapacity");
                        int assignedCustomers = rs.getInt("AssignedCustomers");
                        int availableCapacity = rs.getInt("AvailableCapacity");
                        double occupancyRate = rs.getDouble("OccupancyRate");
                        int totalNights = rs.getInt("TotalNights");
                        double avgStayLength = rs.getDouble("AvgStayLength");
                        int tripDuration = rs.getInt("TripDuration");
                        String hotelUtilization = rs.getString("HotelUtilization");
                        
                        // Add row to table model
                        tableModel.addRow(new Object[] {
                            tripTo, departureDate, hotelId, hotelName, hotelCity,
                            totalCapacity, assignedCustomers, availableCapacity,
                            occupancyRate, totalNights, avgStayLength, tripDuration,
                            hotelUtilization
                        });
                    }
                    
                    success = true;
                } catch (SQLException e) {
                    e.printStackTrace();
                    success = false;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    success = false;
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
                
                return null;
            }
            
            @Override
            protected void done() {
                if (success) {
                    txtStatus.setText("Connected: Found " + tableModel.getRowCount() + " records");
                    txtStatus.setForeground(new Color(200, 255, 200));
                    btnRefresh.setEnabled(true);
                    btnConnect.setText("Reconnect");
                    btnExport.setEnabled(true);
                } else {
                    txtStatus.setText("Connection failed. Check console for details.");
                    txtStatus.setForeground(new Color(255, 150, 150));
                    btnExport.setEnabled(false);
                }
            }
        };
        
        worker.execute();
    }
}