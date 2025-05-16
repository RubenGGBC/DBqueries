package company;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class Query3 extends JFrame {
    private JPanel contentPane;
    private JTable tableResults;
    private DefaultTableModel tableModel;
    private JTextField txtStatus;
    private JTextField txtProjectNumber;
    private JTextField txtHoursThreshold;
    private JButton btnConnect;
    private JButton btnClear;
    private JButton btnShowSQL;
    private JButton btnExport;
    
    private static final String DB_URL = "jdbc:mysql://dif-mysql.ehu.es:23306/DBI08";
    private static final String USER = "DBI08";
    private static final String PASS = "DBI08";
    
    private static final String EMPLOYEE_HOURS_QUERY_TEMPLATE = 
        "SELECT 'Highest hours below given hours' AS Category,\n" +
        "    e1.Fname, e1.Lname, ? AS Project_Number, wo1.Hours\n" +
        "FROM employee e1\n" +
        "    INNER JOIN works_on AS wo1 ON e1.Ssn = wo1.Essn\n" +
        "WHERE wo1.Pno = ? AND wo1.Hours < ?\n" +
        "    AND wo1.Hours = (\n" +
        "        SELECT MAX(wo2.Hours)\n" +
        "        FROM works_on AS wo2\n" +
        "        WHERE wo2.Pno = ? AND wo2.Hours < ?\n" +
        "    )\n" +
        "    \n" +
        "UNION ALL\n" +
        "SELECT 'Lowest hours above given hours' AS Category,\n" +
        "    e3.Fname, e3.Lname, ? AS Project_Number, wo3.Hours\n" +
        "FROM employee e3\n" +
        "    INNER JOIN works_on AS wo3 ON e3.Ssn = wo3.Essn\n" +
        "WHERE wo3.Pno = ?\n" +
        "    AND wo3.Hours > ?\n" +
        "    AND wo3.Hours = (\n" +
        "        SELECT MIN(wo4.Hours)\n" +
        "        FROM works_on wo4\n" +
        "        WHERE wo4.Pno = ? AND wo4.Hours > ?\n" +
        "    );";
    
    // Green color theme for Employee package
    private static final Color DARK_GREEN = new Color(25, 80, 45);
    private static final Color MEDIUM_GREEN = new Color(46, 125, 50);
    private static final Color LIGHT_GREEN = new Color(129, 199, 132);
    private static final Color VERY_LIGHT_GREEN = new Color(232, 245, 233);
    private static final Color TEXT_COLOR = new Color(33, 33, 33);
    private static final Color ACCENT_COLOR = new Color(76, 175, 80);

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
        setTitle("Project Hours Analysis");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 900, 600);
        setLocationRelativeTo(null);
        
        // Create gradient panel
        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, DARK_GREEN,
                                                  getWidth(), getHeight(), new Color(40, 110, 60));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        contentPane.setBorder(BorderFactory.createLineBorder(DARK_GREEN, 2));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));
        
        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setPreferredSize(new Dimension(900, 80));
        headerPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Project Hours Analysis");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(Color.green.darker());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JLabel subtitleLabel = new JLabel("Find employees with highest and lowest hours worked on projects");
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleLabel.setForeground(new Color(220, 255, 220));
        subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        contentPane.add(headerPanel, BorderLayout.NORTH);
        
        // Statement panel
        JPanel statementPanel = new JPanel();
        statementPanel.setBorder(BorderFactory.createLineBorder(DARK_GREEN, 1));
        statementPanel.setBackground(MEDIUM_GREEN);
        statementPanel.setPreferredSize(new Dimension(900, 80));
        contentPane.add(statementPanel, BorderLayout.NORTH);
        statementPanel.setLayout(new BorderLayout());
        
        JLabel titleStatementLabel = new JLabel(" Statement:");
        titleStatementLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleStatementLabel.setForeground(Color.WHITE);
        titleStatementLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 0));
        statementPanel.add(titleStatementLabel, BorderLayout.NORTH);
        
        String statementText = "Given a project number and hours threshold, find the employee with the highest hours below " +
                             "the threshold and the employee with the lowest hours above the threshold.";
                              
        JTextArea txtStatement = new JTextArea(statementText);
        txtStatement.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtStatement.setForeground(Color.WHITE);
        txtStatement.setBackground(MEDIUM_GREEN);
        txtStatement.setWrapStyleWord(true);
        txtStatement.setLineWrap(true);
        txtStatement.setEditable(false);
        txtStatement.setMargin(new Insets(10, 10, 10, 10));
        
        JScrollPane statementScrollPane = new JScrollPane(txtStatement);
        statementScrollPane.setBorder(BorderFactory.createEmptyBorder());
        statementScrollPane.setPreferredSize(new Dimension(900, 80));
        statementPanel.add(statementScrollPane, BorderLayout.CENTER);
        
        // Parameters panel
        JPanel parametersPanel = new JPanel();
        parametersPanel.setBackground(LIGHT_GREEN);
        parametersPanel.setBorder(BorderFactory.createLineBorder(DARK_GREEN, 1));
        parametersPanel.setPreferredSize(new Dimension(900, 70));
        
        // Use GridBagLayout for better control over component placement
        parametersPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Project Number label
        JLabel lblProjectNumber = new JLabel("Project Number:");
        lblProjectNumber.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblProjectNumber.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.1;
        parametersPanel.add(lblProjectNumber, gbc);
        
        // Project Number text field
        txtProjectNumber = new JTextField("30");
        txtProjectNumber.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtProjectNumber.setBackground(VERY_LIGHT_GREEN);
        txtProjectNumber.setForeground(DARK_GREEN);
        gbc.gridx = 1;
        gbc.weightx = 0.3;
        parametersPanel.add(txtProjectNumber, gbc);
        
        // Hours Threshold label
        JLabel lblHoursThreshold = new JLabel("Hours Threshold:");
        lblHoursThreshold.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHoursThreshold.setForeground(Color.WHITE);
        gbc.gridx = 2;
        gbc.weightx = 0.1;
        gbc.insets = new Insets(10, 30, 10, 5);
        parametersPanel.add(lblHoursThreshold, gbc);
        
        // Hours Threshold text field
        txtHoursThreshold = new JTextField("15");
        txtHoursThreshold.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtHoursThreshold.setBackground(VERY_LIGHT_GREEN);
        txtHoursThreshold.setForeground(DARK_GREEN);
        gbc.gridx = 3;
        gbc.weightx = 0.3;
        gbc.insets = new Insets(10, 5, 10, 20);
        parametersPanel.add(txtHoursThreshold, gbc);
        
        contentPane.add(parametersPanel, BorderLayout.NORTH);
        
        // Results panel
        JPanel resultsPanel = new JPanel();
        resultsPanel.setBorder(BorderFactory.createLineBorder(DARK_GREEN, 1));
        resultsPanel.setBackground(VERY_LIGHT_GREEN);
        contentPane.add(resultsPanel, BorderLayout.CENTER);
        resultsPanel.setLayout(new BorderLayout());
        
        JLabel lblResult = new JLabel("Results:");
        lblResult.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblResult.setForeground(DARK_GREEN);
        lblResult.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 0));
        resultsPanel.add(lblResult, BorderLayout.NORTH);
        
        String[] columnNames = {
            "Category", 
            "First Name", 
            "Last Name", 
            "Project Number", 
            "Hours Worked"
        };
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) return Integer.class; // Project Number
                if (columnIndex == 4) return Double.class;  // Hours
                return String.class;
            }
        };
        
        tableResults = new JTable(tableModel);
        tableResults.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableResults.setRowHeight(25);
        tableResults.setGridColor(LIGHT_GREEN);
        tableResults.setSelectionBackground(LIGHT_GREEN);
        tableResults.setSelectionForeground(DARK_GREEN);
        
        JTableHeader header = tableResults.getTableHeader();
        header.setBackground(MEDIUM_GREEN);
        header.setForeground(Color.green.darker());
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBorder(BorderFactory.createLineBorder(DARK_GREEN, 1));
        header.setPreferredSize(new Dimension(header.getWidth(), 30));
        
        // Set alternative row colors
        tableResults.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(table.getSelectionForeground());
                } else {
                    c.setBackground(row % 2 == 0 ? VERY_LIGHT_GREEN : Color.WHITE);
                    c.setForeground(TEXT_COLOR);
                }

                ((JLabel) c).setHorizontalAlignment(JLabel.CENTER);

                return c;
            }
        });
        
        // Create a styled scrollpane
        JScrollPane scrollPane = new JScrollPane(tableResults);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MEDIUM_GREEN, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        scrollPane.getViewport().setBackground(VERY_LIGHT_GREEN);
        scrollPane.setBackground(VERY_LIGHT_GREEN);
        
        // Style scrollbars
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = MEDIUM_GREEN;
                this.trackColor = VERY_LIGHT_GREEN;
            }
        });
        scrollPane.getHorizontalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = MEDIUM_GREEN;
                this.trackColor = VERY_LIGHT_GREEN;
            }
        });
        
        resultsPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setBorder(BorderFactory.createLineBorder(DARK_GREEN, 1));
        controlPanel.setBackground(LIGHT_GREEN);
        controlPanel.setPreferredSize(new Dimension(900, 60));
        contentPane.add(controlPanel, BorderLayout.SOUTH);
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 15));
        
        txtStatus = new JTextField("Ready to connect and search");
        txtStatus.setEditable(false);
        txtStatus.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtStatus.setForeground(DARK_GREEN);
        txtStatus.setBackground(VERY_LIGHT_GREEN);
        txtStatus.setPreferredSize(new Dimension(250, 30));
        txtStatus.setBorder(BorderFactory.createLineBorder(MEDIUM_GREEN));
        controlPanel.add(txtStatus);
        
        btnConnect = createStyledButton("Connect & Search");
        btnConnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                connectAndSearch();
            }
        });
        controlPanel.add(btnConnect);
        
        btnClear = createStyledButton("Clear");
        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearTable();
            }
        });
        controlPanel.add(btnClear);
        
        btnShowSQL = createStyledButton("Show SQL");
        btnShowSQL.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showSQLStatement();
            }
        });
        controlPanel.add(btnShowSQL);
    }
    
    /**
     * Create a styled button with custom painting
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(DARK_GREEN);
                } else if (getModel().isRollover()) {
                    g2.setColor(ACCENT_COLOR);
                } else {
                    g2.setColor(MEDIUM_GREEN);
                }
                
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(new Color(255, 255, 255, 50));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                
                // Draw text
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                g2.setColor(Color.WHITE);
                
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int x = (getWidth() - textWidth) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(text, x, y);
                
                g2.dispose();
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(130, 30);
            }
        };
        
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    /**
     * Show SQL Statement with parameter values filled in
     */
    private void showSQLStatement() {
        String projectNumber = txtProjectNumber.getText().trim();
        String hoursThreshold = txtHoursThreshold.getText().trim();
        
        // Replace placeholder parameters with actual values for display
        String formattedQuery = EMPLOYEE_HOURS_QUERY_TEMPLATE
            .replaceAll("\\?", "\\{param\\}")
            .replace("{param}", projectNumber)
            .replace("{param}", projectNumber)
            .replace("{param}", hoursThreshold)
            .replace("{param}", projectNumber)
            .replace("{param}", hoursThreshold)
            .replace("{param}", projectNumber)
            .replace("{param}", projectNumber)
            .replace("{param}", hoursThreshold)
            .replace("{param}", projectNumber)
            .replace("{param}", hoursThreshold);
        
        JTextArea textArea = new JTextArea(formattedQuery);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setBackground(VERY_LIGHT_GREEN);
        textArea.setForeground(DARK_GREEN);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(700, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, "SQL Statement", JOptionPane.INFORMATION_MESSAGE);
    }
    
    
    /**
     * Clear the result table
     */
    private void clearTable() {
        tableModel.setRowCount(0);
        txtStatus.setText("Results cleared");
    }
    
    /**
     * Connect to database and search with parameters
     */
    private void connectAndSearch() {
        String projectNumberStr = txtProjectNumber.getText().trim();
        String hoursThresholdStr = txtHoursThreshold.getText().trim();
        
        int projectNumber;
        double hoursThreshold;
        
        try {
            projectNumber = Integer.parseInt(projectNumberStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid Project Number", 
                "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            hoursThreshold = Double.parseDouble(hoursThresholdStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid Hours Threshold", 
                "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Clear existing data
        tableModel.setRowCount(0);
        txtStatus.setText("Connecting to database and fetching data...");
        
        // Use SwingWorker to perform database operations in background
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private boolean success = false;
            private int resultCount = 0;
            
            @Override
            protected Void doInBackground() throws Exception {
                Connection conn = null;
                PreparedStatement pstmt = null;
                ResultSet rs = null;
                
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    conn = DriverManager.getConnection(DB_URL, USER, PASS);
                    
                    // Prepare statement with parameters
                    pstmt = conn.prepareStatement(EMPLOYEE_HOURS_QUERY_TEMPLATE);
                    
                    // Set all parameter values
                    pstmt.setInt(1, projectNumber);      // Project Number in first SELECT
                    pstmt.setInt(2, projectNumber);      // Project Number in first WHERE
                    pstmt.setDouble(3, hoursThreshold);  // Hours threshold in first WHERE
                    pstmt.setInt(4, projectNumber);      // Project Number in subquery
                    pstmt.setDouble(5, hoursThreshold);  // Hours threshold in subquery
                    pstmt.setInt(6, projectNumber);      // Project Number in UNION SELECT
                    pstmt.setInt(7, projectNumber);      // Project Number in UNION WHERE
                    pstmt.setDouble(8, hoursThreshold);  // Hours threshold in UNION WHERE
                    pstmt.setInt(9, projectNumber);      // Project Number in UNION subquery
                    pstmt.setDouble(10, hoursThreshold); // Hours threshold in UNION subquery
                    
                    rs = pstmt.executeQuery();
                    
                    while (rs.next()) {
                        String category = rs.getString("Category");
                        String firstName = rs.getString("Fname");
                        String lastName = rs.getString("Lname");
                        int projectNum = rs.getInt("Project_Number");
                        double hours = rs.getDouble("Hours");
                        
                        // Add row to table model
                        tableModel.addRow(new Object[] {
                            category, firstName, lastName, projectNum, hours
                        });
                        
                        resultCount++;
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
                    if (resultCount > 0) {
                        txtStatus.setText("Connected successfully. Found " + resultCount + " result(s) for Project=" + projectNumber + ", Hours=" + hoursThreshold);
                    } else {
                        txtStatus.setText("Connected successfully. No employees found for Project=" + projectNumber + " with Hours threshold=" + hoursThreshold);
                    }
                    btnConnect.setText("Search Again");
                } else {
                    txtStatus.setText("Connection failed or query error");
                }
            }
        };
        
        worker.execute();
    }
}