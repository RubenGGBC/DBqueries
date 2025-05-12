package travel;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.util.Locale;

public class query1 extends JFrame {
    private JPanel contentPane;
    private JTable tableResults;
    private DefaultTableModel tableModel;
    private JTextField txtStatus;
    private JButton btnConnect;
    private JButton btnRefresh;
    
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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1300, 700);
        setLocationRelativeTo(null);
        
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
        
        JPanel panelTable = new JPanel();
        panelTable.setOpaque(false);
        contentPane.add(panelTable, BorderLayout.CENTER);
        panelTable.setLayout(new BorderLayout(0, 0));
        
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
        
        tableResults = new JTable(tableModel);
        tableResults.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableResults.setRowHeight(25);
        tableResults.setIntercellSpacing(new Dimension(10, 5));
        tableResults.setFillsViewportHeight(true);
        tableResults.setShowVerticalLines(false);
        tableResults.setGridColor(new Color(230, 240, 250));
        tableResults.setSelectionBackground(new Color(70, 130, 180));
        tableResults.setSelectionForeground(Color.BLACK);
        tableResults.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
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
        percentRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tableResults.getColumnModel().getColumn(8).setCellRenderer(percentRenderer);
        
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
        utilizationRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tableResults.getColumnModel().getColumn(12).setCellRenderer(utilizationRenderer);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tableResults.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        tableResults.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        tableResults.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        tableResults.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);
        tableResults.getColumnModel().getColumn(9).setCellRenderer(centerRenderer);
        tableResults.getColumnModel().getColumn(10).setCellRenderer(centerRenderer);
        tableResults.getColumnModel().getColumn(11).setCellRenderer(centerRenderer);
        
        JTableHeader header = tableResults.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(25, 84, 123));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(100, 35));
        
        // Set column widths
        tableResults.getColumnModel().getColumn(0).setPreferredWidth(100);
        tableResults.getColumnModel().getColumn(1).setPreferredWidth(120);
        tableResults.getColumnModel().getColumn(2).setPreferredWidth(80);
        tableResults.getColumnModel().getColumn(3).setPreferredWidth(200);
        tableResults.getColumnModel().getColumn(4).setPreferredWidth(120);
        tableResults.getColumnModel().getColumn(5).setPreferredWidth(80);
        tableResults.getColumnModel().getColumn(6).setPreferredWidth(80);
        tableResults.getColumnModel().getColumn(7).setPreferredWidth(80);
        tableResults.getColumnModel().getColumn(8).setPreferredWidth(100);
        tableResults.getColumnModel().getColumn(9).setPreferredWidth(100);
        tableResults.getColumnModel().getColumn(10).setPreferredWidth(80);
        tableResults.getColumnModel().getColumn(11).setPreferredWidth(100);
        tableResults.getColumnModel().getColumn(12).setPreferredWidth(120);
        
        JScrollPane scrollPane = new JScrollPane(tableResults);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(70, 130, 180);
                this.trackColor = new Color(25, 84, 123);
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
        
        JPanel panelFooter = new JPanel();
        panelFooter.setOpaque(false);
        panelFooter.setPreferredSize(new Dimension(1300, 60));
        contentPane.add(panelFooter, BorderLayout.SOUTH);
        panelFooter.setLayout(new BorderLayout(0, 0));
        
        JPanel panelControls = new JPanel();
        panelControls.setOpaque(false);
        panelFooter.add(panelControls, BorderLayout.EAST);
        panelControls.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        
        btnConnect = createStyledButton("Connect to Database");
        btnConnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fetchData();
            }
        });
        panelControls.add(btnConnect);
        
        btnRefresh = createStyledButton("Refresh Data");
        btnRefresh.setEnabled(false);
        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fetchData();
            }
        });
        panelControls.add(btnRefresh);
        
        JPanel panelStatus = new JPanel();
        panelStatus.setOpaque(false);
        panelFooter.add(panelStatus, BorderLayout.WEST);
        panelStatus.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        JLabel lblStatusText = new JLabel("Status:");
        lblStatusText.setForeground(Color.WHITE);
        lblStatusText.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelStatus.add(lblStatusText);
        
        txtStatus = new JTextField();
        txtStatus.setText("Not connected to database");
        txtStatus.setEditable(false);
        txtStatus.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtStatus.setBorder(null);
        txtStatus.setOpaque(false);
        txtStatus.setForeground(new Color(255, 200, 200));
        txtStatus.setPreferredSize(new Dimension(300, 25));
        panelStatus.add(txtStatus);
        
        JPanel panelSummary = new JPanel();
        panelSummary.setOpaque(false);
        panelFooter.add(panelSummary, BorderLayout.CENTER);
        panelSummary.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JLabel lblLegend = new JLabel("Legend: ");
        lblLegend.setForeground(Color.WHITE);
        lblLegend.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelSummary.add(lblLegend);
        
        addLegendItem(panelSummary, "OVERBOOKING", new Color(255, 50, 50));
        addLegendItem(panelSummary, "FULL", new Color(0, 200, 0));
        addLegendItem(panelSummary, "OPTIMAL", new Color(30, 180, 255));
        addLegendItem(panelSummary, "ACCEPTABLE", new Color(255, 180, 30));
        addLegendItem(panelSummary, "UNDERUSED", new Color(200, 200, 200));
    }
    
 
    private void addLegendItem(JPanel panel, String text, Color color) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(label);
    }
    
    
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
        button.setPreferredSize(new Dimension(180, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
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
                } else {
                    txtStatus.setText("Connection failed. Check console for details.");
                    txtStatus.setForeground(new Color(255, 150, 150));
                }
            }
        };
        
        worker.execute();
    }
}