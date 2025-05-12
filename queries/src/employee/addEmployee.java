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
import java.util.Scanner;

public class addEmployee extends JFrame {
	private static final String DB_URL = "jdbc:mysql://dif-mysql.ehu.es:23306/DBI08";
    private static final String USER = "DBI08";
    private static final String PASS = "DBI08";
    
    
	public static void main(String[] args) throws SQLException{
		
		Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        Scanner sc = new Scanner(System.in);
        String newFname;
        String newLname;
        String newSsn;
        int newDno;
        PreparedStatement preparedStatement = null;
        
        System.out.println("Introduce the First name of the employee:");
        newFname = sc.next();
        
        System.out.println("Introduce the Last name of the employee:");
        newLname = sc.next();
        
        System.out.println("Introduce the Ssn of the employee:");
        newSsn = sc.next();
        
        System.out.println("Introduce the Department number of the employee:");
        newDno = sc.nextInt();
        
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
        
            //Disable auto-commit
        	conn.setAutoCommit(false);
        	
        	//Add new row in employee table
        	preparedStatement = conn.prepareStatement("insert into employee values (?,null,?,?,null,null,null,null,null,?)");
        	
        	preparedStatement.setString(1, newFname);
        	preparedStatement.setString(2, newLname);
        	preparedStatement.setString(3, newSsn);
        	preparedStatement.setInt(4, newDno);
        	preparedStatement.executeUpdate();
        	
        	//Commit changes
        	conn.commit();
        	System.out.println("The transaction was commited succesfully");

            
        } catch (SQLException e) {
        	try{
        		//Rollback the transaction
        		conn.rollback();
        		System.out.println("The transaction was rollback");
        	}
        	catch (SQLException e2){
        		System.out.println("There has been a problem when doing the rollback");
        	}

        }
	
        	catch (ClassNotFoundException e) {
            e.printStackTrace();
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
	}

}