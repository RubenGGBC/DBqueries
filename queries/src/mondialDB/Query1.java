package mondialDB;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class Query1 extends JFrame {
    private static final Color PASTEL_BACKGROUND = new Color(253, 245, 230); // Soft peach
    private static final Color PASTEL_HEADER = new Color(255, 228, 196); // Bisque
    private static final Color PASTEL_TEXT = new Color(119, 136, 153); // Slate gray
    private static final Color PASTEL_BUTTON = new Color(221, 160, 221); // Plum
    private static final Color PASTEL_BUTTON_TEXT = new Color(75, 0, 130); // Indigo

    private JTable resultTable;
    private JButton executeButton;
    private JButton showSQLButton;

    private static final String QUERY = "SELECT Country1, Country2 " +
            "FROM borders AS b \n" +
            "JOIN encompasses AS enc1 ON Country1 = enc1.Country AND enc1.Continent = 'Europe' \n" +
            "JOIN encompasses AS enc2 ON Country2 = enc2.Country AND enc2.Continent = 'Europe' \n" +
            "WHERE ( \n" +
            "    SELECT SUM(r.Length) \n" +
            "    FROM country AS c \n" +
            "    INNER JOIN geo_river AS gr ON c.Code = gr.Country \n" +
            "    INNER JOIN river AS r ON gr.River = r.Name \n" +
            "    WHERE c.Code = b.Country1 \n" +
            ") > 6000 \n" +
            "AND ( \n" +
            "    SELECT SUM(r.Length) \n" +
            "    FROM country AS c \n" +
            "    INNER JOIN geo_river AS gr ON c.Code = gr.Country \n" +
            "    INNER JOIN river AS r ON gr.River = r.Name \n" +
            "    WHERE c.Code = b.Country2 \n" +
            ") > 6000";

    public Query1() {
        setTitle("European Countries with Long Rivers");
        setSize(800, 600);
        getContentPane().setBackground(PASTEL_BACKGROUND);

        initComponents();

        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PASTEL_HEADER);
        JLabel titleLabel = new JLabel("European Countries with Rivers > 6000 km");
        titleLabel.setForeground(PASTEL_TEXT);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 18));
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        resultTable = new JTable();
        resultTable.setBackground(Color.WHITE);
        resultTable.setForeground(PASTEL_TEXT);
        resultTable.setSelectionBackground(new Color(176, 224, 230)); // Powder blue
        JScrollPane scrollPane = new JScrollPane(resultTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(PASTEL_BACKGROUND);
        
        executeButton = new JButton("Execute Query");
        executeButton.setBackground(PASTEL_BUTTON);
        executeButton.setForeground(PASTEL_BUTTON_TEXT);
        executeButton.addActionListener(e -> executeQuery());
        buttonPanel.add(executeButton);
        
        showSQLButton = new JButton("Show SQL");
        showSQLButton.setBackground(PASTEL_BUTTON);
        showSQLButton.setForeground(PASTEL_BUTTON_TEXT);
        showSQLButton.addActionListener(e -> showSQLStatement());
        buttonPanel.add(showSQLButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void executeQuery() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://dif-mysql.ehu.es:23306/DBI08 ","DBI08", "DBI08");
            PreparedStatement pstmt = conn.prepareStatement(QUERY);
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

            resultTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));

            rs.close();
            pstmt.close();
            conn.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Database Error: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void showSQLStatement() {
        JTextArea textArea = new JTextArea("Select all european frontiers (i.e. borders between two european countries) \n"
        		+ "such that both countries have at least 6000km of rivers:\n\n" + QUERY);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setBackground(PASTEL_BACKGROUND);
        textArea.setForeground(PASTEL_TEXT);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(700, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, "SQL Statement", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                new Query1().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}