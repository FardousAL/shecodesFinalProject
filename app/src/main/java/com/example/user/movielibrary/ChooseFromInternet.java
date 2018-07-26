package com.example.user.movielibrary;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import MovieAdapter.MovieAdapter;
import MovieObject.MovieObject;

//the third screen activity
public class ChooseFromInternet extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    //to view the search results
    ListView Search_List;
    //array list that contain all the movies from internet data( for search input)
    ArrayList<MovieObject> search_movies = new ArrayList<>();
    MovieAdapter listAdapter;
    public static EditText search_input;
    private Button goButton;

    //the link for the internet query
    final  String urlDomain ="http://api.themoviedb.org/3/search/movie?query=";
    final String apiKey = "&api_key=475886491d485463101022d88fe8cd4d";
//--------------------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_from_internet);

        //flag that view on witch screen we are right now
        Movies.flag="screen3";
        //the action type: is adding new movie from internet
        Movies.state="InternetUpdate";

        goButton=findViewById(R.id.go);
        search_input=findViewById(R.id.editSearch);
        Search_List=findViewById(R.id.searchList);
        listAdapter=new MovieAdapter(search_movies,this);

        //short click-pressing on movie from the list view
        Search_List.setOnItemClickListener(this);

        //on click for the go button - that should search for all the movies that contain search input in there subject
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_movies.clear();
                String input=search_input.getText().toString();
                String jsonQ=urlDomain+input+apiKey;
                showSearchList showList=new showSearchList();
                showList.execute(jsonQ);
                Log.d("debug", "onclick");
            }
        });

        //the saving data that the user insert( to show it again when he return to the screen)
        SharedPreferences pref=getSharedPreferences("input", Context.MODE_PRIVATE);
        if (pref.getString("search input",null)!=null){
            search_input.setText( pref.getString("search input","enter input to search"));
            onClick(Search_List);
        }
        if(savedInstanceState!=null){
            search_input.setText(savedInstanceState.getString("search"));

            onClick(Search_List);
        }


        // when you click on go button
        goButton.setOnClickListener(this);
    }

//--------------------------------------------------------------------------------------------------------------


    //saving the data (for the case when the user go from portiat to landscape)
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("search", search_input.getText().toString());
    }
//--------------------------------------------------------------------------------------------------------------


    //when the user press on phone back button
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences pref=getSharedPreferences("input", Context.MODE_PRIVATE);
        pref.edit().clear().commit();
        Intent intent=new Intent(this,Movies.class);
        startActivity(intent);
        finish();
    }
//--------------------------------------------------------------------------------------------------------------


    //clicking on cancel button
       public void cancelButton(View view) {

        //deleting the data that we saved on shared preference cause is leaving the screen
        SharedPreferences pref=getSharedPreferences("input", Context.MODE_PRIVATE);
        pref.edit().clear().commit();

        Intent intent=new Intent(this,Movies.class);
        startActivity(intent);
        finish();
    }

//--------------------------------------------------------------------------------------------------------------


    //method to close the keyboard when you press on some button
    public void closeKeyBoard(){
        View view=this.getCurrentFocus();
        if(view!=null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

//--------------------------------------------------------------------------------------------------------------

    //when you click on movie from the list view
    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        //saving the search input to show it again for the user when he return to the screen
        SharedPreferences pref=getSharedPreferences("input", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=pref.edit();
        editor.putString("search input",search_input.getText().toString());
        editor.commit();

        //moving to the second screen with the movie data
        Intent intent=new Intent(this,UpdateMovie.class);
        intent.putExtra(Movies.ID,search_movies.get(position).getMovie_id());
        intent.putExtra(Movies.SUBJECT,search_movies.get(position).getMovie_subject());
        intent.putExtra(Movies.BODY,search_movies.get(position).getMovie_body());
        intent.putExtra(Movies.URL,search_movies.get(position).getMovie_url());
        intent.putExtra(Movies.RATE,search_movies.get(position).getmovie_Rating());
        intent.putExtra(Movies.CHECKED,false);
        startActivity(intent);
        finish();
    }

//--------------------------------------------------------------------------------------------------------------


//Http request
    public String sendHttpRequest(String urlString){
        BufferedReader input = null;
        HttpURLConnection httpURLConnection = null;
        StringBuilder request = new StringBuilder();
        URL url = null;

        try {
            url = new URL(urlString);
            httpURLConnection =(HttpURLConnection) url.openConnection();
            if(httpURLConnection.getResponseCode()!=HttpURLConnection.HTTP_OK){
                return "HTTP NOT OK";
            }

            input = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String line;

            while ((line = input.readLine())!=null){
                request.append(line+"\n");
            }


        } catch (MalformedURLException e) {
            Toast.makeText(this,"MalformedURL= "+e.getMessage(),Toast.LENGTH_LONG).show();
            e.getMessage();
            return e.getMessage();
        } catch (IOException e) {
          //  Toast.makeText(this,"IOException= "+e.getMessage(),Toast.LENGTH_LONG).show();
            e.getMessage();
            return e.getMessage();
        }finally {
            if(input!=null){
                try {
                    input.close();
                } catch (IOException e) {
                    Toast.makeText(this,"IOException= "+e.getMessage(),Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
            if(httpURLConnection!=null){
                httpURLConnection.disconnect();
            }
        }
        return request.toString();
    }

 //--------------------------------------------------------------------------------------------------------------

    //extract the Json that we got from the http request
    public void extractJason(String jsonString){
        if (jsonString.equals("")){
            Toast.makeText(this, "there is no results for this input", Toast.LENGTH_LONG).show();
            return;
        }
        JSONObject jsonObject=null;
        String basicImageUrl="https://image.tmdb.org/t/p/w500";
        try {
            jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                int id=object.getInt("id");
                String title = object.getString("title");
                String path = object.getString("poster_path");
                String overview = object.getString("overview");
               Double vote_average=object.getDouble("vote_average");

                MovieObject temp = new MovieObject(id, title, overview, basicImageUrl+path,vote_average.floatValue(),false);
                search_movies.add(temp);
              }
            } catch(JSONException e){
                Log.d("extJason", e.getMessage());
                    e.getMessage();
            }

    }
//--------------------------------------------------------------------------------------------------------------


    //the on click method for the go button
    @Override
    public void onClick(View v) {
        closeKeyBoard();
        search_movies.clear();
        String input=search_input.getText().toString();
        if (input.equals("")){
            Toast.makeText(this, "you should insert search input", Toast.LENGTH_LONG).show();
            return;
        }
        String jsonQ=urlDomain+input+apiKey;
        showSearchList showList=new showSearchList();
        showList.execute(jsonQ);
    }

//===================================================================================================================


    //inner class (methods that is working on background)
    class showSearchList extends AsyncTask<String,Integer,String>{

        // Initialize a new instance of progress dialog
        final ProgressDialog pd = new ProgressDialog(ChooseFromInternet.this, R.style.AlertDialogCustom);

        //method that works before the do in background method
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

        //the actions that we want to do in background
        @Override
        protected String doInBackground(String... strings) {

            return  sendHttpRequest(strings[0]);
        }
//--------------------------------------------------------------------------------------------------------------

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
//--------------------------------------------------------------------------------------------------------------

        @Override
        protected void onPostExecute(String s) {
            //super.onPostExecute(s);
            //extracting the jason and enter the data to the arraylist so it can show on list view
            extractJason(s);
            Search_List.setAdapter(listAdapter);
            pd.dismiss();

        }
    }
}


