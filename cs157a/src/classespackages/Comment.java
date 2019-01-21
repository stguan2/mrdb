package classespackages;

public class Comment {
    private int cID;
    private int rID;
    private int uID;
    private String comment;
    private String commentorName;

    public Comment(int cID, int rID, int uID, String comment, String cname) {
        this.cID = cID;
        this.rID = rID;
        this.uID = uID;
        this.comment = comment;
        this.commentorName = cname;
    }

    public int getcID() {
        return cID;
    }

    public void setcID(int cID) {
        this.cID = cID;
    }

    public int getrID() {
        return rID;
    }

    public void setrID(int rID) {
        this.rID = rID;
    }

    public int getuID() {
        return uID;
    }

    public void setuID(int uID) {
        this.uID = uID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
    		return "\t" + commentorName + " says: \"" + comment + "\"";
    }
}
