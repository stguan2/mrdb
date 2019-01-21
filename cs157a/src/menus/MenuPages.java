package menus;

public enum MenuPages {
	
	WELCOME(new WelcomeMenu()),
	HOME(new HomeMenu()),
	ADMIN(new AdminMenu()),
	CHANGEACCINFO(new ChangeAccInfoMenu()),
	SEARCHMOVIE(new SearchMovieMenu()), 
	REVIEW(new ReviewMenu()),
	COMMENT(new CommentMenu());
	
	private Menu menu;
	
	private MenuPages(Menu m) {
		menu = m;
	}
	
	public Menu getMenu() {
		return menu;
	}
}
