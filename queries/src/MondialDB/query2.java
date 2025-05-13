package MondialDB;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.text.NumberFormat;
import java.util.Vector;

public class query2 extends JFrame {
    private static final Color BACKGROUND_SOFT_LAVENDER = new Color(230, 230, 250);
    private static final Color HEADER_SOFT_PINK = new Color(255, 182, 193);
    private static final Color TEXT_MUTED_GRAY = new Color(105, 105, 105);
    private static final Color BUTTON_SOFT_BLUE = new Color(173, 216, 230);
    private static final Color BUTTON_TEXT_DARK_SLATE = new Color(47, 79, 79);
    private static final Color TABLE_HEADER_COLOR = new Color(200, 212, 255);
    private static final Color TABLE_ROW_ALTERNATE = new Color(240, 248, 255);

    private JTable resultTable;
    private JButton executeQueryButton;
    private JButton showStatementButton;
    private JButton refreshDataButton;
    private JLabel statusLabel;
    private JPanel controlPanel;
    
    private static final String DB_URL = "jdbc:mysql://dif-mysql.ehu.es:23306/DBI08";
    private static final String USER = "DBI08";
    private static final String PASS = "DBI08";

    public query2() {
        setTitle("GDP Comparison Across Borders");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(BACKGROUND_SOFT_LAVENDER);

        initComponents();
        setupLayout();

        setLocationRelativeTo(null);
    }

    private void initComponents() {
        resultTable = new JTable();
        resultTable.setBackground(Color.WHITE);
        resultTable.setForeground(TEXT_MUTED_GRAY);
        resultTable.setSelectionBackground(new Color(173, 216, 230, 100));
        resultTable.setRowHeight(25);

        JTableHeader header = resultTable.getTableHeader();
        header.setBackground(TABLE_HEADER_COLOR);
        header.setForeground(BUTTON_TEXT_DARK_SLATE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));

        resultTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : TABLE_ROW_ALTERNATE);
                }
                if (value instanceof Number) {
                    setText(NumberFormat.getNumberInstance().format(value));
                    setHorizontalAlignment(SwingConstants.RIGHT);
                }
                return c;
            }
        });

        executeQueryButton = createStyledButton("Execute Query", BUTTON_SOFT_BLUE);
        executeQueryButton.addActionListener(this::executeQuery);

        showStatementButton = createStyledButton("Show Statement", new Color(255, 228, 181)); // Light orange
        showStatementButton.addActionListener(this::showStatement);

        refreshDataButton = createStyledButton("Refresh Data", new Color(176, 224, 230)); // Lighter blue
        refreshDataButton.addActionListener(this::executeQuery);
        
        // Status Label
        statusLabel = new JLabel("Ready to execute query", SwingConstants.CENTER);
        statusLabel.setForeground(TEXT_MUTED_GRAY);
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(HEADER_SOFT_PINK);
        JLabel titleLabel = new JLabel("Countries with Highest GDP Among Neighbors");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(BUTTON_TEXT_DARK_SLATE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        controlPanel = new JPanel();
        controlPanel.setBackground(BACKGROUND_SOFT_LAVENDER);
        controlPanel.add(executeQueryButton);
        controlPanel.add(showStatementButton);
        controlPanel.add(refreshDataButton);
        controlPanel.add(statusLabel);
        add(controlPanel, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(BUTTON_TEXT_DARK_SLATE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        return button;
    }

    private void executeQuery(ActionEvent e) {
        String query = "SELECT c.Name, enc.Continent, e.GDP, e.Inflation, count(city.Name) as 'Amount of cities' " +
                "FROM country as c " +
                "JOIN economy as e ON c.Code=e.Country " +
                "JOIN encompasses AS enc ON c.Code=enc.Country " +
                "JOIN city ON c.Code=city.Country " +
                "WHERE NOT EXISTS ( " +
                "    SELECT b.Country1 " +
                "    FROM borders as b " +
                "    JOIN economy as ec ON (b.Country1=c.Code AND ec.Country=b.Country2) " +
                "        OR (b.Country2=c.Code AND ec.Country=b.Country1) " +
                "    WHERE ec.GDP >= e.GDP " +
                ") " +
                "GROUP BY c.Name, enc.Continent, e.GDP, e.Inflation";

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

            DefaultTableModel model = new DefaultTableModel(data, columnNames) {
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    if (columnIndex == 2) return Double.class;
                    if (columnIndex == 3) return Double.class;
                    if (columnIndex == 4) return Integer.class;
                    return String.class;
                }
            };
            resultTable.setModel(model);
            adjustColumnWidths();

            statusLabel.setText(data.size() + " countries found with highest GDP among neighbors");

            rs.close();
            pstmt.close();
            conn.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database Error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            statusLabel.setText("Query execution failed");
        }
    }

    private void showStatement(ActionEvent e) {
        String queryText = "SELECT c.Name, enc.Continent, e.GDP, e.Inflation, count(city.Name) as 'Amount of cities' \n" +
                "FROM country as c \n" +
                "JOIN economy as e ON c.Code=e.Country \n" +
                "JOIN encompasses AS enc ON c.Code=enc.Country \n" +
                "JOIN city ON c.Code=city.Country \n" +
                "WHERE NOT EXISTS ( \n" +
                "    SELECT b.Country1 \n" +
                "    FROM borders as b \n" +
                "    JOIN economy as ec ON (b.Country1=c.Code AND ec.Country=b.Country2) \n" +
                "        OR (b.Country2=c.Code AND ec.Country=b.Country1) \n" +
                "    WHERE ec.GDP >= e.GDP \n" +
                ") \n" +
                "GROUP BY c.Name, enc.Continent, e.GDP, e.Inflation";

        JTextArea textArea = new JTextArea(queryText);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(800, 300));

        JOptionPane.showMessageDialog(this, scrollPane, "SQL Statement", JOptionPane.INFORMATION_MESSAGE);
    }

    private void adjustColumnWidths() {
        TableColumnModel columnModel = resultTable.getColumnModel();
        int[] columnWidths = {150, 100, 100, 100, 150};
        for (int i = 0; i < columnModel.getColumnCount() && i < columnWidths.length; i++) {
            columnModel.getColumn(i).setPreferredWidth(columnWidths[i]);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new query2().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}