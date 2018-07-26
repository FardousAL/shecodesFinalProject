package MovieAdapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.user.movielibrary.Movies;
import com.example.user.movielibrary.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import MovieObject.MovieObject;

//create an Custom Adapter
public class MovieAdapter extends BaseAdapter {

    //Setting Variables: 1- ArrayList is an array that contains all the data for each row, 2- context is the activity that we work on it
    private ArrayList<MovieObject> MoviesList;
    Context context;

    //class constructor that gets in his signature two variables: the arrayList and the context
    public MovieAdapter(ArrayList<MovieObject> moviesList, Context context) {
        MoviesList = moviesList;
        this.context = context;
    }

    //method that returns the array list size
    @Override
    public int getCount() {
        return MoviesList.size();
    }

    //method that returns row on list view (that is an object)
    @Override
    public Object getItem(int position) {
        return MoviesList.get(position);
    }

    //method that returns the position of the row in list view
    @Override
    public long getItemId(int position) {
        return position;
    }

    //method that enter the object data to the xml for one row in list view
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.row_in_list,null);

        //text view
        TextView subject=convertView.findViewById(R.id.movieName);
        TextView body=convertView.findViewById(R.id.bodyView);
        TextView ratingValue=convertView.findViewById(R.id.rateValue);
        subject.setText(MoviesList.get(position).getMovie_subject());
        subject.setInputType(View.TEXT_ALIGNMENT_TEXT_END);
        body.setText(MoviesList.get(position).getMovie_body());

        //rating bar
        ratingValue.setText(""+MoviesList.get(position).getmovie_Rating());
        RatingBar rating=convertView.findViewById(R.id.movieRating);
        rating.setRating( MoviesList.get(position).getmovie_Rating());
        rating.setNumStars(10);
        //giving color to the text view that contains the rating value
        int gColor = Color.GREEN;
        int rColor = Color.RED;
        if (MoviesList.get(position).getmovie_Rating()>5){
            ratingValue.setTextColor(gColor);
        }else
        {
            ratingValue.setTextColor(rColor);
        }

        //image view
        ImageView image=convertView.findViewById(R.id.movieImage);
        //if there is an image url, show the image on image view. if there is not put the default image
        if (MoviesList.get(position).getMovie_url().trim().length()!=0) {
            Picasso.get().load(MoviesList.get(position).getMovie_url()).into(image);
        }else image.setImageResource(R.drawable.defualt_img);

        //check box
        final CheckBox checkBox = convertView.findViewById(R.id.checkBox);
        //show the check box just on the main activity, on search activity turn it to invisible
        if (Movies.flag=="screen1") {

            //if the user click on check box --> Change the status
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (MoviesList.get(position).isChecked()) {

                        //updating the check value in array list
                        MoviesList.get(position).setChecked(false);
                        //updating the check value in data base
                        Movies.myDB.updateCheck("" + MoviesList.get(position).getMovie_id(), MoviesList.get(position).isChecked());
                    }
                    else {

                        //updating the check value in array list
                        MoviesList.get(position).setChecked(true);
                        //updating the check value in data base
                        Movies.myDB.updateCheck(""+MoviesList.get(position).getMovie_id(), MoviesList.get(position).isChecked());
                    }

                }
            });
            checkBox.setChecked(MoviesList.get(position).isChecked());

        } else checkBox.setVisibility(View.INVISIBLE);


        return convertView;
    }
}
