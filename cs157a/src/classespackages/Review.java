package classespackages;

import java.util.ArrayList;

public class Review {
    private int rID;
    private int uID;
    private String tconst;
    private String review;
    private int stars;
    private String reviewerName;
    private int reviewRating;
    private ArrayList<Comment> comments;
    
    public Review(int rID, int uID, String tconst, String review, int stars, String reviewerName, int rate) {
        this.rID = rID;
        this.uID = uID;
        this.tconst = tconst;
        this.review = review;
        this.stars = stars;
        this.reviewerName = reviewerName;
        this.reviewRating = rate;
        comments = new ArrayList<Comment>();
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

    public String getTconst() {
        return tconst;
    }

    public void setTconst(String tconst) {
        this.tconst = tconst;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }
    
    public int getRating() {
    		return reviewRating;
    }
    
    public void setRating(int rate) {
    		this.reviewRating = rate;
    }
    
    public void addComment(Comment c) {
    		comments.add(c);
    }
    
    @Override
    public String toString() {
    		return reviewerName + " says: \"" + review + "\"\nRating: " + stars + "/10\tHelpfulness: " + reviewRating;
    }
    
    public String toStringCommments() {
    		String str = "";
    		
    		for(Comment c : comments) str += c.toString() + "\n";
    		
    		return str;
    }
}
