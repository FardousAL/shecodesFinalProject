package MovieObject;

//create class for movie
public class MovieObject {
    private String movie_subject, movie_body;
    private String movie_url;
    private int movie_id;
    private float movie_Rating;
    private boolean isChecked;


    // constructor that contains all the movie data (creating an object)
    public MovieObject(int movie_id, String movie_subject, String movie_body, String movie_url, float movie_rating,boolean isChecked) {

        this.movie_subject = movie_subject;
        this.movie_body = movie_body;
        this.movie_url = movie_url;
        this.movie_id=movie_id;
        this.movie_Rating=movie_rating;
        this.isChecked=isChecked;
    }

//getters and setters for all the class's variables

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
    public float getmovie_Rating() {
        return movie_Rating;
    }

    public void setmovie_Rating(float rating) {
        this.movie_Rating = rating;
    }

    public String getMovie_subject() {
        return movie_subject;
    }

    public void setMovie_subject(String movie_subject) {
        this.movie_subject = movie_subject;
    }

    public String getMovie_body() {
        return movie_body;
    }

    public void setMovie_body(String movie_body) {
        this.movie_body = movie_body;
    }

    public String getMovie_url() {
        return movie_url;
    }

    public void setMovie_url(String movie_url) {
        this.movie_url = movie_url;
    }

    public int getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(int movie_id) {
        this.movie_id = movie_id;
    }
}
