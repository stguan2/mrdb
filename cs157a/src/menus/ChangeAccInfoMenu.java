package menus;

import java.sql.*;

import classespackages.User;


public class ChangeAccInfoMenu implements Menu {

	private final String menu = 
			"- - - CHANGE ACCOUNT INFORMATION - - -\n" +
					"1. View Account Information\n" +
					"2. Change Name\n" +
					"3. Change Password\n" +
					"| Back [B] | Log Out [Q] |";

	private final MenuPages previous = MenuPages.HOME;
	private PreparedStatement preparedStmt1 = null;
	private PreparedStatement preparedStmt2 = null;
	@Override
	public String getMenuStr() {
		return menu;
	}

	@Override
	public MenuPages previous() {
		return previous;
	}

	/**
	 * Change user's name
	 * @param conn
	 * @param name specified name to be changed to
	 * @param user specified user to change name
	 * @return true if name changed successful, otherwise false
	 * @throws SQLException
	 */
	public Boolean editName(Connection conn, String name, User user) throws SQLException{
		String sql = null;
		Boolean bool = false;
		sql = "Update Users Set Name = ? where uID = ?";
		PreparedStatement preparedStmt = conn.prepareStatement(sql);
		preparedStmt.setString(1, name);
		preparedStmt.setInt(2, user.getuID());

		preparedStmt.executeUpdate();
		preparedStmt1 = preparedStmt;
		bool = true;
		return bool;
	}

	/**
	 * Change user's password
	 * @param conn
	 * @param pass specified password to be changed to
	 * @param user specified user to change password
	 * @return true if password changed successfully, otherwise false
	 * @throws SQLException
	 */
	public Boolean editPass(Connection conn, String pass, User user) throws SQLException{
		String sql = null;
		Boolean bool = false;
		sql = "Update Users Set Pass = ? where uID = ?";
		PreparedStatement preparedStmt = conn.prepareStatement(sql);
		preparedStmt.setString(1, pass);
		preparedStmt.setInt(2, user.getuID());

		preparedStmt.executeUpdate();
		preparedStmt2 = preparedStmt;
		bool = true;
		return bool;
	}

	public PreparedStatement[] getPreparedStatements() {
		PreparedStatement[] preparedStmts = new PreparedStatement[2];
		preparedStmts[0] = preparedStmt1;
		preparedStmts[1] = preparedStmt2;
		return preparedStmts;
	}

}
