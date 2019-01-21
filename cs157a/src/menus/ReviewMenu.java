package menus;

import java.sql.*;
import java.util.ArrayList;

import classespackages.*;

public class ReviewMenu implements Menu {
	
	private final String menu = 
			"- - - VIEW OR WRITE A REVIEW - - -\n" +
			"1. View Reviews\n" +
			"2. Write a Review\n" +
			"3. Edit a Review\n" +
			"| Back [B] | Log Out [Q] |";
	//for editing a review, print out the old review & rating first before calling write methods
	private final MenuPages previous = MenuPages.SEARCHMOVIE;
	
	private PreparedStatement[] pstmts = new PreparedStatement[3];
	private Movie currMovie;
	private ResultSet rs = null;
	private ArrayList<Review> reviewList;
	
	@Override
	public String getMenuStr() {
		return menu;
	}

	@Override
	public MenuPages previous() {
		currMovie = null;
		reviewList = null;
		return previous;
	}
	
	public PreparedStatement[] getStatements() {
		return pstmts;
	}
	
	public void setMovie(Movie m) {
		currMovie = m;
	}
	
	public Movie getCurrMovie() {
		return currMovie;
	}
	
	public int getReviewListSize() {
		return reviewList.size();
	}
	
	public Review getReview(int index) {
		return reviewList.get(index);
	}
	
	/**
	 * Queries the DB for all reviews of a particular movie
	 * @param conn connection object from main method
	 * @throws SQLException
	 */
	public void viewReviewQuery(Connection conn) throws SQLException {
		
		String sql = "  SELECT rID, Users.uID, name, tconst, review, stars, IFNULL(sum(helpful), 0) AS reviewRating" + 
				"  FROM (TitleReviews join Users using(uID)) LEFT OUTER JOIN CommentReviews using(rID)" + 
				"  GROUP BY rID, tconst" + 
				"  HAVING tconst = ?;";
		pstmts[0] = conn.prepareStatement(sql);
		pstmts[0].setString(1, currMovie.getTconst());
		
		rs = pstmts[0].executeQuery();
		fillReviewList(); //updates currMovie's list of reviews
	}
	
	/**
	 * Helper method to load result set to currMovie's review list
	 * @throws SQLException
	 */
	private void fillReviewList() throws SQLException {
		reviewList = new ArrayList<Review>();
		while(rs.next()) {
			reviewList.add(
				new Review(rs.getInt("rID"),
					 rs.getInt("uID"), 
					 rs.getString("tconst"), 
					 rs.getString("review"), 
					 rs.getInt("stars"),
					 rs.getString("name"),
					 rs.getInt("reviewRating"))
				);
		}
	}
	
	/**
	 * Returns the menu of reviews to be selected from
	 * @return
	 */
	public String generateReviewList() {
		if(reviewList.size() == 0) {
			return "There are currently no reviews for this movie.\n\n";
		}
		
		String listStr = "";
		
		for(int i = 0; i < reviewList.size(); i++) {
			listStr = listStr + (i + 1) + ". " + reviewList.get(i).toString() + "\n\n";
		}
		
		return listStr;
	}
	
	/**
	 * Queries the DB to insert a review
	 * @param conn connection object from main method
	 * @param currUser reviewer's id
	 * @param txt the review
	 * @param stars the reviewer's star rating out of 10
	 * @return error identifying if query worked or not; 
	 * 1 for success, -1 for unexpected fail, 1062 for duplicate entry, 1146 for invalid input
	 */
	public int writeReviewQuery(Connection conn, User currUser, String txt, int stars) {
		int accept = -1;
		try {
			String sql = "INSERT INTO TitleReviews(uID, tconst, review, stars)" + 
					"  VALUES(?, ?, ?, ?);";
			pstmts[1] = conn.prepareStatement(sql);
			pstmts[1].setInt(1, currUser.getuID());
			pstmts[1].setString(2, currMovie.getTconst());
			pstmts[1].setString(3, txt);
			pstmts[1].setInt(4, stars);
			pstmts[1].executeUpdate();
			
			accept = 1;
		}
		catch(SQLException se) {
			while(se != null)
			{
				if(se.getErrorCode() == 1062) { /* duplicate entry */
					accept = 1062;
				}
				else if(se.getErrorCode() == 1146) { /* invalid input to stars */
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
	
	/**
	 * Queries the DB to edit a review
	 * @param conn connection object from main method
	 * @param currUser reviewer's id
	 * @param txt the review
	 * @param stars the reviewer's star rating out of 10
	 */
	public int editReviewQuery(Connection conn, User currUser, String txt, int stars) {
		int accept = -1;
		try {
			String sql = "UPDATE TitleReviews SET review = ?, stars = ? WHERE uID = ? AND tconst = ?";
			pstmts[2] = conn.prepareStatement(sql);
			pstmts[2].setString(1, txt);
			pstmts[2].setInt(2, stars);
			pstmts[2].setInt(3, currUser.getuID());
			pstmts[2].setString(4, currMovie.getTconst());
			int success = pstmts[2].executeUpdate();
			
			if(success == 1) accept = 1;
		}
		catch(SQLException se) {
			while(se != null)
			{
				if(se.getErrorCode() == 1146) { /* invalid input to stars */
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
