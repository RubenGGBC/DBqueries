package employee;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.event.*;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class ProjectEmployeeHours extends JFrame {
    private JPanel contentPane;
    private JTable tableResults;
    private DefaultTableModel tableModel;
    private JTextField txtStatus;
    private JTextField txtProjectNumber;
    private JTextField txtHoursThreshold;
    private JButton btnConnect;
    private JButton btnSearch;
    private JButton btnClear;
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

    private static final Color DARK_BLUE = new Color(25, 50, 93);
    private static final Color MEDIUM_BLUE = new Color(70, 130, 180);
    private static final Color LIGHT_BLUE = new Color(173, 216, 230);
    private static final Color VERY_LIGHT_BLUE = new Color(240, 248, 255);

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
                    ProjectEmployeeHours frame = new ProjectEmployeeHours();
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
    public ProjectEmployeeHours() {
        setTitle("Project Hours Analysis");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 600);
        setLocationRelativeTo(null);
        
        contentPane = new JPanel();
        contentPane.setBorder(BorderFactory.createLineBorder(DARK_BLUE, 2));
        contentPane.setBackground(VERY_LIGHT_BLUE);
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));
        
        JPanel statementPanel = new JPanel();
        statementPanel.setBorder(BorderFactory.createLineBorder(DARK_BLUE, 1));
        statementPanel.setBackground(MEDIUM_BLUE);
        statementPanel.setPreferredSize(new Dimension(900, 100));
        contentPane.add(statementPanel, BorderLayout.NORTH);
        statementPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Statement:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 0));
        statementPanel.add(titleLabel, BorderLayout.NORTH);

        String statementText = "Given a project number and a number of hours, retrieve (if exists) the employee (Fname and Lname) " +
                             "and the number of hours worked in the given Project of the employee that works the highest number " +
                             "of hours in the given project between those employees that work less than the given number of hours " +
                             "in the given project. Also retrieve (if exists) the same information for the employee that works the " +
                             "lowest number of hours in the given project between those employees that work more than the given " +
                             "number of hours in the given project.";
                              
        JTextArea txtStatement = new JTextArea(statementText);
        txtStatement.setFont(new Font("Arial", Font.BOLD, 14));
        txtStatement.setForeground(Color.WHITE);
        txtStatement.setBackground(MEDIUM_BLUE);
        txtStatement.setWrapStyleWord(true);
        txtStatement.setLineWrap(true);
        txtStatement.setEditable(false);
        txtStatement.setMargin(new Insets(10, 10, 10, 10));
        
        JScrollPane statementScrollPane = new JScrollPane(txtStatement);
        statementScrollPane.setBorder(BorderFactory.createEmptyBorder());
        statementScrollPane.setPreferredSize(new Dimension(900, 80));
        statementPanel.add(statementScrollPane, BorderLayout.CENTER);
        
        JPanel inputPanel = new JPanel();
        inputPanel.setBorder(BorderFactory.createLineBorder(DARK_BLUE, 1));
        inputPanel.setBackground(LIGHT_BLUE);
        inputPanel.setPreferredSize(new Dimension(900, 70));
        contentPane.add(inputPanel, BorderLayout.NORTH);
        statementPanel.add(inputPanel, BorderLayout.SOUTH);
        inputPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        
        JLabel lblProject = new JLabel("Project Number:");
        lblProject.setFont(new Font("Arial", Font.BOLD, 14));
        lblProject.setForeground(Color.WHITE);
        inputPanel.add(lblProject);
        
        txtProjectNumber = new JTextField("30");  // Default value
        txtProjectNumber.setFont(new Font("Arial", Font.PLAIN, 14));
        txtProjectNumber.setPreferredSize(new Dimension(60, 30));
        inputPanel.add(txtProjectNumber);
        
        JLabel lblHours = new JLabel("Hours Threshold:");
        lblHours.setFont(new Font("Arial", Font.BOLD, 14));
        lblHours.setForeground(Color.WHITE);
        inputPanel.add(lblHours);
        
        txtHoursThreshold = new JTextField("15");  // Default value
        txtHoursThreshold.setFont(new Font("Arial", Font.PLAIN, 14));
        txtHoursThreshold.setPreferredSize(new Dimension(60, 30));
        inputPanel.add(txtHoursThreshold);
        
        btnSearch = createStyledButton("Search", DARK_BLUE, Color.WHITE);
        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fetchData();
            }
        });
        btnSearch.setEnabled(false);  // Will be enabled after connection
        inputPanel.add(btnSearch);
        
        JPanel resultsPanel = new JPanel();
        resultsPanel.setBorder(BorderFactory.createLineBorder(DARK_BLUE, 1));
        resultsPanel.setBackground(VERY_LIGHT_BLUE);
        contentPane.add(resultsPanel, BorderLayout.CENTER);
        resultsPanel.setLayout(new BorderLayout());
        
        JLabel lblResult = new JLabel("Results:");
        lblResult.setFont(new Font("Arial", Font.BOLD, 14));
        lblResult.setForeground(DARK_BLUE);
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
        tableResults.setFont(new Font("Arial", Font.PLAIN, 12));
        tableResults.setRowHeight(25);
        tableResults.setGridColor(MEDIUM_BLUE);
        tableResults.setSelectionBackground(LIGHT_BLUE);
        tableResults.setSelectionForeground(DARK_BLUE);
        
        JTableHeader header = tableResults.getTableHeader();
        header.setBackground(MEDIUM_BLUE);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 12));
        header.setBorder(BorderFactory.createLineBorder(DARK_BLUE, 1));
        header.setPreferredSize(new Dimension(header.getWidth(), 30)); 
        
        tableResults.getColumnModel().getColumn(0).setPreferredWidth(200); // Category
        tableResults.getColumnModel().getColumn(1).setPreferredWidth(100); // First Name
        tableResults.getColumnModel().getColumn(2).setPreferredWidth(100); // Last Name
        tableResults.getColumnModel().getColumn(3).setPreferredWidth(100); // Project Number
        tableResults.getColumnModel().getColumn(4).setPreferredWidth(100); // Hours Worked
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        tableResults.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? VERY_LIGHT_BLUE : Color.WHITE);
                    c.setForeground(DARK_BLUE);
                }
                
                ((JLabel) c).setHorizontalAlignment(JLabel.CENTER);
                
                ((JComponent) c).setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 230, 240), 1),
                    BorderFactory.createEmptyBorder(2, 5, 2, 5)
                ));
                
                return c;
            }
        });
        
        tableResults.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Project Number
        tableResults.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Hours
        
        JScrollPane scrollPane = new JScrollPane(tableResults);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MEDIUM_BLUE, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        scrollPane.getViewport().setBackground(VERY_LIGHT_BLUE);
        scrollPane.setBackground(VERY_LIGHT_BLUE);
        
        scrollPane.setColumnHeaderView(tableResults.getTableHeader());
        tableResults.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = MEDIUM_BLUE;
                this.trackColor = VERY_LIGHT_BLUE;
            }
        });
        scrollPane.getHorizontalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = MEDIUM_BLUE;
                this.trackColor = VERY_LIGHT_BLUE;
            }
        });
        
        resultsPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel controlPanel = new JPanel();
        controlPanel.setBorder(BorderFactory.createLineBorder(DARK_BLUE, 1));
        controlPanel.setBackground(LIGHT_BLUE);
        controlPanel.setPreferredSize(new Dimension(900, 60));
        contentPane.add(controlPanel, BorderLayout.SOUTH);
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 15));
        
        txtStatus = new JTextField("Not connected to database");
        txtStatus.setEditable(false);
        txtStatus.setFont(new Font("Arial", Font.BOLD, 12));
        txtStatus.setForeground(DARK_BLUE);
        txtStatus.setBackground(VERY_LIGHT_BLUE);
        txtStatus.setPreferredSize(new Dimension(200, 25));
        txtStatus.setBorder(BorderFactory.createLineBorder(MEDIUM_BLUE));
        controlPanel.add(txtStatus);
        
        btnConnect = createStyledButton("Connect", DARK_BLUE, Color.WHITE);
        btnConnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                connectToDatabase();
            }
        });
        controlPanel.add(btnConnect);
        
        btnClear = createStyledButton("Clear", DARK_BLUE, Color.WHITE);
        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearTable();
            }
        });
        controlPanel.add(btnClear);
        
        JButton btnExit = createStyledButton("Exit", DARK_BLUE, Color.WHITE);
        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        controlPanel.add(btnExit);
        
        // Add a button to show the SQL query
        JButton btnShowQuery = createStyledButton("Show SQL", DARK_BLUE, Color.WHITE);
        btnShowQuery.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showSqlQuery();
            }
        });
        controlPanel.add(btnShowQuery);
    }
    
    /**
     * Method to create buttons with consistent style
     */
    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(100, 30));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        return button;
    }
    
    /**
     * Show the SQL query in a popup window with the current parameter values
     */
    private void showSqlQuery() {
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
        
        JDialog dialog = new JDialog(this, "SQL Query", true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(VERY_LIGHT_BLUE);
        
        JTextArea txtQuery = new JTextArea(formattedQuery);
        txtQuery.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtQuery.setEditable(false);
        txtQuery.setBackground(Color.WHITE);
        txtQuery.setForeground(DARK_BLUE);
        txtQuery.setBorder(BorderFactory.createLineBorder(MEDIUM_BLUE));
        txtQuery.setMargin(new Insets(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(txtQuery);
        scrollPane.setBorder(BorderFactory.createLineBorder(DARK_BLUE));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        JButton btnClose = createStyledButton("Close", DARK_BLUE, Color.WHITE);
        btnClose.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(VERY_LIGHT_BLUE);
        buttonPanel.add(btnClose);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setContentPane(mainPanel);
        dialog.setVisible(true);
    }
    
    /**
     * Connect to database
     */
    private void connectToDatabase() {
        txtStatus.setText("Connecting to database...");
        
        // Use SwingWorker to perform database operations in background
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private boolean success = false;
            
            @Override
            protected Void doInBackground() throws Exception {
                Connection conn = null;
                
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    conn = DriverManager.getConnection(DB_URL, USER, PASS);
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
                    txtStatus.setText("Connected to database");
                    btnSearch.setEnabled(true);
                    btnConnect.setText("Reconnect");
                    // Automatically perform initial search with default values
                    fetchData();
                } else {
                    txtStatus.setText("Connection failed");
                }
            }
        };
        
        worker.execute();
    }
    
    
    private void clearTable() {
        tableModel.setRowCount(0);
        txtStatus.setText("Results cleared");
    }
    
  
    private void fetchData() {
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
        txtStatus.setText("Fetching data...");
        
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
                        txtStatus.setText("Found " + resultCount + " results");
                    } else {
                        txtStatus.setText("No employees found matching criteria");
                    }
                } else {
                    txtStatus.setText("Query failed");
                }
            }
        };
        
        worker.execute();
    }
}