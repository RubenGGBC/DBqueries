package MondialDB;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;
import java.util.Vector;

public class query3 extends JFrame {
    // Pastel color theme consistent with other Mondial frames
    private static final Color PASTEL_BACKGROUND = new Color(253, 245, 230); // Soft peach
    private static final Color PASTEL_HEADER = new Color(255, 228, 196); // Bisque
    private static final Color PASTEL_TEXT = new Color(119, 136, 153); // Slate gray
    private static final Color PASTEL_BUTTON = new Color(221, 160, 221); // Plum
    private static final Color PASTEL_BUTTON_TEXT = new Color(75, 0, 130); // Indigo
    private static final Color PASTEL_TABLE_HEADER = new Color(255, 222, 173); // Light orange
    private static final Color PASTEL_SELECTION = new Color(176, 224, 230); // Powder blue

    private JTable resultTable;
    private JButton executeButton;
    private JButton showStatementButton;
    private JButton exportButton;
    private JLabel statusLabel;
    
    private static final String DB_URL = "jdbc:mysql://dif-mysql.ehu.es:23306/DBI08";
    private static final String USER = "DBI08";
    private static final String PASS = "DBI08";

    public query3() {
        setTitle("Province Population Analysis");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(PASTEL_BACKGROUND);

        initComponents();

        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PASTEL_HEADER);
        headerPanel.setPreferredSize(new Dimension(900, 80));
        headerPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Provinces with Cities Above Population Threshold");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(PASTEL_TEXT);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JLabel subtitleLabel = new JLabel("Analysis of provinces where all cities have at least 300 people");
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleLabel.setForeground(PASTEL_TEXT);
        subtitleLabel.setFont(new Font("Serif", Font.ITALIC, 16));
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Statement panel
        JPanel statementPanel = new JPanel();
        statementPanel.setBorder(BorderFactory.createLineBorder(PASTEL_TEXT, 1));
        statementPanel.setBackground(PASTEL_BUTTON);
        statementPanel.setPreferredSize(new Dimension(900, 80));
        statementPanel.setLayout(new BorderLayout());
        
        JLabel statementLabel = new JLabel(" Query Description:");
        statementLabel.setFont(new Font("Serif", Font.BOLD, 14));
        statementLabel.setForeground(PASTEL_BUTTON_TEXT);
        statementPanel.add(statementLabel, BorderLayout.NORTH);
        
        JTextArea statementText = new JTextArea(
            "This query selects all provinces such that all their cities have at least 300 people. For them, " +
            "it shows the amount of cities per province, and the average population on them. Also, it displays " +
            "the name of the city with the biggest amount of citizens in the province, along with the population amount."
        );
        statementText.setFont(new Font("Serif", Font.PLAIN, 14));
        statementText.setForeground(PASTEL_BUTTON_TEXT);
        statementText.setBackground(PASTEL_BUTTON);
        statementText.setWrapStyleWord(true);
        statementText.setLineWrap(true);
        statementText.setEditable(false);
        statementText.setMargin(new Insets(5, 10, 5, 10));
        statementPanel.add(statementText, BorderLayout.CENTER);
        
        add(statementPanel, BorderLayout.NORTH);

        // Results table
        resultTable = new JTable();
        resultTable.setBackground(Color.WHITE);
        resultTable.setForeground(PASTEL_TEXT);
        resultTable.setSelectionBackground(PASTEL_SELECTION);
        resultTable.setFont(new Font("Serif", Font.PLAIN, 14));
        resultTable.setRowHeight(25);
        resultTable.setIntercellSpacing(new Dimension(5, 0));
        resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Set alternating row colors
        resultTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (isSelected) {
                    c.setBackground(PASTEL_SELECTION);
                    c.setForeground(PASTEL_TEXT);
                } else {
                    c.setBackground(row % 2 == 0 ? new Color(240, 248, 255) : Color.WHITE);
                    c.setForeground(PASTEL_TEXT);
                }
                
                ((JLabel) c).setHorizontalAlignment(column > 0 ? JLabel.CENTER : JLabel.LEFT);
                
                return c;
            }
        });
        
        // Table header styling
        JTableHeader header = resultTable.getTableHeader();
        header.setBackground(PASTEL_TABLE_HEADER);
        header.setForeground(PASTEL_TEXT);
        header.setFont(new Font("Serif", Font.BOLD, 14));
        
        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(PASTEL_TEXT, 1));
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(PASTEL_BACKGROUND);
        buttonPanel.setPreferredSize(new Dimension(900, 60));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        // Status label
        statusLabel = new JLabel("Ready to execute query", JLabel.CENTER);
        statusLabel.setForeground(PASTEL_TEXT);
        statusLabel.setFont(new Font("Serif", Font.ITALIC, 14));
        statusLabel.setPreferredSize(new Dimension(250, 30));
        buttonPanel.add(statusLabel);
        
        executeButton = createStyledButton("Execute Query");
        executeButton.addActionListener(e -> executeQuery());
        buttonPanel.add(executeButton);
        
        showStatementButton = createStyledButton("Show SQL");
        showStatementButton.addActionListener(e -> showSQLStatement());
        buttonPanel.add(showStatementButton);
        
        exportButton = createStyledButton("Export Data");
        exportButton.addActionListener(e -> exportData());
        buttonPanel.add(exportButton);
        
        JButton closeButton = createStyledButton("Close");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(new Color(216, 191, 216)); // Pressed color
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(238, 130, 238)); // Hover color
                } else {
                    g2.setColor(PASTEL_BUTTON); // Default color
                }
                
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                g2.setColor(new Color(139, 0, 139, 50)); // Border
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                
                // Draw text
                g2.setFont(new Font("Serif", Font.BOLD, 14));
                g2.setColor(PASTEL_BUTTON_TEXT);
                
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int x = (getWidth() - textWidth) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(text, x, y);
                
                g2.dispose();
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(120, 40);
            }
        };
        
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }

    private void executeQuery() {
        statusLabel.setText("Executing query...");
        
        // Query for provinces where all cities have at least 300 people
        String query = "SELECT pr.name, pr.Population, pr.Country, COUNT(city.Name) AS 'Number of cities', " +
                      "AVG(city.Population) AS 'Average population', " +
                      "( " + 
                      "    SELECT Name " +
                      "    FROM city " +
                      "    WHERE city.Province=pr.Name AND city.Country = pr.Country " +
                      "    ORDER by city.Population DESC " +
                      "    LIMIT 1 " +
                      ") as 'City with most citizens', " +
                      "MAX(city.Population) AS 'Population in city' " +
                      "FROM province AS pr " +
                      "JOIN city ON pr.Name=city.Province AND pr.Country=city.Country " +
                      "WHERE NOT EXISTS ( " +
                      "    SELECT c.name " +
                      "    FROM city AS c " +
                      "    WHERE c.Province=pr.Name AND c.Country=pr.Country AND (c.Population<300 OR c.Population IS NULL) " +
                      ")  " +
                      "GROUP BY pr.name, pr.Population, pr.Country " +
                      "ORDER BY AVG(city.Population) ASC";

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            Vector<String> columnNames = new Vector<>();
            for (int column = 1; column <= columnCount; column++) {
                columnNames.add(metaData.getColumnName(column));
            }

            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                    row.add(rs.getObject(columnIndex));
                }
                data.add(row);
            }

            resultTable.setModel(new DefaultTableModel(data, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Make cells non-editable
                }
                
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    if (columnIndex == 1) return Integer.class; // Population
                    if (columnIndex == 3) return Integer.class; // Number of cities
                    if (columnIndex == 4) return Double.class;  // Average population
                    if (columnIndex == 6) return Integer.class; // Population in city
                    return String.class;
                }
            });
            
            // Format the columns
            DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
            rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
            resultTable.getColumnModel().getColumn(1).setCellRenderer(rightRenderer); // Population
            resultTable.getColumnModel().getColumn(3).setCellRenderer(rightRenderer); // Number of cities
            resultTable.getColumnModel().getColumn(4).setCellRenderer(rightRenderer); // Average population
            resultTable.getColumnModel().getColumn(6).setCellRenderer(rightRenderer); // Population in city
            
            // Center-align column headers
            ((DefaultTableCellRenderer)resultTable.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(JLabel.CENTER);

            statusLabel.setText("Query executed successfully. Found " + data.size() + " results.");
            
            rs.close();
            pstmt.close();
            conn.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Database Error: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            statusLabel.setText("Query execution failed.");
            ex.printStackTrace();
        }
    }
    
    private void showSQLStatement() {
        String queryText = "-- This query finds provinces where all cities have at least 300 people\n" +
                      "SELECT pr.name, pr.Population, pr.Country, COUNT(city.Name) AS 'Number of cities',\n" +
                      "AVG(city.Population) AS 'Average population',\n" +
                      "(\n" +
                      "    SELECT Name\n" +
                      "    FROM city\n" +
                      "    WHERE city.Province=pr.Name AND city.Country = pr.Country\n" +
                      "    ORDER by city.Population DESC\n" +
                      "    LIMIT 1\n" +
                      ") as 'City with most citizens',\n" +
                      "MAX(city.Population) AS 'Population in city'\n" +
                      "FROM province AS pr\n" +
                      "JOIN city ON pr.Name=city.Province AND pr.Country=city.Country\n" +
                      "WHERE NOT EXISTS (\n" +
                      "    SELECT c.name\n" +
                      "    FROM city AS c\n" +
                      "    WHERE c.Province=pr.Name AND c.Country=pr.Country AND (c.Population<300 OR c.Population IS NULL)\n" +
                      ")\n" +
                      "GROUP BY pr.name, pr.Population, pr.Country\n" +
                      "ORDER BY AVG(city.Population) ASC";
        
        JTextArea textArea = new JTextArea(queryText);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setBackground(PASTEL_BACKGROUND);
        textArea.setForeground(PASTEL_TEXT);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, "SQL Statement", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void exportData() {
        if (resultTable.getModel().getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, 
                "No data to export. Please execute the query first.", 
                "Export Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // This is a placeholder for export functionality
        JOptionPane.showMessageDialog(this, 
            "Data would be exported to CSV/Excel here.", 
            "Export Data", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new query3().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}