package db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import classespackages.*;
import java.sql.*;
import menus.*;

public class DBTester {
	
	private static Scanner sc;
	private static final String GOODBYE_STR = "Goodbye.";	
	private static final String EXITING_STR = "Shutting down...";
	private static boolean done = false; //whether the application is done
	private static boolean legal = false; //whether a user's input is an accepted string
	private static MenuPages currMenu = MenuPages.WELCOME;

	// JDBC driver name and database URL
	private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";  
	private static final String DB_URL = "jdbc:mysql://localhost:3306/mrdb";

	//  Database credentials
	private static final String USER = "root";
	private static final String PASS = "011312041";
	
	private static Connection conn = null;
	private static User loggedInUser;
	
	private static CallableStatement c = null;
	public static void main(String[] args)
	{
		ArrayList<Statement> stmts = new ArrayList<Statement>(); //array list containing all used stmts
		stmts.add(c);
		//from Welcome
		stmts.addAll(Arrays.asList(
				((WelcomeMenu)MenuPages.WELCOME.getMenu()).getPreparedStatements()));
		//from Admin
		stmts.addAll(Arrays.asList(
				(((AdminMenu) MenuPages.ADMIN.getMenu()).getStatements())));
		//from ChangeAccInfo
		stmts.addAll(Arrays.asList(
				((ChangeAccInfoMenu)MenuPages.CHANGEACCINFO.getMenu()).getPreparedStatements()));
		//from SearchMovie
		stmts.addAll(Arrays.asList(
				((SearchMovieMenu)MenuPages.SEARCHMOVIE.getMenu()).getStatements()));
		//from Review
		stmts.addAll(Arrays.asList(
				((ReviewMenu)MenuPages.REVIEW.getMenu()).getStatements()));
		//from Comments
		stmts.addAll(Arrays.asList(
				((CommentMenu)MenuPages.COMMENT.getMenu()).getStatements()));		

		sc = new Scanner(System.in);
		
		try {
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			while(!done)
			{
				System.out.println(currMenu.getMenu().getMenuStr());
			
				if(currMenu == MenuPages.WELCOME)
					welcomeActions(sc.nextLine());
				else if(currMenu == MenuPages.HOME)
					homeActions(sc.nextLine());
				else if(currMenu == MenuPages.ADMIN)
					adminActions(sc.nextLine());
				else if(currMenu == MenuPages.CHANGEACCINFO)
					changeAccInfoActions(sc.nextLine());
				else if(currMenu == MenuPages.SEARCHMOVIE)
					searchMovieActions(sc.nextLine());
				else if(currMenu == MenuPages.REVIEW)
					reviewActions(sc.nextLine());
				else if(currMenu == MenuPages.COMMENT)
					commentActions(sc.nextLine());
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			try { 
				if(conn != null) conn.close();
				
				for(Statement s : stmts)
					if(s != null) s.close();
			}
			catch(SQLException se){ se.printStackTrace(); }
			sc.close();	
		}
		
	}
	
	/**
	 * Prints exiting string and ends the program.
	 */
	private static void exit()
	{
		legal = true; done = true;
		System.out.println(EXITING_STR);
	}

	/**
	 * Prints goodbye and returns to the Welcome menu
	 */
	private static void logOut()
	{
		legal = true;
		System.out.println(GOODBYE_STR);
		currMenu = MenuPages.WELCOME;
	}
	
	/**
	 * Called when user enters incorrect input. Allows the menus to keep looping with
	 * new user input.
	 * @return the new input
	 */
	private static String getNextInput()
	{
		legal = false;
		return sc.nextLine();
	}
	
	/**
	 * Either logs the user in or allows user to create a new account
	 * @param in user input
	 */
	private static void welcomeActions(String in) throws SQLException
	{
		do {
			legal = true;
			
			switch(in) {
				case "Q":
				{
					exit();
					break;
				}
				case "1":
				{
					//Log In
					System.out.println("Username:");
					String username = sc.nextLine();
					System.out.println("Password:");
					String password = sc.nextLine();
					ResultSet rs = ((WelcomeMenu)currMenu.getMenu()).checkLogin(conn, username, password);
					if(rs.next()) {
						loggedInUser = new User(rs.getInt("uID"), rs.getString("name"), username, password, rs.getBoolean("admin"));
						
						String sql = "{call userLogsIn(?)}";
						c = conn.prepareCall(sql);
						c.setInt(1,rs.getInt("uID"));
						c.execute();
						
						((HomeMenu)MenuPages.HOME.getMenu()).setAdmin(loggedInUser.isAdmin());
						currMenu = MenuPages.HOME;
					}
					else {
						System.out.println("The username and password combination is incorrect.");
					}
					break;
				}
				case "2":
				{
					Boolean checkUsername = null;
					System.out.println("Username:");
					String username = sc.nextLine();
					//checkUsername is true when database contains username.
					//else it is false.
					checkUsername = ((WelcomeMenu)currMenu.getMenu()).checkUsername(conn, username);
					while(checkUsername) {
						System.out.println("Username already exists. Please choose another username:");
						username = sc.nextLine();
						checkUsername = ((WelcomeMenu)currMenu.getMenu()).checkUsername(conn, username);
					}
					System.out.println("Password:");
					String password = sc.nextLine();
					System.out.println("Name:");
					String name = sc.nextLine();
					((WelcomeMenu)currMenu.getMenu()).createAccount(conn, username, password, name);
					System.out.println("Successfully created account.");
					break;
				}
				default:
				{
					in = getNextInput();
					break;
				}
			}
		} while(!legal);
	}
	
	/**
	 * Selects either search for movie or change acc information from Home menu
	 * @param in user input
	 */
	private static void homeActions(String in)
	{
		do {
			legal = true;
			if(in.equals("Q")) logOut();
			else if(in.equals("1")) currMenu = MenuPages.SEARCHMOVIE;
			else if(in.equals("2")) currMenu = MenuPages.CHANGEACCINFO;
			else if(in.equals("3") && loggedInUser.isAdmin()) currMenu = MenuPages.ADMIN;
			else in = getNextInput();
		} while(!legal);
	}
	
	
	private static void adminActions(String in) throws SQLException
	{
		do {
			legal = true;
			
			switch(in) {
				case "Q":
				{
					exit();
					break;
				}
				case "B":
				{
					currMenu = currMenu.getMenu().previous();
					break;
				}
				case "1":
				{
					((AdminMenu)currMenu.getMenu()).checkUserInfo(conn);
					ArrayList<String> info = ((AdminMenu)currMenu.getMenu()).getUserInfo();
					for (int i = 0;i<info.size();i++) {
						System.out.println(info.get(i));
					}
					break;
				}
				case "2":
				{
					((AdminMenu)currMenu.getMenu()).listReviews(conn);
					break;
				}
				case "3":
				{
					((AdminMenu)currMenu.getMenu()).deleteReview(conn);
					break;
				}
				case "4":
				{
					System.out.println("Enter the cutoff year: ");
					String cutoffInput = null;
					do {
						cutoffInput = sc.nextLine();
					}
					while(!cutoffInput.matches("\\d{4}"));
					
					System.out.println("All users who have not logged on since this year will be archived. Confirm? [Y/N]");
					String confirmInput = sc.nextLine();
					
					int archived = 0;
					if(confirmInput.equals("Y")) {
						archived = ((AdminMenu)MenuPages.ADMIN.getMenu()).archiveUsers(conn, Integer.parseInt(cutoffInput));
						if(archived < 0)
							System.out.println("Archiving failed. Please try again.");
						else
							System.out.println("Archived " + archived + " user accounts.");
					}
					
					System.out.println("Sending back to admin menu...\n");
					break;
				}
				default:
				{
					in = getNextInput();
					break;
				}
			}
		} while(!legal);
	}
	
	/**
	 * Selects change name or change password from Change Account Information menu
	 * @param in user input
	 */
	private static void changeAccInfoActions(String in) throws SQLException
	{
		do {
			legal = true;
			
			switch(in)
			{
				case "Q":
				{
					logOut();
					break;
				}
				case "B":
				{
					currMenu = currMenu.getMenu().previous();
					break;
				}
				case "1": //View account information
				{
					//Pulls information from Users class (only displays name and username)
					System.out.println("name: " + loggedInUser.getName());
					System.out.println("username: " + loggedInUser.getUsername());
					currMenu = MenuPages.CHANGEACCINFO;
					break;
				}
				case "2": //Change name
				{
					System.out.println("New name: ");
					String newName = sc.nextLine();
					//update DB
					Boolean bool = ((ChangeAccInfoMenu)currMenu.getMenu()).editName(conn, newName, loggedInUser);
					//update User class if DB is successfully updated.
					if (bool) {
						loggedInUser.setName(newName);
						System.out.println("Successfully changed name.");
					}
					else {
						System.out.println("Failed to change name.");
					}
					currMenu = MenuPages.CHANGEACCINFO;
					break;
				}
				case "3": //Change password
				{
					System.out.println("New password: ");
					String newPW = sc.nextLine();
					//update DB
					Boolean bool = ((ChangeAccInfoMenu)currMenu.getMenu()).editPass(conn, newPW, loggedInUser);
					//update User class if DB is successfully updated.
					if (bool) {
						loggedInUser.setPassword(newPW);
						System.out.println("Successfully changed password.");
					}
					else {
						System.out.println("Failed to change password.");
					}
					currMenu = MenuPages.CHANGEACCINFO;
					break;
				}
				default:
				{
					in = getNextInput();
					break;
				}
			}
		} while(!legal);
	}
	
	/**
	 * Queries database for movies based on user input
	 * @param in user input
	 */
	private static void searchMovieActions(String in)
	{
		ReviewMenu rm = (ReviewMenu)MenuPages.REVIEW.getMenu();
		SearchMovieMenu sm = (SearchMovieMenu)MenuPages.SEARCHMOVIE.getMenu();
		do {
			legal = true;
			
			switch(in)
			{
				case "Q":
				{
					logOut();
					break;
				}
				case "B":
				{
					currMenu = currMenu.getMenu().previous();
					break;
				}
				case "1": 
				{
					//Search by title
					rm.setMovie(sm.searchbytitle(conn,sc));
					if(rm.getCurrMovie() != null)
						currMenu = MenuPages.REVIEW;
					break;
				}
				case "2":
				{
					//Search by rating
					rm.setMovie(sm.searchbyrating(conn,sc));
					if(rm.getCurrMovie() != null)
						currMenu = MenuPages.REVIEW;
					break;
				}
				case "3":
				{
					//Search by genre
					rm.setMovie(sm.searchbygenre(conn,sc));
					if(rm.getCurrMovie() != null)
						currMenu = MenuPages.REVIEW;
					break;
				}
				case "4":
				{
					//Search by year
					rm.setMovie(sm.searchbyyear(conn,sc));
					if(rm.getCurrMovie() != null)
						currMenu = MenuPages.REVIEW;
					break;
				}
				case "5":
				{
					//Search by reviewed
					rm.setMovie(sm.reviewedMovies(conn,sc));
					if(rm.getCurrMovie() != null)
						currMenu = MenuPages.REVIEW;
					break;
				}
				default:
				{
					in = getNextInput();
					break;
				}
			}
		} while(!legal);
	}
	
	/**
	 * Based on user input, either moves menu to viewing reviews or
	 * writing a review.
	 * @param in user input
	 * @throws SQLException
	 */
	private static void reviewActions(String in) throws SQLException
	{
		ReviewMenu rm = (ReviewMenu)MenuPages.REVIEW.getMenu(); //variable for the ReviewMenu object
		
		do {
			legal = true;
			
			switch(in)
			{
				case "Q":
				{
					logOut();
					break;
				}
				case "B":
				{
					currMenu = currMenu.getMenu().previous();
					break;
				}
				case "1":
				{					
					rm.viewReviewQuery(conn); //queries the database
					
					System.out.println(
							rm.generateReviewList() +
							"| Back [B] | Log Out [Q] |"
						); //prints the list of reviews as a menu
					
					viewReviewActions(sc.nextLine()); //allows user to select from the printed list
					break;
				}
				case "2":
				{
					//get input from user
					System.out.println("Write your review: "); 
					String reviewInput = sc.nextLine();
					
					System.out.println("On a scale of 1 to 10, how do you rate this movie?");
					String starsInput = null;
					do {
						starsInput = sc.nextLine();
					} while(!starsInput.matches("[1-9]|10"));
					
					//queries the database
					int errorCode = rm.writeReviewQuery(conn, loggedInUser, reviewInput, Integer.parseInt(starsInput));
					if(errorCode == 1)
						System.out.println("Review successfully written.");
					else if(errorCode == 1062)
						System.out.println("You have already written a review. Try editing your review instead.");
					else if(errorCode == 1146)
						System.out.println("Invalid rating number. Please enter an integer from 1 to 10.");
					else
						System.out.println("Unexpected error. Review could not be written.");					
					//sends back to View-Write-Edit menu
					System.out.println("Sending back to menu...\n");
					break;
				}
				case "3":
				{
					//get input from user
					System.out.println("Write your new review: "); 
					String reviewInput = sc.nextLine();
					
					System.out.println("On a scale of 1 to 10, how do you rate this movie now?");
					String starsInput = null;
					do {
						starsInput = sc.nextLine();
					} while(!starsInput.matches("[1-9]|10"));
					
					//queries the database
					int errorCode = rm.editReviewQuery(conn, loggedInUser, reviewInput, Integer.parseInt(starsInput));
					if(errorCode == 1)
						System.out.println("Review successfully edited.");
					else if(errorCode == -1)
						System.out.println("You have not yet written a review. Try writing a review instead.");
					else if(errorCode == 1146)
						System.out.println("Invalid rating number. Please enter an integer from 1 to 10.");
					else
						System.out.println("Unexpected error. Review could not be written.");	
					//sends back to View-Write-Edit menu
					System.out.println("Sending back to menu...\n");
					break;
				}
				default:
				{
					in = getNextInput();
					break;
				}
			}
		} while(!legal);
	}
	
	/**
	 * Based on user input, selects a review from the list
	 * @param in user input, the number of the review from the list
	 * @throws SQLException
	 */
	private static void viewReviewActions(String in) throws SQLException
	{
		do {
			legal = true;
			if(in.matches("\\d+")) //if input is a number
			{
				int selection = Integer.parseInt(in); //parse it
				if(selection > 0 && //if the number is a valid entry between 1 and the review list size
						selection <= ((ReviewMenu)MenuPages.REVIEW.getMenu()).getReviewListSize())
					{	
						currMenu = MenuPages.COMMENT; //change the menu to write or edit a comment menu
						
						CommentMenu cm = (CommentMenu) currMenu.getMenu();
						
						cm.setReview( //and set the current review to user's selection
								((ReviewMenu)MenuPages.REVIEW.getMenu()).getReview(selection-1)
							);
						
						System.out.println( //print review
								cm.getReview() 
							);
						
						cm.viewCommentQuery(conn); //queries for review's comments					
						System.out.println( //print review's comments
								cm.getReview().toStringCommments()
							);
					}
			}
			else if(in.equals("B"))
				currMenu = MenuPages.REVIEW;
			else if(in.equals("Q"))
				logOut();
			else in = getNextInput();
		} while(!legal);
	}
	
	/**
	 * Based on user input, either allows user to write a comment or edit a comment made on
	 * a particular review.
	 * @param in user input
	 * @throws SQLException
	 */
	private static void commentActions(String in) throws SQLException 
	{
		CommentMenu cm = (CommentMenu) MenuPages.COMMENT.getMenu();
		do {
			legal = true;
			
			switch(in) {
				case "Q":
				{
					logOut();
					break;
				}
				case "B":
				{
					currMenu = currMenu.getMenu().previous();
					break;
				}
				case "1":
				{
					//get input from user
					System.out.println("Write your comment: "); 
					String commentInput = sc.nextLine();
					
					System.out.println("Vote +1 or -1 on the review: ");
					String helpfulInput = null;
					do {
						helpfulInput = sc.nextLine();
					} while(!helpfulInput.matches("[+-]?1"));
					
					//queries the database
					int errorCode = cm.writeCommentQuery(conn, loggedInUser, commentInput, Integer.parseInt(helpfulInput));
					if(errorCode == 1)
						System.out.println("Comment successfully written.");
					else if(errorCode == 1062)
						System.out.println("You have already written a comment. Try editing your comment instead.");
					else if(errorCode == 1146)
						System.out.println("Invalid vote number. Please enter either 1 or -1.");
					else
						System.out.println("Unexpected error. Comment could not be written.");	
					//sends back to View-Write-Edit menu
					System.out.println("Sending back to menu...\n");
					break;
				}
				case "2":
				{
					//get input from user
					System.out.println("Write your new comment: "); 
					String reviewInput = sc.nextLine();
					
					System.out.println("Vote +1 or -1 on the review: ");
					String helpfulInput = null;
					do {
						helpfulInput = sc.nextLine();
					} while(!helpfulInput.matches("[+-]?1"));
					
					//queries the database
					int errorCode = cm.editCommentQuery(conn, loggedInUser, reviewInput, Integer.parseInt(helpfulInput));
						if(errorCode == 1)
							System.out.println("Comment successfully edited.");
						else if(errorCode == -1)
							System.out.println("You have not yet written a comment. Try writing a comment instead.");
						else if(errorCode == 1146)
							System.out.println("Invalid vote number. Please enter either 1 or -1.");
						else
							System.out.println("Unexpected error. Comment could not be edited.");	
					//sends back to View-Write-Edit menu
					System.out.println("Sending back to menu...\n");
					break;
				}
				default:
				{
					in = getNextInput();
					break;
				}
			}
		} while(!legal);
	}
}
