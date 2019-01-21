package menus;

import classespackages.Movie;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

public class SearchMovieMenu implements Menu {
	private Statement myStmt;
	private ResultSet myRs;
	Movie result ,selectedMovie;

	private final String menu = 
			"- - - SEARCH FOR A MOVIE - - -\n" +
			"1. Search By Title\n" +
			"2. Search by Rating\n" +
			"3. Search By Genre\n" +
			"4. Search by Year\n" +
			"5. Search by Reviewed Movies\n" +
			"| Back [B] | Log Out [Q] |";
	
	private final MenuPages previous = MenuPages.HOME;

	@Override
	public String getMenuStr() {
		return menu;
	}

	@Override
	public MenuPages previous() {
		return previous;
	}

	/**
	 * search movies filtered by title
	 * @param conn contains the current sql connection
	 * @return selected Movie
	 */
	public Movie searchbytitle(Connection conn, Scanner sc){
		System.out.print("please enter the title of the movie you're looking for:");
		String input = sc.nextLine();
		try{
			myStmt = conn.createStatement();
			myRs = myStmt.executeQuery("select * from titleinfo join titlerating using(tconst) where title='" + input  + "'");
			selectedMovie = printsearchMovie(myRs,sc);
		}
		catch(Exception exc){
			exc.printStackTrace();
		}
		return selectedMovie;
	}

	/**
	 * search movies filtered by rating
	 * @param conn contains the current sql connection
	 * @return selected movie
	 */
	public Movie searchbyrating(Connection conn, Scanner sc){
		int input = -1;
		do{
			System.out.print("please enter the minimum movie rating you're looking for(1-10):");
			while(!sc.hasNextInt()){
				System.out.print("That's not a number, try again...:");
				sc.next();
			}
			input = sc.nextInt();
		}while(input <= 0 || input > 10);
		try{
			myStmt = conn.createStatement();
			myRs = myStmt.executeQuery("select * from titleinfo  join titlerating using(tconst) where averageRating >= '" + input + "' order by averageRating DESC");
			selectedMovie = printsearchMovie(myRs,sc);
		}
		catch(Exception exc){
			exc.printStackTrace();
		}
		return selectedMovie;
	}

	/**
	 * search movie filtered by genre
	 * @param conn contains the current sql connection
	 * @return selected movie
	 */
	public Movie searchbygenre(Connection conn, Scanner sc){
		System.out.print("please enter the genre you're looking for:");
		String input = sc.next();
		try{
			myStmt = conn.createStatement();
			myRs = myStmt.executeQuery("select * from titleinfo  join titlerating using(tconst) where genre1" +
					"= '" + input  + "' or genre2"  +
					"= '" + input + "' or genre3" +
					"= '" + input + "'");
			selectedMovie = printsearchMovie(myRs,sc);
		}
		catch(Exception exc){
			exc.printStackTrace();
		}
		return selectedMovie;
	}

	/**
	 * search movies filtered by years
	 * @param conn contains the current sql connection
	 * @return selected movie
	 */
	public Movie searchbyyear(Connection conn, Scanner sc){
		System.out.print("please enter the year of the movie you're looking for:");
		String input = sc.next();
		try{
			myStmt = conn.createStatement();
			myRs = myStmt.executeQuery("select * from titleinfo  join titlerating using(tconst) where year='" + input + "'");
			selectedMovie = printsearchMovie(myRs,sc);
		}
		catch(Exception exc){
			exc.printStackTrace();
		}
		return selectedMovie;
	}

	/**
	 * search reviewed movies
	 * @param conn contains the current sql connection
	 * @return selected movie
	 */
	public Movie reviewedMovies(Connection conn, Scanner sc){
		try{
			myStmt = conn.createStatement();
			myRs = myStmt.executeQuery("select * from titleinfo m join titlerating using(tconst) where exists ( select * from titlereviews r where m.tconst = r.tconst)");
			selectedMovie =  printsearchMovie(myRs, sc);
		}
		catch(Exception exc){
			exc.printStackTrace();
		}
		return selectedMovie;
	}

	/**
	 * prints result set
	 * @param mys contains result set
	 * @return the selected movie to the callee
	 * @throws Exception
	 */
	private Movie printsearchMovie(ResultSet mys, Scanner sc)throws Exception {
		String []genres = new String[3];
		ArrayList<Movie> movielist = new ArrayList<Movie>();

		int size = 0;
		int incrementor = 1;
		Movie temp_Movie = null;
		while (mys.next()) {
			size++;
		}

		mys.beforeFirst();// reset pointer
		if (size == 0) {
			System.out.println("\n---------------------------\n-------- no result --------\n---------------------------\n");
		} else {
			while (mys.next()) {

				String tconst = mys.getString("tconst");
				String title = mys.getString("title") == null ? "" : "title:" + mys.getString("title");
				String year = mys.getString("year") == null ? "" : ", year:" + mys.getString("year");
				String averageRating = mys.getString("averageRating") == null ? "" : ", averageRating:" + mys.getString("averageRating");
				String numVotes = mys.getString("numVotes") == null ? "" : ", numVotes:" + mys.getString("numVotes");
				String runtimeMinutes = mys.getString("runtimeMinutes") == null ? "" : ", runtime Min:" + mys.getString("runtimeMinutes");
				String genre1 = mys.getString("genre1") == null ? "" : ", genres:" + mys.getString("genre1");
				String genre2 = mys.getString("genre2") == null ? "" : ", " + mys.getString("genre2");
				String genre3 = mys.getString("genre3").equals("N\r") ? "" : ", " + mys.getString("genre3");

				System.out.println(incrementor + ":" + title + averageRating + numVotes + year + runtimeMinutes + genre1 + genre2 + genre3);
				incrementor++;

				genres[0] = genre1;
				genres[1] = genre2;
				genres[2] = genre3;

				temp_Movie = new Movie(tconst, title, averageRating, numVotes, year, runtimeMinutes, genres);
				movielist.add(temp_Movie);
			}
			int input = -1;
			do{
				System.out.print("Select a valid specific movie base on their (#):");
				while(!sc.hasNextInt()){
					System.out.print("That's not a number, try again...:");
					sc.next();
				}
				input = sc.nextInt();
			}while(input <= 0 || input > movielist.size());
			result = movielist.get(input - 1);
			String[] resultgenres = result.getGenres();
			String printgenre = "";
			for(String genre: resultgenres){
				if(genre != null)
					printgenre += genre;
			}
			System.out.println("- - - RESULT - - -\n");
			System.out.println(result.getTitle() + result.getYear() + result.getAverageRating() + result.getNumVotes() + result.getRuntimeMinutes() + printgenre + "\n");
		}
		return result;
	}
	public Statement[] getStatements() {
		Statement[] stmts = new Statement[1];
		stmts[0] = myStmt;
		return stmts;
	}
}
