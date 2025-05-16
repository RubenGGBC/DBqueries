package travel;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.text.NumberFormat;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.util.Locale;

public class Query3 extends JFrame {
    private JPanel contentPane;
    private JTable tableResults;
    private DefaultTableModel tableModel;
    private JTextField txtStatus;
    private JButton btnConnect;
    private JButton btnRefresh;
    private JButton btnShowSQL;
    private JButton btnExport;
    
    private static final String DB_URL = "jdbc:mysql://dif-mysql.ehu.es:23306/DBI08";
    private static final String USER = "DBI08";
    private static final String PASS = "DBI08";
    
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
    "    ) AS TotalRevenue " +
    "FROM " +
    "    tourguide tg " +
    "    JOIN trip t ON tg.GuideId = t.GuideId " +
    "    JOIN hotel_trip_customer htc ON t.TripTo = htc.TripTo AND t.DepartureDate = htc.DepartureDate " +
    "WHERE EXISTS ( " +
    "    SELECT * " +
    "    FROM languages l " +
    "    WHERE l.GuideId = tg.GuideId " +
    ") " +
    "GROUP BY " +
    "    tg.GuideId, tg.guidename " +
    "ORDER BY " +
    "    TotalRevenue DESC";

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
                    Query3 frame = new Query3();
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
    public Query3() {
        setTitle("Tour Guide Performance Analysis");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 1200, 700);
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
        panelHeader.setPreferredSize(new Dimension(1200, 100));
        contentPane.add(panelHeader, BorderLayout.NORTH);
        panelHeader.setLayout(null);
        
        JLabel lblTitle = new JLabel("Tour Guide Performance Dashboard");
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
        
        // Statement panel
        JPanel statementPanel = new JPanel();
        statementPanel.setBorder(BorderFactory.createLineBorder(LIGHT_BLUE, 1));
        statementPanel.setBackground(new Color(25, 55, 85));
        statementPanel.setPreferredSize(new Dimension(1200, 60));
        contentPane.add(statementPanel, BorderLayout.NORTH);
        statementPanel.setLayout(new BorderLayout());
        
        JLabel statementLabel = new JLabel(" Query Description:");
        statementLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statementLabel.setForeground(Color.WHITE);
        statementPanel.add(statementLabel, BorderLayout.WEST);
        
        JTextArea statementText = new JTextArea(
            "This query analyzes tour guide performance metrics including total trips led, customers served, and revenue generated by tour guides who speak at least one language."
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
        
        // Table model setup
        String[] columnNames = {"Guide ID", "Guide Name", "Total Trips", "Total Customers", "Total Revenue"};
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
        tableResults.setGridColor(new Color(230, 240, 250));
        tableResults.setSelectionBackground(LIGHT_BLUE);
        tableResults.setSelectionForeground(Color.blue.darker());
        
        // Format revenue column as currency
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer() {
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
        
        // Table default renderer
        tableResults.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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
                
                return c;
            }
        });
        
        // Table header styling
        JTableHeader header = tableResults.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(MEDIUM_BLUE);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(100, 35));
        
        // Set column widths
        tableResults.getColumnModel().getColumn(0).setPreferredWidth(80);
        tableResults.getColumnModel().getColumn(1).setPreferredWidth(250);
        tableResults.getColumnModel().getColumn(2).setPreferredWidth(100);
        tableResults.getColumnModel().getColumn(3).setPreferredWidth(150);
        tableResults.getColumnModel().getColumn(4).setPreferredWidth(200);
        
        // Custom scroll pane with styled scrollbars
        JScrollPane scrollPane = new JScrollPane(tableResults);
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
        
        // Footer panel
        JPanel panelFooter = new JPanel();
        panelFooter.setOpaque(false);
        panelFooter.setPreferredSize(new Dimension(1200, 60));
        contentPane.add(panelFooter, BorderLayout.SOUTH);
        panelFooter.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 15));
        
        txtStatus = new JTextField("Not connected to database");
        txtStatus.setEditable(false);
        txtStatus.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtStatus.setBorder(null);
        txtStatus.setOpaque(false);
        txtStatus.setForeground(new Color(255, 200, 200));
        txtStatus.setPreferredSize(new Dimension(300, 25));
        panelFooter.add(txtStatus);
        
        btnConnect = createStyledButton("Connect to Database");
        btnConnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fetchData();
            }
        });
        panelFooter.add(btnConnect);
        
        btnRefresh = createStyledButton("Refresh Data");
        btnRefresh.setEnabled(false);
        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fetchData();
            }
        });
        panelFooter.add(btnRefresh);
        
        btnShowSQL = createStyledButton("Show SQL");
        btnShowSQL.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showSQLStatement();
            }
        });
        panelFooter.add(btnShowSQL);
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
                return new Dimension(150, 35);
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
        JTextArea textArea = new JTextArea(TOUR_GUIDE_QUERY);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setBackground(new Color(240, 248, 255));
        textArea.setForeground(DARK_BLUE);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, "SQL Statement", JOptionPane.INFORMATION_MESSAGE);
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
                        
                        // Add row to table model - no Languages column anymore
                        tableModel.addRow(new Object[] {
                            guideId, guideName, totalTrips, totalCustomers, totalRevenue
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