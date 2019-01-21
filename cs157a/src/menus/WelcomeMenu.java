package menus;

import java.sql.*;

public class WelcomeMenu implements Menu {
	private final String menu = 
			"--------------------------------\n\n" + 
					"\tWelcome to MRDb!\n\n" +
					"--------------------------------\n" +
					"1. Log In\n" +
					"2. Create an Account\n" +
					"| Exit [Q] |";
	private final MenuPages previous = null;
	PreparedStatement preparedStmt1 = null;
	PreparedStatement preparedStmt2 = null;
	PreparedStatement preparedStmt3 = null;
	public ResultSet checkLogin(Connection conn, String username, String pass) throws SQLException {
		ResultSet rs = null;
		String sql = null;
		sql = "SELECT * FROM Users WHERE username = ? and pass = ?";
		PreparedStatement preparedStmt = conn.prepareStatement(sql);
		preparedStmt.setString(1, username);
		preparedStmt.setString(2, pass);				
		rs = preparedStmt.executeQuery();
		preparedStmt1 = preparedStmt;
		return rs;
	}

	//Returns false if Users database does not contain specified username
	public Boolean checkUsername(Connection conn, String username) throws SQLException {
		String sql = null;
		ResultSet rs = null;
		sql = "SELECT * FROM Users WHERE username = ?";
		PreparedStatement preparedStmt = conn.prepareStatement(sql);
		preparedStmt.setString(1, username);
		rs = preparedStmt.executeQuery();
		preparedStmt2 = preparedStmt;
		return rs.next();
	}
	
	public void createAccount(Connection conn, String username, String password, String name) throws SQLException{
		String sql = null;
		sql = "INSERT INTO Users(name,username,pass) VALUES(?, ?, ?)";
		PreparedStatement preparedStmt = conn.prepareStatement(sql);
		preparedStmt.setString(1, name);
		preparedStmt.setString(2, username);
		preparedStmt.setString(3, password);
		preparedStmt.executeUpdate();
		preparedStmt3 = preparedStmt;
	}
	@Override
	public String getMenuStr() {
		return menu;
	}

	@Override
	public MenuPages previous() {
		return previous;
	}

	public PreparedStatement[] getPreparedStatements() {
		PreparedStatement[] preparedStmts = new PreparedStatement[3];
		preparedStmts[0] = preparedStmt1;
		preparedStmts[1] = preparedStmt2;
		preparedStmts[2] = preparedStmt3;
		return preparedStmts;
	}
}
