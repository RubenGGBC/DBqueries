package employee;

import java.sql.*;
import java.util.Scanner;
import javax.swing.JFrame;

public class updateEmployee extends JFrame {
    private static final String DB_URL = "jdbc:mysql://dif-mysql.ehu.es:23306/DBI08";
    private static final String USER = "DBI08";
    private static final String PASS = "DBI08";

    public static void main(String[] args) {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        Scanner sc = new Scanner(System.in);

        try {
            System.out.println("Introduce the name of the project you want to modify:");
            String pname = sc.next();

            System.out.println("Introduce the Department number of the employee:");
            int newDno = sc.nextInt();

            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // Disable auto-commit
            conn.setAutoCommit(false);

            // Prepare update statement
            String sql = "UPDATE project SET Dnum = ? WHERE Pname = ?";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, newDno);
            preparedStatement.setString(2, pname);

            // Execute update
            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated == 0) {
                System.out.println("No project found with the name: " + pname);
            } else {
                System.out.println("Updated " + rowsUpdated + " row(s) successfully.");
            }

            // Commit changes
            conn.commit();
            System.out.println("The transaction was committed successfully");

        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                    System.out.println("The transaction was rolled back");
                }
            } catch (SQLException e2) {
                System.out.println("Error during rollback: " + e2.getMessage());
            }
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found: " + e.getMessage());
        } finally {
            // Close resources
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}
