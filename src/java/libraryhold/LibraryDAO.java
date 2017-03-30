// Giorgio Latour
// Library Holds Servlet
// IHRTLUHC
package libraryhold;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LibraryDAO {

    Statement stmt;
    Connection connection;
    PreparedStatement check_item_held_pstmt;
    PreparedStatement hold_item_pstmt;
    PreparedStatement get_PatronNum_pstmt;
    PreparedStatement get_Number_Holds_pstmt;

    public ResultSet searchItems(String keyword) {
        ResultSet rset = null;
        try {
            rset = stmt.executeQuery("select iditem, title from "
                    + "item where title like \"%" + keyword + "%\"");

            return rset;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return rset;
    }

    public Connection getConnection() {
        return connection;
    }

    public void HoldItem(String iditem, int patronNum) {
        try {
            hold_item_pstmt.setString(1, iditem);
            hold_item_pstmt.setString(2, Integer.toString(patronNum));

            hold_item_pstmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int getPatronNum(String patron) {
        int PatronNum = 0;
        try {
            get_PatronNum_pstmt.setString(1, patron);

            ResultSet rset = get_PatronNum_pstmt.executeQuery();

            if (rset.next()) {
                PatronNum = rset.getInt(1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return PatronNum;
    }

    public int getNumberOfHolds(String iditem) {
        int numberOfHolds = 0;
        try {
            get_Number_Holds_pstmt.setString(1, iditem);

            ResultSet rset = get_Number_Holds_pstmt.executeQuery();

            if (rset.next()) {
                numberOfHolds = rset.getInt(1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return numberOfHolds;
    }

    public boolean checkIfItemIsAlreadyHeld(String iditem, String patron) {
        boolean held = false;
        try {
            //System.out.println("iditem = " + iditem + " patron= " + getPatronNum(patron));

            check_item_held_pstmt.setString(1, iditem);
            check_item_held_pstmt.setString(2, Integer.toString(getPatronNum(patron)));

            ResultSet rset = check_item_held_pstmt.executeQuery();

            while (rset.next()) {
                String str = rset.getString(1);
                //System.out.println(str);
                if (!rset.getString(1).equalsIgnoreCase("0")) {
                    held = true;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return held;
    }

    public void initDB() {
        try {
            // Load JDBC.
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Driver loaded.");

            // Establish connection.
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost/libraryholds", "root", "cmsc250");
            
            // Create statement.
            stmt = connection.createStatement();
            
            String getNumberOfHoldsSQL = "select COUNT(*) from hold where item = ?";
            get_Number_Holds_pstmt = connection.prepareStatement(getNumberOfHoldsSQL);

            String checkItemHeldSQL = "select COUNT(*) from hold where item = ? and patron = ?;";
            check_item_held_pstmt = connection.prepareStatement(checkItemHeldSQL);

            String holdItemSQL = "insert into hold (item, patron) "
                    + "values (?, ?)";
            hold_item_pstmt = connection.prepareStatement(holdItemSQL);

            String getPatronNumSQL = "select idpatron from patron where card = ?";
            get_PatronNum_pstmt = connection.prepareStatement(getPatronNumSQL);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
