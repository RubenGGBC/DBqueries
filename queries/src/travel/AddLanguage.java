package travel;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class AddLanguage extends JFrame {
    private JPanel contentPane;
    private JTextField txtGuideId;
    private JTextField txtLanguage;
    private JTextField txtStatus;
    private JButton btnAdd;
    private JButton btnClear;
    private JButton btnShowGuides;
    private JButton btnExit;
    private JButton btnConnect;
    private JButton btnRefresh;
    private JTable languagesTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    
    private static final String DB_URL = "jdbc:mysql://dif-mysql.ehu.es:23306/DBI08";
    private static final String USER = "DBI08";
    private static final String PASS = "DBI08";
    
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
        setTitle("Tour Guide Languages Manager");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 1000, 700);
        setLocationRelativeTo(null);
        
        // Main panel with gradient background
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
        panelHeader.setPreferredSize(new Dimension(1000, 100));
        contentPane.add(panelHeader, BorderLayout.NORTH);
        panelHeader.setLayout(null);
        
        JLabel lblTitle = new JLabel("Tour Guide Languages Manager");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setBounds(10, 11, 980, 42);
        panelHeader.add(lblTitle);
        
        JLabel lblSubtitle = new JLabel("View and manage languages spoken by tour guides");
        lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblSubtitle.setForeground(new Color(200, 230, 255));
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtitle.setBounds(10, 50, 980, 30);
        panelHeader.add(lblSubtitle);
        
        // Split the content into two parts: form and table
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setOpaque(false);
        splitPane.setDividerLocation(200);
        splitPane.setDividerSize(5);
        splitPane.setBorder(null);
        contentPane.add(splitPane, BorderLayout.CENTER);
        
        // Form panel
        JPanel panelForm = new JPanel();
        panelForm.setOpaque(false);
        panelForm.setPreferredSize(new Dimension(1000, 200));
        panelForm.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        splitPane.setTopComponent(panelForm);
        panelForm.setLayout(null);
        
        // Guide ID
        JLabel lblGuideId = new JLabel("Guide ID:");
        lblGuideId.setForeground(Color.WHITE);
        lblGuideId.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblGuideId.setBounds(50, 20, 150, 30);
        panelForm.add(lblGuideId);
        
        txtGuideId = new JTextField();
        txtGuideId.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtGuideId.setBounds(200, 20, 200, 35);
        txtGuideId.setBackground(VERY_LIGHT_BLUE);
        txtGuideId.setForeground(DARK_BLUE);
        txtGuideId.setBorder(BorderFactory.createLineBorder(LIGHT_BLUE));
        panelForm.add(txtGuideId);
        
        // Language
        JLabel lblLanguage = new JLabel("Language:");
        lblLanguage.setForeground(Color.WHITE);
        lblLanguage.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblLanguage.setBounds(50, 70, 150, 30);
        panelForm.add(lblLanguage);
        
        txtLanguage = new JTextField();
        txtLanguage.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtLanguage.setBounds(200, 70, 200, 35);
        txtLanguage.setBackground(VERY_LIGHT_BLUE);
        txtLanguage.setForeground(DARK_BLUE);
        txtLanguage.setBorder(BorderFactory.createLineBorder(LIGHT_BLUE));
        panelForm.add(txtLanguage);
        
        // Information panel
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(70, 130, 180, 120));
        infoPanel.setBounds(450, 20, 450, 120);
        infoPanel.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 100)));
        panelForm.add(infoPanel);
        infoPanel.setLayout(new BorderLayout());
        
        JLabel lblInfo = new JLabel("<html><div style='text-align: center; margin: 10px;'>" + 
                "<b>Instructions:</b><br/>" +
                "Add a new language that a tour guide can speak. " +
                "Both the Guide ID and Language name are required.<br/><br/>" +
                "Press 'Show Guides' to see a list of available tour guides.<br/>" +
                "Press 'Connect' to view existing language assignments." +
                "</div></html>");
        lblInfo.setForeground(Color.WHITE);
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoPanel.add(lblInfo, BorderLayout.CENTER);
        
        // Form buttons panel
        JPanel formButtonsPanel = new JPanel();
        formButtonsPanel.setOpaque(false);
        formButtonsPanel.setBounds(50, 130, 350, 50);
        panelForm.add(formButtonsPanel);
        formButtonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));
        
        // Add button
        btnAdd = createStyledButton("Add Language");
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addLanguageToDatabase();
            }
        });
        formButtonsPanel.add(btnAdd);
        
        // Show Guides button
        btnShowGuides = createStyledButton("Show Guides");
        btnShowGuides.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showGuides();
            }
        });
        formButtonsPanel.add(btnShowGuides);
        
        // Clear button
        btnClear = createStyledButton("Clear Form");
        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
        formButtonsPanel.add(btnClear);
        
        // Table panel
        JPanel panelTable = new JPanel();
        panelTable.setOpaque(false);
        splitPane.setBottomComponent(panelTable);
        panelTable.setLayout(new BorderLayout(0, 0));
        
        // Statement panel (similar to query1)
        JPanel statementPanel = new JPanel();
        statementPanel.setBorder(BorderFactory.createLineBorder(LIGHT_BLUE, 1));
        statementPanel.setBackground(new Color(25, 55, 85));
        statementPanel.setPreferredSize(new Dimension(1000, 40));
        panelTable.add(statementPanel, BorderLayout.NORTH);
        statementPanel.setLayout(new BorderLayout());
        
        JLabel statementLabel = new JLabel(" Languages Data Table:");
        statementLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statementLabel.setForeground(Color.WHITE);
        statementPanel.add(statementLabel, BorderLayout.WEST);
        
        JTextArea statementText = new JTextArea(
            "This table shows all languages associated with tour guides in the database."
        );
        statementText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statementText.setForeground(Color.WHITE);
        statementText.setBackground(new Color(25, 55, 85));
        statementText.setWrapStyleWord(true);
        statementText.setLineWrap(true);
        statementText.setEditable(false);
        statementText.setMargin(new Insets(5, 20, 5, 10));
        statementPanel.add(statementText, BorderLayout.CENTER);
        
        // Set up table model
        String[] columnNames = {"Guide ID", "Guide Name", "Language"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        languagesTable = new JTable(tableModel);
        languagesTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        languagesTable.setRowHeight(25);
        languagesTable.setIntercellSpacing(new Dimension(10, 5));
        languagesTable.setFillsViewportHeight(true);
        languagesTable.setShowVerticalLines(false);
        languagesTable.setGridColor(new Color(230, 240, 250));
        languagesTable.setSelectionBackground(LIGHT_BLUE);
        languagesTable.setSelectionForeground(Color.WHITE);
        
        // Default cell renderer for consistent alternating rows
        languagesTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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
                if (column != 1) { // Skip guide name
                    ((JLabel) c).setHorizontalAlignment(JLabel.CENTER);
                }
                
                return c;
            }
        });
        
        // Table header styling
        JTableHeader header = languagesTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(MEDIUM_BLUE);
        header.setForeground(DARK_BLUE);
        header.setPreferredSize(new Dimension(100, 35));
        
        // Create scroll pane with styled scrollbars
        scrollPane = new JScrollPane(languagesTable);
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
        
        // Footer panel with controls
        JPanel panelFooter = new JPanel();
        panelFooter.setOpaque(false);
        panelFooter.setPreferredSize(new Dimension(1000, 80));
        contentPane.add(panelFooter, BorderLayout.SOUTH);
        panelFooter.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        
        // Add status field
        txtStatus = new JTextField("Not connected to database");
        txtStatus.setEditable(false);
        txtStatus.setForeground(new Color(255, 200, 200));
        txtStatus.setBackground(new Color(25, 84, 123));
        txtStatus.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtStatus.setBorder(null);
        txtStatus.setHorizontalAlignment(SwingConstants.CENTER);
        txtStatus.setPreferredSize(new Dimension(250, 35));
        panelFooter.add(txtStatus);
        
        // Connect button
        btnConnect = createStyledButton("Connect");
        btnConnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fetchLanguageData();
            }
        });
        panelFooter.add(btnConnect);
        
        // Refresh button
        btnRefresh = createStyledButton("Refresh");
        btnRefresh.setEnabled(false);
        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fetchLanguageData();
            }
        });
        panelFooter.add(btnRefresh);
        
       
  
    }
    
    /**
     * Creates a styled button with visual effects
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
                
                g2.setColor(Color.WHITE);
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
     * Clear the form fields
     */
    private void clearForm() {
        txtGuideId.setText("");
        txtLanguage.setText("");
        txtStatus.setText("Form cleared");
    }
    
    /**
     * Show list of available guides
     */
    private void showGuides() {
        SwingWorker<JTable, Void> worker = new SwingWorker<JTable, Void>() {
            @Override
            protected JTable doInBackground() throws Exception {
                Connection conn = null;
                Statement stmt = null;
                ResultSet rs = null;
                
                try {
                    conn = DriverManager.getConnection(DB_URL, USER, PASS);
                    stmt = conn.createStatement();
                    rs = stmt.executeQuery("SELECT GuideId, guidename FROM tourguide ORDER BY GuideId");
                    
                    // Create table model
                    String[] columnNames = {"Guide ID", "Guide Name"};
                    DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
                        @Override
                        public boolean isCellEditable(int row, int column) {
                            return false;
                        }
                    };
                    
                    // Populate model
                    while (rs.next()) {
                        model.addRow(new Object[] {
                            rs.getString("GuideId"),
                            rs.getString("guidename")
                        });
                    }
                    
                    // Create and configure table
                    JTable guidesTable = new JTable(model);
                    guidesTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    guidesTable.setRowHeight(25);
                    guidesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    
                    // Set colors
                    guidesTable.setBackground(VERY_LIGHT_BLUE);
                    guidesTable.setForeground(DARK_BLUE);
                    guidesTable.setSelectionBackground(LIGHT_BLUE);
                    guidesTable.setSelectionForeground(Color.blue);
                    
                    // Set header style
                    JTableHeader header = guidesTable.getTableHeader();
                    header.setBackground(MEDIUM_BLUE);
                    header.setForeground(Color.blue.darker());
                    header.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    
                    // Add selection listener
                    guidesTable.getSelectionModel().addListSelectionListener(e -> {
                        if (!e.getValueIsAdjusting() && guidesTable.getSelectedRow() != -1) {
                            String guideId = (String) guidesTable.getValueAt(guidesTable.getSelectedRow(), 0);
                            txtGuideId.setText(guideId);
                        }
                    });
                    
                    return guidesTable;
                    
                } finally {
                    try {
                        if (rs != null) rs.close();
                        if (stmt != null) stmt.close();
                        if (conn != null) conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            
            @Override
            protected void done() {
                try {
                    JTable guidesTable = get();
                    
                    // Create scrollpane
                    JScrollPane scrollPane = new JScrollPane(guidesTable);
                    scrollPane.setPreferredSize(new Dimension(500, 300));
                    
                    // Show in dialog
                    JOptionPane.showMessageDialog(
                        AddLanguage.this,
                        scrollPane,
                        "Available Tour Guides",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    
                } catch (Exception e) {
                	JOptionPane.showInputDialog(this);
                    
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Fetch languages data from database
     */
    private void fetchLanguageData() {
        // Clear existing data
        tableModel.setRowCount(0);
        txtStatus.setText("Connecting to database...");
        txtStatus.setForeground(new Color(255, 255, 200));
        
        // Use SwingWorker to perform database operations in background
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private boolean success = false;
            private int recordCount = 0;
            
            @Override
            protected Void doInBackground() throws Exception {
                Connection conn = null;
                Statement stmt = null;
                ResultSet rs = null;
                
                try {
                    conn = DriverManager.getConnection(DB_URL, USER, PASS);
                    stmt = conn.createStatement();
                    String query = "SELECT l.GuideId, t.guidename, l.Lang " +
                                   "FROM languages l " +
                                   "JOIN tourguide t ON l.GuideId = t.GuideId " +
                                   "ORDER BY l.GuideId, l.Lang";
                    
                    
                    rs = stmt.executeQuery(query);
                    
                    while (rs.next()) {
                        String guideId = rs.getString("GuideId");
                        String guideName = rs.getString("guidename");
                        String language = rs.getString("Lang");
                        
                        // Add row to table model
                        tableModel.addRow(new Object[] {
                            guideId, guideName, language
                        });
                        recordCount++;
                    }
                    
                    success = true;
                } catch (SQLException e) {
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
                    txtStatus.setText("Connected: Found " + recordCount + " languages");
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
    
    /**
     * Add language to database
     */
    private void addLanguageToDatabase() {
        
        String guideId = txtGuideId.getText().trim();
        String language = txtLanguage.getText().trim();
        
        // Update status
        txtStatus.setText("Adding language...");
        
        Connection conn = null;
        PreparedStatement pstmt = null;
                
        try {
                    
        	Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            
            // Disable auto-commit
            conn.setAutoCommit(false);
            
            // Add the language
            pstmt = conn.prepareStatement("INSERT INTO languages (GuideId, Lang) VALUES (?, ?)");
            pstmt.setString(1, guideId);
            pstmt.setString(2, language);
            pstmt.executeUpdate();
                    
            // Commit changes
            conn.commit();
            
            JOptionPane.showMessageDialog(this, 
                    "Record added successfully", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            
            //Refresh data in the table
            fetchLanguageData();
            
        }catch (SQLException e) {
            try {
                // Rollback the transaction
                conn.rollback();
                        
                JOptionPane.showMessageDialog(this, 
                    "Rollback has been done: " +
                    "It is likely that the introduced GuideId is not in the list of guides (you can check the guides clicking the \"Show Guides\" button) or that the introduced combination is already in the list", 
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
