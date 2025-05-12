package employee;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.event.*;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class EmployyePairs extends JFrame {
    private JPanel contentPane;
    private JTable tableResults;
    private DefaultTableModel tableModel;
    private JTextField txtStatus;
    private JButton btnConnect;
    private JButton btnRefresh;
    private static final String DB_URL = "jdbc:mysql://dif-mysql.ehu.es:23306/DBI08";
    private static final String USER = "DBI08";
    private static final String PASS = "DBI08";
    
    private static final String DEPARTMENT_COMPARISON_QUERY = 
        "SELECT d1.Dname AS First_Department_Name, d1.Dnumber AS First_Department_Number,\n" +
        "    (\n" +
        "        SELECT COUNT(*)\n" +
        "        FROM employee AS E_Male\n" +
        "        WHERE E_Male.Dno = d1.Dnumber AND E_Male.Sex = 'M'\n" +
        "    ) AS Number_Male_Employees_Dept1,\n" +
        "    d2.Dname AS Second_Department_Name, d2.Dnumber AS Second_Department_Number,\n" +
        "    (\n" +
        "        SELECT COUNT(*)\n" +
        "        FROM employee AS E_Female\n" +
        "        WHERE E_Female.Dno = d2.Dnumber AND E_Female.Sex = 'F'\n" +
        "    ) AS Number_Female_Employees_Dept2\n" +
        "FROM department AS d1 \n" +
        "    CROSS JOIN department d2\n" +
        "WHERE d1.Dnumber != d2.Dnumber\n" +
        "    AND NOT EXISTS (\n" +
        "        SELECT *\n" +
        "        FROM project AS p3\n" +
        "        INNER JOIN works_on AS wo3 ON p3.Pnumber = wo3.Pno\n" +
        "        INNER JOIN employee AS e3 ON wo3.Essn = e3.Ssn\n" +
        "        WHERE e3.Sex = 'M' AND e3.Dno = d1.Dnumber\n" +
        "        AND NOT EXISTS (\n" +
        "            SELECT *\n" +
        "            FROM works_on AS wo4\n" +
        "            INNER JOIN employee AS e4 ON wo4.Essn = e4.Ssn\n" +
        "            WHERE e4.Sex = 'F' AND e4.Dno = d2.Dnumber\n" +
        "            AND wo4.Pno = p3.Pnumber\n" +
        "        )\n" +
        "    )\n" +
        "    AND NOT EXISTS (\n" +
        "        SELECT *\n" +
        "        FROM works_on AS wo5\n" +
        "        INNER JOIN employee AS e5 ON wo5.Essn = e5.Ssn\n" +
        "        WHERE e5.Sex = 'F' AND e5.Dno = d2.Dnumber\n" +
        "        AND NOT EXISTS (\n" +
        "           SELECT *\n" +
        "            FROM project AS p4\n" +
        "            INNER JOIN works_on AS wo6 ON p4.Pnumber = wo6.Pno\n" +
        "            INNER JOIN employee AS e6 ON wo6.Essn = e6.Ssn\n" +
        "            WHERE e6.Sex = 'M' AND e6.Dno = d1.Dnumber\n" +
        "            AND wo5.Pno = p4.Pnumber\n" +
        "        )\n" +
        "    )\n" +
        "    AND EXISTS (\n" +
        "        SELECT *\n" +
        "        FROM employee e7\n" +
        "        WHERE e7.Dno = d1.Dnumber AND e7.Sex = 'M'\n" +
        "    )\n" +
        "    AND EXISTS (\n" +
        "        SELECT *\n" +
        "        FROM employee e8\n" +
        "        WHERE e8.Dno = d2.Dnumber AND e8.Sex = 'F'\n" +
        "    )\n" +
        "ORDER BY \n" +
        "    d1.Dnumber, d2.Dnumber;";

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
                    EmployyePairs frame = new EmployyePairs();
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
    public EmployyePairs() {
        setTitle("Department Comparison Analyzer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1000, 600); // Increased width to accommodate more columns
        setLocationRelativeTo(null);
        
        // Simple panel with border
        contentPane = new JPanel();
        contentPane.setBorder(BorderFactory.createLineBorder(DARK_BLUE, 2));
        contentPane.setBackground(VERY_LIGHT_BLUE);
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));
        
        JPanel statementPanel = new JPanel();
        statementPanel.setBorder(BorderFactory.createLineBorder(DARK_BLUE, 1));
        statementPanel.setBackground(MEDIUM_BLUE);
        statementPanel.setPreferredSize(new Dimension(1000, 100)); // Aumentado para texto largo
        contentPane.add(statementPanel, BorderLayout.NORTH);
        statementPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Statement:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 0));
        statementPanel.add(titleLabel, BorderLayout.NORTH);
        String statementText = "Find all pairs of departments (d1, d2), where d1 has male employees and d2 has female employees, " +
                              "such that every project with male employees from d1 also has female employees from d2 " +
                              "working on it, and every project with female employees from d2 also has male employees " +
                              "from d1 working on it. Display department names, numbers, and employee counts.";
                              
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
        statementScrollPane.setPreferredSize(new Dimension(1000, 80));
        statementPanel.add(statementScrollPane, BorderLayout.CENTER);
        
        JPanel resultsPanel = new JPanel();
        resultsPanel.setBorder(BorderFactory.createLineBorder(DARK_BLUE, 1));
        resultsPanel.setBackground(VERY_LIGHT_BLUE);
        contentPane.add(resultsPanel, BorderLayout.CENTER);
        resultsPanel.setLayout(new BorderLayout());
        
        JLabel lblResult = new JLabel("Result:");
        lblResult.setFont(new Font("Arial", Font.BOLD, 14));
        lblResult.setForeground(DARK_BLUE);
        lblResult.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 0));
        resultsPanel.add(lblResult, BorderLayout.NORTH);
        
        String[] columnNames = {
            "First Dept Name", 
            "First Dept No", 
            "Male Employees", 
            "Second Dept Name", 
            "Second Dept No", 
            "Female Employees"
        };
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) return Integer.class; // First_Department_Number
                if (columnIndex == 2) return Integer.class; // Number_Male_Employees_Dept1
                if (columnIndex == 4) return Integer.class; // Second_Department_Number
                if (columnIndex == 5) return Integer.class; // Number_Female_Employees_Dept2
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
        header.setPreferredSize(new Dimension(header.getWidth(), 30)); // Altura aumentada
        
        tableResults.getColumnModel().getColumn(0).setPreferredWidth(150); // First Dept Name
        tableResults.getColumnModel().getColumn(1).setPreferredWidth(100); // First Dept No
        tableResults.getColumnModel().getColumn(2).setPreferredWidth(120); // Male Employees
        tableResults.getColumnModel().getColumn(3).setPreferredWidth(150); // Second Dept Name
        tableResults.getColumnModel().getColumn(4).setPreferredWidth(100); // Second Dept No
        tableResults.getColumnModel().getColumn(5).setPreferredWidth(120); // Female Employees
        
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
        
        tableResults.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); // First Dept No
        tableResults.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Male Employees
        tableResults.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Second Dept No
        tableResults.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // Female Employees
        
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
        controlPanel.setPreferredSize(new Dimension(1000, 60));
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
                fetchData();
            }
        });
        controlPanel.add(btnConnect);
        
        btnRefresh = createStyledButton("Refresh", DARK_BLUE, Color.WHITE);
        btnRefresh.setEnabled(false);
        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fetchData();
            }
        });
        controlPanel.add(btnRefresh);
        
        JButton btnExit = createStyledButton("Exit", DARK_BLUE, Color.WHITE);
        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        controlPanel.add(btnExit);
        
        JButton btnShowQuery = createStyledButton("Show SQL", DARK_BLUE, Color.WHITE);
        btnShowQuery.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showSqlQuery();
            }
        });
        controlPanel.add(btnShowQuery);
    }
    
    
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
    
   
    private void showSqlQuery() {
        JDialog dialog = new JDialog(this, "SQL Query", true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(VERY_LIGHT_BLUE);
        
        JTextArea txtQuery = new JTextArea(DEPARTMENT_COMPARISON_QUERY);
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
     * Connect to database and fetch data
     */
    private void fetchData() {
        // Clear existing data
        tableModel.setRowCount(0);
        txtStatus.setText("Connecting to database...");
        
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
                    rs = stmt.executeQuery(DEPARTMENT_COMPARISON_QUERY);
                    while (rs.next()) {
                        String firstDeptName = rs.getString("First_Department_Name");
                        int firstDeptNo = rs.getInt("First_Department_Number");
                        int maleCount = rs.getInt("Number_Male_Employees_Dept1");
                        String secondDeptName = rs.getString("Second_Department_Name");
                        int secondDeptNo = rs.getInt("Second_Department_Number");
                        int femaleCount = rs.getInt("Number_Female_Employees_Dept2");
                        
                        // Add row to table model
                        tableModel.addRow(new Object[] {
                            firstDeptName, firstDeptNo, maleCount,
                            secondDeptName, secondDeptNo, femaleCount
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
                    txtStatus.setText("Found " + tableModel.getRowCount() + " department pairs");
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