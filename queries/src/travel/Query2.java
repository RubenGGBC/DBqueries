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

public class Query2 extends JFrame {
    private JPanel contentPane;
    private JTable tableResults;
    private DefaultTableModel tableModel;
    private JTextField txtStatus;
    private JButton btnConnect;
    private JButton btnRefresh;
    private JButton btnShowSQL;
    private JButton btnExport;
    private JButton btnExit;
    
    private static final String DB_URL = "jdbc:mysql://dif-mysql.ehu.es:23306/DBI08";
    private static final String USER = "DBI08";
    private static final String PASS = "DBI08";
    
    private static final String EXCURSION_QUERY = 
    		"SELECT \n" +
    		"  oe.tripto, \n" +
    		"  oe.departuredate, \n" +
    		"  oe.codeexc, \n" +
    		"  oe.excursionto, \n" +
    		"  COUNT(DISTINCT eoc.customerid) AS ParticipatingCustomers, \n" +
    		"  (SELECT COUNT(DISTINCT customerid) \n" +
    		"   FROM hotel_trip_customer \n" +
    		"   WHERE tripto = oe.tripto \n" +
    		"   AND departuredate = oe.departuredate) AS TotalTripCustomers, \n" +
    		"  oe.price AS ExcursionPrice, \n" +
    		"  COUNT(DISTINCT eoc.customerid) * oe.price AS TotalRevenue \n" +
    		"FROM optional_excursion AS oe \n" +
    		"INNER JOIN excur_opt_customer AS eoc ON oe.tripto = eoc.tripto \n" +
    		"  AND oe.departuredate = eoc.departuredate \n" +
    		"  AND oe.codeexc = eoc.codeexc \n" +
    		"WHERE EXISTS (\n" +
    		"  SELECT * \n" +
    		"  FROM trip AS t \n" +
    		"  WHERE t.tripto = oe.tripto \n" +
    		"  AND t.departuredate = oe.departuredate \n" +
    		") \n" +
    		"GROUP BY oe.tripto, oe.departuredate, oe.codeexc, oe.excursionto, oe.price \n" +
    		"ORDER BY ParticipatingCustomers DESC;";

    // Blue and black color theme for Travel package - from TourGuideRevenueViewer
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
                    Query2 frame = new Query2();
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
    public Query2() {
        setTitle("Excursion Participation Analysis");
        setBounds(100, 100, 1400, 700);
        setLocationRelativeTo(null);
        
        // Create gradient panel like in TourGuideRevenueViewer
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
        
        // Header panel like in TourGuideRevenueViewer
        JPanel panelHeader = new JPanel();
        panelHeader.setOpaque(false);
        panelHeader.setPreferredSize(new Dimension(1400, 100));
        contentPane.add(panelHeader, BorderLayout.NORTH);
        panelHeader.setLayout(null);
        
        JLabel lblTitle = new JLabel("Excursion Participation Dashboard");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setBounds(10, 11, 1364, 42);
        panelHeader.add(lblTitle);
        
        JLabel lblSubtitle = new JLabel("Analysis of excursion participation rates, revenue metrics, and customer engagement");
        lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblSubtitle.setForeground(new Color(200, 230, 255));
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtitle.setBounds(10, 50, 1364, 30);
        panelHeader.add(lblSubtitle);
        
        // Statement panel like in query1
        JPanel statementPanel = new JPanel();
        statementPanel.setBorder(BorderFactory.createLineBorder(LIGHT_BLUE, 1));
        statementPanel.setBackground(new Color(25, 55, 85));
        statementPanel.setPreferredSize(new Dimension(1400, 75));
        contentPane.add(statementPanel, BorderLayout.NORTH);
        statementPanel.setLayout(new BorderLayout());
        
        JLabel statementLabel = new JLabel(" Query Description:");
        statementLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statementLabel.setForeground(Color.WHITE);
        statementPanel.add(statementLabel, BorderLayout.WEST);
        
        JTextArea statementText = new JTextArea(
            "For each trip, this query takes all excursions, and gives different metrics, "
            + "such as the participation rate (taken from the count of customers in the trip with respect to the customers in the excursion), "
            + "the revenue (calculated from the price times the amount of unique customers),  the revenue contribution percent "
            + "(with respect to the total revenue generated in the trip), and a label for the popularity of the excursion "
            + "(based on the participation rate: low, medium, high or very high):"
        );
        statementText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statementText.setForeground(Color.WHITE);
        statementText.setBackground(new Color(25, 55, 85));
        statementText.setWrapStyleWord(true);
        statementText.setLineWrap(true);
        statementText.setEditable(false);
        statementText.setMargin(new Insets(5, 20, 5, 10));
        statementPanel.add(statementText, BorderLayout.CENTER);
        
        // Table panel
        JPanel panelTable = new JPanel();
        panelTable.setOpaque(false);
        contentPane.add(panelTable, BorderLayout.CENTER);
        panelTable.setLayout(new BorderLayout(0, 0));
        
        String[] columnNames = {"Trip To", "Departure Date", "Exc. Code", "Excursion To", 
                "Participating", "Total Customers", "Participation %", "Price", 
                "Excursion Revenue", "Popularity"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4) return Integer.class;
                if (columnIndex == 5) return Integer.class;
                if (columnIndex == 6) return Double.class;
                if (columnIndex == 7) return Double.class;
                if (columnIndex == 8) return Double.class;
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
        tableResults.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // Currency formatter like in TourGuideRevenueViewer
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
        tableResults.getColumnModel().getColumn(7).setCellRenderer(currencyRenderer);
        tableResults.getColumnModel().getColumn(8).setCellRenderer(currencyRenderer);
        
        // Percentage formatter like in query1
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
        tableResults.getColumnModel().getColumn(6).setCellRenderer(percentRenderer);
        
        // Popularity column renderer with color coding
        DefaultTableCellRenderer popularityRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (value != null) {
                    String status = value.toString();
                    if (status.equals("VERY HIGH")) {
                        c.setForeground(new Color(0, 200, 0));
                        setFont(new Font("Segoe UI", Font.BOLD, 14));
                    } else if (status.equals("HIGH")) {
                        c.setForeground(new Color(30, 180, 255));
                        setFont(new Font("Segoe UI", Font.BOLD, 14));
                    } else if (status.equals("MEDIUM")) {
                        c.setForeground(new Color(255, 180, 30));
                        setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    } else if (status.equals("LOW")) {
                        c.setForeground(new Color(255, 150, 150));
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
        popularityRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tableResults.getColumnModel().getColumn(9).setCellRenderer(popularityRenderer);
        
        // Default cell renderer with alternating row colors like in TourGuideRevenueViewer
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tableResults.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        tableResults.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        tableResults.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        
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
        
        // Table header styling like in TourGuideRevenueViewer
        JTableHeader header = tableResults.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(MEDIUM_BLUE);
        header.setForeground(Color.blue.darker());
        header.setPreferredSize(new Dimension(100, 35));
        
        // Set column widths like in TourGuideRevenueViewer
        tableResults.getColumnModel().getColumn(0).setPreferredWidth(100);
        tableResults.getColumnModel().getColumn(1).setPreferredWidth(120);
        tableResults.getColumnModel().getColumn(2).setPreferredWidth(80);
        tableResults.getColumnModel().getColumn(3).setPreferredWidth(200);
        tableResults.getColumnModel().getColumn(4).setPreferredWidth(100);
        tableResults.getColumnModel().getColumn(5).setPreferredWidth(120);
        tableResults.getColumnModel().getColumn(6).setPreferredWidth(120);
        tableResults.getColumnModel().getColumn(7).setPreferredWidth(100);
        tableResults.getColumnModel().getColumn(8).setPreferredWidth(150);
        tableResults.getColumnModel().getColumn(9).setPreferredWidth(100);
        
        // Create scroll pane with styled scrollbars like in TourGuideRevenueViewer
        JScrollPane scrollPane = new JScrollPane(tableResults);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Custom scrollbar styling like in TourGuideRevenueViewer
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
        
        // Add legend panel like in query1
        JPanel legendPanel = new JPanel();
        legendPanel.setOpaque(false);
        legendPanel.setPreferredSize(new Dimension(1400, 40));
        panelTable.add(legendPanel, BorderLayout.SOUTH);
        legendPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        JLabel lblLegend = new JLabel("Popularity: ");
        lblLegend.setForeground(Color.WHITE);
        lblLegend.setFont(new Font("Segoe UI", Font.BOLD, 14));
        legendPanel.add(lblLegend);
        
        addLegendItem(legendPanel, "VERY HIGH (>75%)", new Color(0, 200, 0));
        addLegendItem(legendPanel, "HIGH (50-75%)", new Color(30, 180, 255));
        addLegendItem(legendPanel, "MEDIUM (25-50%)", new Color(255, 180, 30));
        addLegendItem(legendPanel, "LOW (<25%)", new Color(255, 150, 150));
        
        // Footer panel like in TourGuideRevenueViewer
        JPanel panelFooter = new JPanel();
        panelFooter.setOpaque(false);
        panelFooter.setPreferredSize(new Dimension(1400, 60));
        contentPane.add(panelFooter, BorderLayout.SOUTH);
        panelFooter.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 15));
        
        // Status text field like in query1
        txtStatus = new JTextField("Not connected to database");
        txtStatus.setEditable(false);
        txtStatus.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtStatus.setBorder(null);
        txtStatus.setOpaque(false);
        txtStatus.setForeground(new Color(255, 200, 200));
        txtStatus.setPreferredSize(new Dimension(300, 25));
        panelFooter.add(txtStatus);
        
        // Add connect button like in TourGuideRevenueViewer
        btnConnect = createStyledButton("Connect to Database");
        btnConnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fetchData();
            }
        });
        panelFooter.add(btnConnect);
        
        // Add refresh button like in query1
        btnRefresh = createStyledButton("Refresh Data");
        btnRefresh.setEnabled(false);
        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fetchData();
            }
        });
        panelFooter.add(btnRefresh);
        
        // Add Show SQL button from TourGuideRevenueViewer
        btnShowSQL = createStyledButton("Show SQL");
        btnShowSQL.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showSQLStatement();
            }
        });
        panelFooter.add(btnShowSQL);
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
     * Create a styled button like in TourGuideRevenueViewer
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
     * Show SQL Statement in dialog like in TourGuideRevenueViewer
     */
    private void showSQLStatement() {
        JTextArea textArea = new JTextArea("For each trip, this query takes all excursions, and gives different metrics, \n"
                + "such as the participation rate (taken from the count of customers in the trip with respect to the customers in the excursion), \n"
                + "the revenue (calculated from the price times the amount of unique customers),  the revenue contribution percent \n"
                + "(with respect to the total revenue generated in the trip), and a label for the popularity of the excursion \n"
                + "(based on the participation rate: low, medium, high or very high): \n\n\n"
                + EXCURSION_QUERY);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setBackground(new Color(240, 248, 255));
        textArea.setForeground(DARK_BLUE);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(800, 500));
        
        JOptionPane.showMessageDialog(this, scrollPane, "SQL Statement", JOptionPane.INFORMATION_MESSAGE);
    }
  
    
    /**
     * Connect to database and fetch data like in TourGuideRevenueViewer
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
                    rs = stmt.executeQuery(EXCURSION_QUERY);
                    
                    while (rs.next()) {
                        String tripTo = rs.getString("tripto");
                        String departureDate = rs.getString("departuredate");
                        String codeExc = rs.getString("codeexc");
                        String excursionTo = rs.getString("excursionto");
                        int participatingCustomers = rs.getInt("ParticipatingCustomers");
                        int totalTripCustomers = rs.getInt("TotalTripCustomers");
                        double participationRate = (double) participatingCustomers / totalTripCustomers * 100;
                        double excursionPrice = rs.getDouble("ExcursionPrice");
                        double totalRevenue = rs.getDouble("TotalRevenue");
                        
                        // Calculate popularity category based on participation rate
                        String popularity;
                        if (participationRate >= 75) {
                            popularity = "VERY HIGH";
                        } else if (participationRate >= 50) {
                            popularity = "HIGH";
                        } else if (participationRate >= 25) {
                            popularity = "MEDIUM";
                        } else {
                            popularity = "LOW";
                        }
                        
                        // Add row to table model - adjusted to match the new number of columns
                        tableModel.addRow(new Object[] {
                            tripTo, departureDate, codeExc, excursionTo,
                            participatingCustomers, totalTripCustomers, participationRate,
                            excursionPrice, totalRevenue, popularity
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
                    txtStatus.setText("Connected: Found " + tableModel.getRowCount() + " excursion records");
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