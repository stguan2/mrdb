package classespackages;

public class User {
    private int uID;
    private String name;
    private String username;
    private String password;
    private boolean admin;

    public User(int uID, String name, String username, String password, boolean admin){
        this.uID = uID;
        this.name = name;
        this.username = username;
        this.password = password;
        this.admin = admin;
    }

    public int getuID() {
        return uID;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setuID(int uID) {
        this.uID = uID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isAdmin() {
        return admin;
    }
}
