package com.example.user.movielibrary;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import MovieObject.MovieObject;

//the second screen activity
public class UpdateMovie extends AppCompatActivity implements View.OnClickListener {

    //the class variables
    private EditText subject,body,url;
    private TextView txtRatingValue;
    private ImageView image;
    private Button ok,cancel,show;
    private RatingBar rate;
    //the movie id that want to update
    public static int id_input;
    //keys to transfer data between screens
    public static String MID="ID";
    public static String MSUBJECT="SUBJECT";
    public static String MBODY="BODY";
    public static String MURL="URL";
    public static String MRATE="RATE";

    //for checkbox
    public static String MCHECKED="CHECKED";
    boolean checked;

    //for rating bar
    private RatingBar ratingBar;
    int gColor = Color.GREEN;
    int rColor = Color.RED;

//--------------------------------------------------------------------------------------------------------------

    //on create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_movie);

        //connecting the xml to java
        subject=findViewById(R.id.editSubject);
        body=findViewById(R.id.editBody);
        url=findViewById(R.id.editUrl);
        image=findViewById(R.id.showImage);
        image.setVisibility(View.GONE);
        ok=findViewById(R.id.ok);
        cancel=findViewById(R.id.cancel);
        rate=findViewById(R.id.movieRating);
        show=findViewById(R.id.show);
        txtRatingValue = findViewById(R.id.rateValue);
        ratingBar = findViewById(R.id.movieRating);
        //the number of stars on rating bar
        ratingBar.setNumStars(10);

        // לשאוף את המידע על פי סוג הפעולה והדף שהוא בא ממנו על מנת להציג אותו
        if(Movies.state=="update"||Movies.state=="InternetUpdate") {
            Intent intent = getIntent();
            if (intent.getExtras() != null) {
                Bundle db = intent.getExtras();

                //the data that we got from the first screen for update movie or from third screen for insert
                id_input = (int) db.get(Movies.ID);
                String subject_input = (String) db.get(Movies.SUBJECT);
                String body_input = (String) db.get(Movies.BODY);
                String url_input = (String) db.get(Movies.URL);
                float  rate_input = (float) db.get(Movies.RATE);

                //for checkbox
                checked=(boolean)db.get(Movies.CHECKED);
                //insert the data that we got to the second screen fields
                body.setMovementMethod(new ScrollingMovementMethod());
                subject.setText(subject_input);
                body.setText(body_input);
                url.setText(url_input);
                rate.setRating( rate_input);
                txtRatingValue.setText(""+rate_input);
                //giving a color to the rating value
                if (rate.getRating()>5){
                    txtRatingValue.setTextColor(gColor);
                }else
                {
                    txtRatingValue.setTextColor(rColor);
                }
            }
        }

        //if we click on rating bar
        ratingBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtRatingValue.setText(""+ratingBar.getRating());
                if (ratingBar.getRating()>5){
                    txtRatingValue.setTextColor(gColor);
                }else
                {
                    txtRatingValue.setTextColor(rColor);
                }
            }
        });

        //if we click on rating bar to change his value
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {

                txtRatingValue.setText(String.valueOf(rating));
                if (rating>5){
                    txtRatingValue.setTextColor(gColor);
                }else
                {
                    txtRatingValue.setTextColor(rColor);
                }
            }
        });

        //the buttons
        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);
        show.setOnClickListener(this);
    }

//--------------------------------------------------------------------------------------------------------------


    //to check if the movie is exist on the database (when you want to add same movie from search screen)
    public boolean IsExist(){
        Cursor cursor=Movies.myDB.getAlldata();
        cursor.moveToFirst();
        while(cursor.moveToNext()) {
        if(id_input==cursor.getInt(4))
            return false;
        }
        return true;
    }
//--------------------------------------------------------------------------------------------------------------


    //on click method for all the screen buttons
    @Override
    public void onClick(View v) {
        switch (v.getId()){

            //**************************************************************************************
            //if you click on ok button should update or add the data
            case R.id.ok:
                //if we in case of update movie in the database
                if(Movies.state.equals("update")){
                    //creating object from the data that we got above
                    MovieObject data=new MovieObject(id_input,subject.getText().toString(), body.getText().toString(), url.getText().toString(),rate.getRating(),checked);
                    //to check that the subject field is not null
                    if (!subject.getText().toString().equals("")) {
                        boolean isUpdate = Movies.myDB.updateData(data);
                        if (isUpdate) {
                            Toast.makeText(this, "updated", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "not updated ", Toast.LENGTH_LONG).show();
                        }
                    //send the data and moving to the previous screen
                    Intent intent=new Intent(this,Movies.class);
                    intent.putExtra(MID,data.getMovie_id());
                    intent.putExtra(MSUBJECT,data.getMovie_subject());
                    intent.putExtra(MBODY,data.getMovie_body());
                    intent.putExtra(MURL,data.getMovie_url());
                    intent.putExtra(MRATE,data.getmovie_Rating());
                    //for checkbox
                    intent.putExtra(MCHECKED,data.isChecked());
                    startActivity(intent);
                    finish();

                    }
                    else Toast.makeText(this, "subject filed is empty", Toast.LENGTH_LONG).show();
                } else
                    //for the case of manual add
                    if (Movies.state.equals("add")) {
                    //creating object from the data that we got above
                    MovieObject data = new MovieObject(id_input, subject.getText().toString(), body.getText().toString(), url.getText().toString(),rate.getRating(),false);
                    //to check that the subject field is not null
                    if (!subject.getText().toString().equals("")) {
                        boolean isInsert = Movies.myDB.insertData(data);
                        if (isInsert) {
                            Toast.makeText(this, "inserted data", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "not inserted ", Toast.LENGTH_LONG).show();
                        }
                        //send the data and moving to the previous screen
                        Intent intent = new Intent(this, Movies.class);
                        intent.putExtra(MID, data.getMovie_id());
                        intent.putExtra(MSUBJECT, data.getMovie_subject());
                        intent.putExtra(MBODY, data.getMovie_body());
                        intent.putExtra(MURL, data.getMovie_url());
                        intent.putExtra(MRATE,data.getmovie_Rating());
                        //for checkbox
                        intent.putExtra(MCHECKED,data.isChecked());
                        startActivity(intent);
                        finish();
                    } else Toast.makeText(this, "subject filed is empty", Toast.LENGTH_LONG).show();
                } else
                     //for the case of internet add
                    if (Movies.state.equals("InternetUpdate")) {
                        //creating object from the data that we got above
                        MovieObject data = new MovieObject(id_input, subject.getText().toString(), body.getText().toString(), url.getText().toString(),rate.getRating(),false);

                        //to check if the movie was added before to the list(DB)
                        if (IsExist()) {
                            if (!subject.getText().toString().equals("")) {
                                boolean isInsert = Movies.myDB.insertData(data);
                                if (isInsert) {
                                    Toast.makeText(this, "inserted data", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(this, "not inserted ", Toast.LENGTH_LONG).show();
                                }
                                //send the data and moving to the previous screen
                                Intent intent = new Intent(this, ChooseFromInternet.class);
                                intent.putExtra(MID, data.getMovie_id());
                                intent.putExtra(MSUBJECT, data.getMovie_subject());
                                intent.putExtra(MBODY, data.getMovie_body());
                                intent.putExtra(MURL, data.getMovie_url());
                                intent.putExtra(MRATE,data.getmovie_Rating());
                                //for checkbox
                                intent.putExtra(MCHECKED,data.isChecked());
                                startActivity(intent);
                                finish();
                            } else
                                Toast.makeText(this, "subject filed is empty", Toast.LENGTH_LONG).show();
                        } else Toast.makeText(this, "is already exist", Toast.LENGTH_LONG).show();

                }
                break;
            //**************************************************************************************
            //if you click on the cancel button
            case R.id.cancel:
                // moving to the previous screen without saving the data
                if(Movies.flag=="screen1") {
                    Intent intent = new Intent(this, Movies.class);
                    startActivity(intent);
                    finish();
                }else if(Movies.flag=="screen3"){
                    Intent intent = new Intent(this, ChooseFromInternet.class);
                    startActivity(intent);
                    finish();
                }
                break;
            //**************************************************************************************
            //if you press on show button
            case R.id.show:
                //to close the keyboard
                closeKeyBoard();
                //to check if the url field is not null
                if (!url.getText().toString().equals("")) {
                    ImageTask showImage = new ImageTask();
                    showImage.execute();
                } else Toast.makeText(this, "there is no url", Toast.LENGTH_LONG).show();

                break;
        }

    }

//--------------------------------------------------------------------------------------------------------------


    //method to close the keyboard
    public void closeKeyBoard(){
        View view=this.getCurrentFocus();
        if(view!=null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
     }
//--------------------------------------------------------------------------------------------------------------


     //on phone back press - to return to the previous screen
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(Movies.flag=="screen1") {
            Intent intent = new Intent(this, Movies.class);
            startActivity(intent);
            finish();
        }else if(Movies.flag=="screen3"){
            Intent intent = new Intent(this, ChooseFromInternet.class);
            startActivity(intent);
            finish();
        }

    }
//==============================================================================================================


//inner class for methods on background
class ImageTask extends AsyncTask<String, Void, Void> {

  // Initialize a new instance of progress dialog
        final ProgressDialog pd = new ProgressDialog(UpdateMovie.this, R.style.AlertDialogCustom);
//--------------------------------------------------------------------------------------------------------------

        //method before the doInBackground method
        @Override
        protected void onPreExecute() {
            //super.onPreExecute();

            // Set progress dialog style spinner
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);

            // Set the progress dialog title and message
            pd.setTitle("wait while loading data");
            pd.setMessage("Loading.........");
            pd.show();
        }
//--------------------------------------------------------------------------------------------------------------

    //doInBackground method
    @Override
    protected Void doInBackground(String... voids) {
        return null;
    }
//--------------------------------------------------------------------------------------------------------------

    @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
//--------------------------------------------------------------------------------------------------------------

        //method after background method-to show the image
        @Override
        protected void onPostExecute(Void s) {
           // super.onPostExecute(s);
            image.setVisibility(View.VISIBLE);
            Picasso.get().load(url.getText().toString()).into(image);
            pd.dismiss();
        }
    }
}
