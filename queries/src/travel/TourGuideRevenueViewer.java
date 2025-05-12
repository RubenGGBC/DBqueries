package travel;

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

public class TourGuideRevenueViewer extends JFrame {
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
    private static final String TOUR_GUIDE_QUERY = 
        "SELECT " +
        "    tg.GuideId, " +
        "    tg.guidename, " +
        "    COUNT(DISTINCT t.TripTo, t.DepartureDate) AS TotalTrips, " +
        "    COUNT(DISTINCT htc.CustomerId) AS TotalCustomers, " +
        "    SUM(t.Numdays * t.Ppday * " +
        "        (SELECT COUNT(DISTINCT htc2.CustomerId) " +
        "         FROM hotel_trip_customer htc2 " +
        "         WHERE htc2.TripTo = t.TripTo AND htc2.DepartureDate = t.DepartureDate) " +
        "    ) AS TotalRevenue, " +
        "    GROUP_CONCAT(DISTINCT l.Lang ORDER BY l.Lang) AS Languages " +
        "FROM " +
        "    tourguide tg " +
        "    JOIN trip t ON tg.GuideId = t.GuideId " +
        "    JOIN hotel_trip_customer htc ON t.TripTo = htc.TripTo AND t.DepartureDate = htc.DepartureDate " +
        "    LEFT JOIN languages l ON tg.GuideId = l.GuideId " +
        "GROUP BY " +
        "    tg.GuideId, tg.guidename " +
        "ORDER BY " +
        "    TotalRevenue DESC";

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
                    TourGuideRevenueViewer frame = new TourGuideRevenueViewer();
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
    public TourGuideRevenueViewer() {
        setTitle("Tour Guide Revenue Analysis");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1200, 700);
        setLocationRelativeTo(null);
        
        // Custom main panel with gradient background
        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(39, 60, 117), 
                                                 getWidth(), getHeight(), new Color(25, 42, 86));
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
        panelHeader.setPreferredSize(new Dimension(1200, 100));
        contentPane.add(panelHeader, BorderLayout.NORTH);
        panelHeader.setLayout(null);
        
        JLabel lblTitle = new JLabel("Tour Guide Revenue Dashboard");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setBounds(10, 11, 1164, 42);
        panelHeader.add(lblTitle);
        
        JLabel lblSubtitle = new JLabel("Comprehensive view of tour guide performance metrics");
        lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblSubtitle.setForeground(new Color(200, 200, 255));
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtitle.setBounds(10, 50, 1164, 30);
        panelHeader.add(lblSubtitle);
        
        // Table panel
        JPanel panelTable = new JPanel();
        panelTable.setOpaque(false);
        contentPane.add(panelTable, BorderLayout.CENTER);
        panelTable.setLayout(new BorderLayout(0, 0));
        
        // Table model setup
        String[] columnNames = {"Guide ID", "Guide Name", "Total Trips", "Total Customers", "Total Revenue", "Languages"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class;
                if (columnIndex == 2) return Integer.class;
                if (columnIndex == 3) return Integer.class;
                if (columnIndex == 4) return Double.class;
                return String.class;
            }
        };
        
        tableResults = new JTable(tableModel);
        tableResults.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableResults.setRowHeight(25);
        tableResults.setIntercellSpacing(new Dimension(10, 5));
        tableResults.setFillsViewportHeight(true);
        tableResults.setShowVerticalLines(false);
        tableResults.setGridColor(new Color(230, 230, 250));
        tableResults.setSelectionBackground(new Color(100, 149, 237));
        tableResults.setSelectionForeground(Color.BLACK);
        tableResults.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // Format revenue column as currency
        DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer() {
            private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Number) {
                    value = currencyFormat.format(((Number)value).doubleValue());
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };
        currencyRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tableResults.getColumnModel().getColumn(4).setCellRenderer(currencyRenderer);
        
        // Center align for numeric columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tableResults.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tableResults.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        tableResults.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        
        // Table header styling
        JTableHeader header = tableResults.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(39, 60, 117));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(100, 35));
        
        // Set column widths
        tableResults.getColumnModel().getColumn(0).setPreferredWidth(80);
        tableResults.getColumnModel().getColumn(1).setPreferredWidth(250);
        tableResults.getColumnModel().getColumn(2).setPreferredWidth(100);
        tableResults.getColumnModel().getColumn(3).setPreferredWidth(150);
        tableResults.getColumnModel().getColumn(4).setPreferredWidth(200);
        tableResults.getColumnModel().getColumn(5).setPreferredWidth(300);
        
        // Custom scroll pane with styled scrollbars
        JScrollPane scrollPane = new JScrollPane(tableResults);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Custom scrollbar styling
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(100, 149, 237);
                this.trackColor = new Color(39, 60, 117);
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
        
        // Footer panel
        JPanel panelFooter = new JPanel();
        panelFooter.setOpaque(false);
        panelFooter.setPreferredSize(new Dimension(1200, 60));
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
    }
    
    /**
     * Create a styled button with hover effects
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(new Color(70, 130, 180));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(100, 149, 237));
                } else {
                    g2.setColor(new Color(65, 105, 225));
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
                    rs = stmt.executeQuery(TOUR_GUIDE_QUERY);
                    while (rs.next()) {
                        int guideId = rs.getInt("GuideId");
                        String guideName = rs.getString("guidename");
                        int totalTrips = rs.getInt("TotalTrips");
                        int totalCustomers = rs.getInt("TotalCustomers");
                        double totalRevenue = rs.getDouble("TotalRevenue");
                        String languages = rs.getString("Languages");
                        
                        // Add row to table model
                        tableModel.addRow(new Object[] {
                            guideId, guideName, totalTrips, totalCustomers, 
                            totalRevenue, languages
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
 