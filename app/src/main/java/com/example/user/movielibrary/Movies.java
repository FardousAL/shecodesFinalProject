package com.example.user.movielibrary;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import MovieAdapter.MovieAdapter;
import MovieDB.MovieDB;
import MovieObject.MovieObject;


//the first screen activity
public class Movies extends AppCompatActivity  {

    //flag to know from witch activity you came- to takes you back to the previous screen when you press on ok button on the second screen
    public static String flag;
    //is indicator for second screen to know what kind of action to perform-add manual/update/internet add
    public static String state;
    //list view to view the movies and their data
    public static ListView Movies_List;
    // An array containing all the movies and their data( objects array)
    public static ArrayList<MovieObject> movies;
    //data base variable to insert and update the data
    public static MovieDB myDB;
    //adapter variable that preform the data on list view
    public static MovieAdapter movieAdapter;
    //movie id
    public static int id;
    //keys(the name of the columns) are for sending information and retrieve information between screens
    public static final String ID = "ID";
    public static final String SUBJECT = "SUBJECT";
    public static final String BODY = "BODY";
    public static final String URL = "URL";
    public static final String RATE = "RATE";
    public static final String CHECKED="CHECKED";

//--------------------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        //flag that view on witch screen we are right now
        flag="screen1";


        //initialization
        movies=new ArrayList<>();
        myDB=new MovieDB(this);

        //to insert all the data from data base into the array list
        CreateArrayFromDB(myDB.getAlldata());
        Movies_List=findViewById(R.id.MoviesList);
        movieAdapter=new MovieAdapter(movies,this);
        Movies_List.setAdapter(movieAdapter);

        //retrieve information from the second screen for update/add
            Intent intent = getIntent();
            if (intent.getExtras() != null) {
                Bundle db = intent.getExtras();
                int id_input = (int) db.get(UpdateMovie.MID);
                String subject_input = (String) db.get(UpdateMovie.MSUBJECT);
                String body_input = (String) db.get(UpdateMovie.MBODY);
                String url_input = (String) db.get(UpdateMovie.MURL);
                float rate_input = (float) db.get(UpdateMovie.MRATE);
                boolean checked=(boolean)db.get(UpdateMovie.MCHECKED);


                MovieObject temp;
                if(id_input!=id&& subject_input!=null){
                    temp= new MovieObject(id_input,subject_input,body_input,url_input,rate_input,checked);
                   //to insert the data we retrieved from the second screen to the array list
                    movies.add(temp);
                    movieAdapter.notifyDataSetChanged();
                }
                //refresh the list view
                movieAdapter.notifyDataSetChanged();
            }

        //short press- when you click on movie from the list view, open the second screen with the movie data (for update)
        Movies_List.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //The action type is to update an existing movie
                state="update";
                //transition from first screen to the second screen with sending the movie data by put extra
                Intent intent=new Intent(Movies.this,UpdateMovie.class);
                Movies.id=movies.get(position).getMovie_id();
                intent.putExtra(ID,movies.get(position).getMovie_id());
                intent.putExtra(SUBJECT,movies.get(position).getMovie_subject());
                intent.putExtra(BODY,movies.get(position).getMovie_body());
                intent.putExtra(URL,movies.get(position).getMovie_url());
                intent.putExtra(RATE,movies.get(position).getmovie_Rating());
                intent.putExtra(CHECKED,movies.get(position).isChecked());
                startActivity(intent);
                finish();

            }
        });
//**************************************************************************************************
        //long press- when you click on movie from the list view, open dialog with two options
        Movies_List.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent,final View view, final int position, long id) {
                //building the dialog
                AlertDialog.Builder builder=new AlertDialog.Builder(Movies.this, R.style.AlertDialogCustom);
                builder.setCancelable(true);
                //set title to the dialog
                builder.setTitle("Choose one option");
                //the first option: update movie
                builder.setPositiveButton("update movie", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        state="update";
                        //transition from first screen to the second screen with sending the movie data by put extra
                        Intent intent=new Intent(Movies.this,UpdateMovie.class);
                        Movies.id=movies.get(position).getMovie_id();
                        intent.putExtra(ID,movies.get(position).getMovie_id());
                        intent.putExtra(SUBJECT,movies.get(position).getMovie_subject());
                        intent.putExtra(BODY,movies.get(position).getMovie_body());
                        intent.putExtra(URL,movies.get(position).getMovie_url());
                        intent.putExtra(RATE,movies.get(position).getmovie_Rating());
                        intent.putExtra(CHECKED,movies.get(position).isChecked());
                        startActivity(intent);
                        finish();

                    }
                });
                //the second option: delete movie
                builder.setNegativeButton("delete movie", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       //deleting the movie data from data base
                        myDB.delete(String.valueOf(movies.get(position).getMovie_id()));
                        //deleting the movie data from array list
                        movies.remove(position);
                        //refresh the first screen
                        movieAdapter.notifyDataSetChanged();

                    }
                });
                //displaying the dialog
                builder.show();
                return true;
            }
        });
    }

//--------------------------------------------------------------------------------------------------------------


    //to insert all the movies data from data base into the array list
    public void CreateArrayFromDB(Cursor cursor){
        while (cursor.moveToNext()){
            //replace the data for check box from integer to boolean-cause the check box on the object is boolean and in the data base is integer(0 or 1)
            boolean bol=(cursor.getInt(6)!=0);
            //creating an movie object for each single movie with his data
            MovieObject temp= new MovieObject(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getFloat(5),bol);
            //insert the movie object to the array list
            movies.add(temp);
        }
    }
//--------------------------------------------------------------------------------------------------------------


    //on click method for the add (+) button- should open dialog with two options:manual add/internet add
    public void addDialog(View view) {
        state="add";
        //building the dialog
        AlertDialog.Builder builder=new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        builder.setCancelable(true);
        //set title to the dialog
        builder.setTitle("Choose one option..");
        //the first option: internet add so it goes to the third screen
        builder.setPositiveButton("add from Internet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(Movies.this,ChooseFromInternet.class);
                startActivity(intent);
                finish();
            }
        });
        //the second option: manual add so it goes to the second screen with empty fileds
        builder.setNegativeButton("add manual", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(Movies.this,UpdateMovie.class);
                startActivity(intent);
                finish();
            }
        });
        builder.show();
    }
//--------------------------------------------------------------------------------------------------------------


    //when you pressing on phone back button should not close the main screen
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Movies.this,Movies.class);
        startActivity(intent);
        finish();
    }
//--------------------------------------------------------------------------------------------------------------


    //creating the menu (connecting to the xml menu)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.movie_menu,menu);
        return true;
    }
//--------------------------------------------------------------------------------------------------------------


    //activate the menu options- when you select an option from the menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            //when you click on log out option it will close the main screen
            case R.id.logOut:
                this.finish();
                return true;
            //when you click on delete all it should delete all the movies that appear on list view(by deleting the DB and array list)
            case R.id.deleteAll:
                myDB.deleteAll();
                movies.clear();
                movieAdapter.notifyDataSetChanged();
                return true;
            default:
                return true;
        }


    }
}
