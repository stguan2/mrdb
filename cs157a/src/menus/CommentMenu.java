package menus;

import java.sql.*;
import classespackages.*;

public class CommentMenu implements Menu {
	
	private final String menu = 
			"- - - REPLY WITH A COMMENT - - -\n" +
			"1. Write a Comment\n" +
			"2. Edit Old Comment\n" +
			"| Back [B] | Log Out [Q] |";
	private final MenuPages previous = MenuPages.REVIEW;
	
	private PreparedStatement[] pstmts = new PreparedStatement[3];
	private ResultSet rs = null;
	private Review currReview;
	
	
	@Override
	public String getMenuStr() {
		return menu;
	}

	@Override
	public MenuPages previous() {
		currReview = null;
		return previous;
	}
	
	public Review getReview() {
		return currReview;
	}
	
	public void setReview(Review r) {
		currReview = r;
	}
	
	public PreparedStatement[] getStatements() {
		return pstmts;
	}
	
	public void viewCommentQuery(Connection conn) throws SQLException {
		String sql = "  SELECT cID, rID, uID, comment, name" + 
				"  FROM CommentReviews join Users using(uID)" + 
				"  WHERE rID = ?;";
		pstmts[0] = conn.prepareStatement(sql);
		pstmts[0].setInt(1, currReview.getrID());
		rs = pstmts[0].executeQuery();
		
		fillCommentList();
	}
	
	public void fillCommentList() throws SQLException {
		while(rs.next()) {
			currReview.addComment(
					new Comment(rs.getInt("cID"),
							rs.getInt("rID"),
							rs.getInt("uID"),
							rs.getString("comment"),
							rs.getString("name")
							));
		}
	}
	
	public int writeCommentQuery(Connection conn, User currUser, String txt, int helpful) {
		int accept = -1;
		try {
			String sql = "INSERT INTO CommentReviews(uID, rID, comment, helpful)" +
					  " VALUES(?, ?, ?, ?);";
			pstmts[1] = conn.prepareStatement(sql);
			pstmts[1].setInt(1, currUser.getuID());
			pstmts[1].setInt(2, currReview.getrID());
			pstmts[1].setString(3, txt);
			pstmts[1].setInt(4, helpful);
			pstmts[1].executeUpdate();
			
			accept = 1;
		}
		catch(SQLException se) {
			while(se != null)
			{
				if(se.getErrorCode() == 1062) { /* duplicate entry */
					accept = 1062;
				}
				else if(se.getErrorCode() == 1146) { /* invalid input to helpful */
					accept = 1146;
				}
				else {
					System.out.println("ERROR: " + se.getErrorCode() + " STATE: " + se.getSQLState()); 
					System.out.println(se.getMessage() + "\n"); 
				}
				se = se.getNextException();
			}
		}
			
		return accept;
	}
	
	public int editCommentQuery(Connection conn, User currUser, String txt, int helpful) {
		int accept = -1;	
		try {
			String sql = "UPDATE CommentReviews SET comment = ?, helpful = ? WHERE uID = ? AND rID = ?";
			pstmts[2] = conn.prepareStatement(sql);
			pstmts[2].setString(1, txt);
			pstmts[2].setInt(2, helpful);
			pstmts[2].setInt(3, currUser.getuID());
			pstmts[2].setInt(4, currReview.getrID());
			int success = pstmts[2].executeUpdate();
			
			if(success == 1) accept = 1;
		}
		catch(SQLException se) {
			while(se != null)
			{
				if(se.getErrorCode() == 1146) { /* invalid input to helpful */
					accept = 1146;
				}
				else {
					System.out.println("ERROR: " + se.getErrorCode() + " STATE: " + se.getSQLState()); 
					System.out.println(se.getMessage() + "\n"); 
				}
				se = se.getNextException();
			}
		}
		
		return accept;
	}

}
