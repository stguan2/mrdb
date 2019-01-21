package menus;

public class HomeMenu implements Menu {
	
	private final String menu = 
			"- - - H O M E - - -\n" +
			"1. Search for Movie\n" +
			"2. Change Account Information\n" +
			"| Log Out [Q] |";
	
	private final String adminMenu =  
			"- - - H O M E - - -\n" +
			"1. Search for Movie\n" +
			"2. Change Account Information\n" +
			"3. Administrator Actions\n" +
			"| Log Out [Q] |";
	
	private final MenuPages previous = null;
	private boolean admin;
	
	@Override
	public String getMenuStr() {
		if(admin) return adminMenu;
		else return menu;
	}

	@Override
	public MenuPages previous() {
		return previous;
	}
	
	public void setAdmin(boolean admin)
	{
		this.admin = admin;
	}
	
}
