package company;

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

public class Query1 extends JFrame {
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
    
    // The SQL query
    private static final String EMPLOYEE_PROJECT_QUERY = 
        " SELECT e.Fname, e.Lname, d.Dname, e.Dno,\n" +
        "    COUNT(DISTINCT wo.Pno) AS Number_Of_Projects,\n" +
        "    COUNT(DISTINCT dep.Dependent_name) AS Number_Of_Dependents\n" +
        "FROM employee AS e\n" +
        "    INNER JOIN department AS d ON e.Dno = d.Dnumber\n" +
        "    INNER JOIN works_on AS wo ON e.Ssn = wo.Essn\n" +
        "    INNER JOIN project AS p ON wo.Pno = p.Pnumber\n" +
        "    INNER JOIN dependent AS dep ON e.Ssn = dep.Essn\n" +
        "WHERE NOT EXISTS (\n" +
        "        SELECT *\n" +
        "        FROM works_on AS wo2\n" +
        "        INNER JOIN project AS p2 ON wo2.Pno = p2.Pnumber\n" +
        "        WHERE wo2.Essn = e.Ssn AND p2.Dnum != e.Dno\n" +
        "    )\n" +
        "    AND EXISTS (\n" +
        "        SELECT *\n" +
        "        FROM dependent AS dep2\n" +
        "        WHERE dep2.Essn = e.Ssn\n" +
        "        AND dep2.Relationship IN ('Son', 'Daughter')\n" +
        "    )\n" +
        "GROUP BY e.Ssn, e.Fname, e.Lname, d.Dname, e.Dno\n" +
        "ORDER BY Number_Of_Projects DESC, Number_Of_Dependents DESC";

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
                    Query1 frame = new Query1();
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
    public Query1() {
        setTitle("Employee Project and Dependent Analysis");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 800, 600);
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
        
        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setPreferredSize(new Dimension(800, 80));
        headerPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Employee Project and Dependent Analysis");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JLabel subtitleLabel = new JLabel("Find employees with department projects who have dependents");
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleLabel.setForeground(new Color(220, 255, 220));
        subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        contentPane.add(headerPanel, BorderLayout.NORTH);
        
        // Statement panel
        JPanel statementPanel = new JPanel();
        statementPanel.setBorder(BorderFactory.createLineBorder(DARK_GREEN, 1));
        statementPanel.setBackground(MEDIUM_GREEN);
        statementPanel.setPreferredSize(new Dimension(800, 80));
        contentPane.add(statementPanel, BorderLayout.NORTH);
        statementPanel.setLayout(new BorderLayout());
        
        JLabel titleStatementLabel = new JLabel(" Statement:");
        titleStatementLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleStatementLabel.setForeground(Color.WHITE);
        titleStatementLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 0));
        statementPanel.add(titleStatementLabel, BorderLayout.NORTH);
        
        String statementText = "This query retrieves employees where all of their projects correspond to their department, " +
                              "and they have at least one child (son or daughter) as a dependent. Results are sorted by " +
                              "number of projects and dependents.";
                              
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
        statementScrollPane.setPreferredSize(new Dimension(800, 80));
        statementPanel.add(statementScrollPane, BorderLayout.CENTER);
        
        // Results panel
        JPanel resultsPanel = new JPanel();
        resultsPanel.setBorder(BorderFactory.createLineBorder(DARK_GREEN, 1));
        resultsPanel.setBackground(VERY_LIGHT_GREEN);
        contentPane.add(resultsPanel, BorderLayout.CENTER);
        resultsPanel.setLayout(new BorderLayout());
        
        JLabel lblResult = new JLabel("Result:");
        lblResult.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblResult.setForeground(DARK_GREEN);
        lblResult.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 0));
        resultsPanel.add(lblResult, BorderLayout.NORTH);
        
        String[] columnNames = {"First Name", "Last Name", "Department", "Dept No", "Projects", "Dependents"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) return Integer.class;
                if (columnIndex == 4) return Integer.class;
                if (columnIndex == 5) return Integer.class;
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
        header.setForeground(Color.green);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBorder(BorderFactory.createLineBorder(DARK_GREEN, 1));
        header.setPreferredSize(new Dimension(header.getWidth(), 30));
        
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
        controlPanel.setPreferredSize(new Dimension(800, 60));
        contentPane.add(controlPanel, BorderLayout.SOUTH);
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 15));
        
        txtStatus = new JTextField("Not connected to database");
        txtStatus.setEditable(false);
        txtStatus.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtStatus.setForeground(DARK_GREEN);
        txtStatus.setBackground(VERY_LIGHT_GREEN);
        txtStatus.setPreferredSize(new Dimension(200, 30));
        txtStatus.setBorder(BorderFactory.createLineBorder(MEDIUM_GREEN));
        controlPanel.add(txtStatus);
        
        btnConnect = createStyledButton("Connect");
        btnConnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fetchData();
            }
        });
        controlPanel.add(btnConnect);
        
        btnRefresh = createStyledButton("Refresh");
        btnRefresh.setEnabled(false);
        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fetchData();
            }
        });
        controlPanel.add(btnRefresh);
        
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
                return new Dimension(100, 30);
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
     * Show SQL Statement in a dialog
     */
    private void showSQLStatement() {
        JTextArea textArea = new JTextArea(EMPLOYEE_PROJECT_QUERY);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setBackground(VERY_LIGHT_GREEN);
        textArea.setForeground(DARK_GREEN);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(700, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, "SQL Statement", JOptionPane.INFORMATION_MESSAGE);
    }
    
    
    /**
     * Connect to database and fetch data
     */
    private void fetchData() {
        tableModel.setRowCount(0);
        txtStatus.setText("Connecting to database...");
        
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
                    rs = stmt.executeQuery(EMPLOYEE_PROJECT_QUERY);
                    while (rs.next()) {
                        String firstName = rs.getString("Fname");
                        String lastName = rs.getString("Lname");
                        String deptName = rs.getString("Dname");
                        int deptNo = rs.getInt("Dno");
                        int numProjects = rs.getInt("Number_Of_Projects");
                        int numDependents = rs.getInt("Number_Of_Dependents");
                        
                        // Add row to table model
                        tableModel.addRow(new Object[] {
                            firstName, lastName, deptName, deptNo, 
                            numProjects, numDependents
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
                    txtStatus.setText("Found " + tableModel.getRowCount() + " employees");
                    btnRefresh.setEnabled(true);
                    btnConnect.setText("Reconnect");
                } else {
                    txtStatus.setText("Connection failed");
                }
            }
        };
        
        worker.execute();
    }
}