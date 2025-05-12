package employee;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.text.NumberFormat;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.util.Locale;

public class EmployeeProjectAnalyzer extends JFrame {
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
    private static final String EMPLOYEE_PROJECT_QUERY = 
        "SELECT e.Fname, e.Lname, d.Dname, e.Dno,\n" +
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
                    EmployeeProjectAnalyzer frame = new EmployeeProjectAnalyzer();
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
    public EmployeeProjectAnalyzer() {
        setTitle("Employee Project and Dependent Analysis");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 600);
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
        statementPanel.setPreferredSize(new Dimension(800, 100)); // Aumentado de 80 a 100
        contentPane.add(statementPanel, BorderLayout.NORTH);
        statementPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Statement:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 0));
        statementPanel.add(titleLabel, BorderLayout.NORTH);
        
        String statementText = "1- Retrieve the employee Fname and Lname, its department name and number, the number of projects " +
                              "they work on and the number of dependents they have, of all the employees that all the projects " +
                              "they work on corresponds to their department and where those employees have at least a son or " +
                              "a daughter as a dependent. The result Will be sorted descenctly by the number of projects and " +
                              "in case of tie, by the number of dependendents.";
                              
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
        statementScrollPane.setPreferredSize(new Dimension(800, 80));
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
        tableResults.setFont(new Font("Arial", Font.PLAIN, 12));
        tableResults.setRowHeight(25);
        tableResults.setGridColor(MEDIUM_BLUE);
        tableResults.setSelectionBackground(LIGHT_BLUE);
        tableResults.setSelectionForeground(DARK_BLUE);
        
        JTableHeader header = tableResults.getTableHeader();
        header.setBackground(MEDIUM_BLUE);
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Arial", Font.BOLD, 12));
        header.setBorder(BorderFactory.createLineBorder(DARK_BLUE, 1));
        header.setPreferredSize(new Dimension(header.getWidth(), 30)); // Altura aumentada
        
        tableResults.getColumnModel().getColumn(0).setPreferredWidth(100); // First Name
        tableResults.getColumnModel().getColumn(1).setPreferredWidth(100); // Last Name
        tableResults.getColumnModel().getColumn(2).setPreferredWidth(150); // Department
        tableResults.getColumnModel().getColumn(3).setPreferredWidth(80);  // Dept No
        tableResults.getColumnModel().getColumn(4).setPreferredWidth(80);  // Projects
        tableResults.getColumnModel().getColumn(5).setPreferredWidth(100); // Dependents
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
       
        
        tableResults.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(table.getSelectionForeground()); // Usa los valores configurados por defecto
                } else {
                    c.setBackground(row % 2 == 0 ? VERY_LIGHT_BLUE : Color.WHITE);
                    c.setForeground(Color.BLACK); // Asegura que el texto sea negro y visible
                }

                ((JLabel) c).setHorizontalAlignment(JLabel.CENTER);
                ((JComponent) c).setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 230, 240), 1),
                    BorderFactory.createEmptyBorder(2, 5, 2, 5)
                ));

                return c;
            }
        });


        
        tableResults.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Dept No
        tableResults.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Projects
        tableResults.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // Dependents
        
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
        controlPanel.setPreferredSize(new Dimension(800, 60));
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