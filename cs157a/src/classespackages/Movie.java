package classespackages;
import java.util.ArrayList;

public class Movie {
    private String tconst;
    private String title;
    private String averageRating;
    private String numVotes;
    private String year;
    private String runtimeMinutes;
    private String[] genres;
    private ArrayList<Review> reviews;

    public Movie(String tconst, String title, String averageRating, String numVotes, String year, String runtimeMinutes, String[] genres) {
        this.tconst = tconst;
        this.title = title;
        this.averageRating = averageRating;
        this.numVotes = numVotes;
        this.year = year;
        this.runtimeMinutes = runtimeMinutes;
        this.genres = genres;
    }

    public String getTconst() {
        return tconst;
    }

    public void setTconst(String tconst) {
        this.tconst = tconst;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(String averageRating) {
        this.averageRating = averageRating;
    }

    public String getNumVotes() {
        return numVotes;
    }

    public void setNumVotes(String numVotes) {
        this.numVotes = numVotes;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getRuntimeMinutes() {
        return runtimeMinutes;
    }

    public void setRuntimeMinutes(String runtimeMinutes) {
        this.runtimeMinutes = runtimeMinutes;
    }

    public String[] getGenres() {
        return genres;
    }

    public void setGenres(String[] genres) {
        this.genres = genres;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }
}
