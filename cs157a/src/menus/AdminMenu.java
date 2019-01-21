package menus;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class AdminMenu implements Menu {
	private Scanner sc = new Scanner(System.in);
	private ResultSet myRs = null;
	private Statement myStmt = null;
	private CallableStatement cstmt1 = null;
	private CallableStatement cstmt2 = null;
	private Statement stmt1 = null;
	
	private final String menu = 
			"- - - ADMINISTRATOR ACTIONS - - -\n" +
			"1. View All User Activity\n" +
			"2. List All Users and Their Reviews\n" +
			"3. Delete a Review\n" +
			"4. Archive Users\n" +
			"| Back [B] | Log Out [Q] |";
	private final MenuPages previous = MenuPages.HOME;
	private ArrayList<String> userInfoList;

	
	@Override
	public String getMenuStr() {
		return menu;
	}

	@Override
	public MenuPages previous() {
		return previous;
	}

	/**
	 * delete a review tuple based on rID
	 * @param conn contains the current sql connection
	 * @throws SQLException
	 */
	public void deleteReview(Connection conn) throws SQLException {
		System.out.print("Select the rID you want to delete:");
		String rID = sc.next();
		String sql = "{call deleteReview(?)}";
		cstmt1 = conn.prepareCall(sql);
		cstmt1.setString(1,  rID);
		cstmt1.execute();
		if((cstmt1.getUpdateCount() == 1)){
			System.out.println("Deletion of comment with an rID:"+ rID +" is completed.");
		}else{
			System.out.println("No comment with a rID:" + rID + " has been deleted");
		}
	}

	/**
	 * grabs all the user and their reviews
	 * @param conn contains the current sql connection
	 */
	public void listReviews(Connection conn){
		try{
			myStmt = conn.createStatement();
			myRs = myStmt.executeQuery("SELECT uID, name, review, rID FROM Users LEFT OUTER JOIN TitleReviews using(uID);");
			printlistReviews(myRs);
		}
		catch(Exception exc){
			exc.printStackTrace();
		}
	}

	/**
	 * prints results
	 * @param mys contains the result set
	 * @throws Exception
	 */
	private void printlistReviews(ResultSet mys)throws Exception {
		int incrementor = 0;
		String previousuID = "";
		while (mys.next()) {
			String uID = mys.getString("uID") == null ? "" : mys.getString("uID");
			String rID = mys.getString("rID") == null ? "" : "* rID:" + mys.getString("rID");
			String review = mys.getString("review") == null ? "This user has not done any reviews yet..." : rID + "\n\t\t- "+ mys.getString("review");
			if (!previousuID.equals(uID)) {
				String name = mys.getString("name") == null ? "" : mys.getString("name");
				previousuID = uID;
				incrementor++;
				System.out.println(incrementor + ".)name:" + name  + "\n\t" + review);
			}else{
				System.out.println("\t" + review);
			}
		}
	}

	public ArrayList<String> getUserInfo () {
		return userInfoList;
	}

	/**
	 * Checks all user's total amount of posted reviews and comments and uses helper method to put it into userInfoList
	 * @param conn
	 * @throws SQLException
	 */
	public void checkUserInfo(Connection conn) throws SQLException {
		String sql = null;
		ResultSet rs = null;
		sql = "SELECT uID, count(*) AS activity, lastLoggedIn " + 
				"FROM (SELECT uID FROM TitleReviews UNION ALL SELECT uID FROM CommentReviews) AS R join Users using(uID) " + 
				"GROUP BY uID, lastLoggedIn;";
		Statement stmt = conn.createStatement();
		stmt1 = stmt;
		rs = stmt.executeQuery(sql);
		fillUserInfoList(rs);
	}

	/**
	 * Helper method that fills Arraylist userInfoList with user's information from a result set
	 * @param rs Result set that is in the format of uID, count(*) as activity
	 * @throws SQLException
	 */
	private void fillUserInfoList(ResultSet rs) throws SQLException {
		userInfoList = new ArrayList<String>();
		while(rs.next()) {
			String info = "User ID: " + rs.getString("uID") + ", Total reviews and comments: "+ rs.getInt("activity") +
					", Last logged in: " + rs.getTimestamp("lastLoggedIn");
			userInfoList.add(info);
		}
	}
	
	/**
	 * Calls the archiveUsers stored procedure on all users who have not logged since a certain cutoff year
	 * @param conn
	 * @param cutoffyr
	 * @return updated the number of users archived 
	 * @throws SQLException
	 */
	public int archiveUsers(Connection conn, int cutoffyr) {
		int updated = -1;
		try {
			cstmt2 = conn.prepareCall("{CALL archiveUsers(?)}");
			cstmt2.setInt(1, cutoffyr);
			cstmt2.execute();
			updated = cstmt2.getUpdateCount();
		}
		catch(SQLException se) {
			System.out.println("ERROR: " + se.getErrorCode() + " STATE: " + se.getSQLState()); 
			System.out.println(se.getMessage() + "\n");
			se = se.getNextException();
		}
		return updated;
		
	}
	
	public Statement[] getStatements() {
		Statement[] stmts = new Statement[4];
		stmts[0] = stmt1;
		stmts[1] = myStmt;
		stmts[2] = cstmt1;
		stmts[3] = cstmt2;
		return stmts;
	}
}
