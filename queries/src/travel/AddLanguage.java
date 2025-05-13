package travel;

import java.sql.*;
import java.util.Scanner;
import javax.swing.JFrame;

public class AddLanguage extends JFrame {
    private static final String DB_URL = "jdbc:mysql://dif-mysql.ehu.es:23306/DBI08";
    private static final String USER = "DBI08";
    private static final String PASS = "DBI08";

    public static void main(String[] args) {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        Scanner sc = new Scanner(System.in);

        try {
            System.out.println("Introduce the GuideId you want to add a language to:");
            String guideId = sc.next();

            System.out.println("Introduce the name of the language you want to add:");
            String language = sc.next();

            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // Disable auto-commit
            conn.setAutoCommit(false);

            // Prepare update statement
            preparedStatement = conn.prepareStatement("insert into languages values (?,?)");
            preparedStatement.setString(1, guideId);
            preparedStatement.setString(2, language);

            // Execute update
            preparedStatement.executeUpdate();

            // Commit changes
            conn.commit();
            System.out.println("The transaction was committed successfully");

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                    System.out.println("The guideId might not be in the list of tourguides or that language was already assigned to that guide");
                    System.out.println("Try again with different values.");
                    System.out.println("Possible values of GuideId: from 72515633 to 72515657");
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