package MovieDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import MovieObject.MovieObject;

//create the class of data base
public class MovieDB extends SQLiteOpenHelper {

    //the file name
    public static final String DB_NAME="movies.db";
    //the table name
    public static final String TABLE_NAME="movies";
    //the name of the columns
    public static final String COL_1="ID";
    public static final String COL_2="SUBJECT";
    public static final String COL_3="BODY";
    public static final String COL_4="URL";
    public static final String COL_5="NETID";
    public static final String COL_6="RATING";
    public static final String COL_7="CHECKED";


    //class constructor: create the data base file
    public MovieDB(Context context) {
        super(context, DB_NAME, null, 1);

    }

    //creating the data base table
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,SUBJECT TEXT,BODY TEXT,URL TEXT,NETID INTEGER,RATING FLOAT,CHECKED INTEGER )");
    }

    // dropping the existing table and build a new one
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists "+TABLE_NAME);
        onCreate(db);
    }

    // inserting the data for new object to the data base table
    public boolean insertData(MovieObject movie){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(COL_2,movie.getMovie_subject());
        contentValues.put(COL_3,movie.getMovie_body());
        contentValues.put(COL_4,movie.getMovie_url());
        contentValues.put(COL_5,movie.getMovie_id());
        contentValues.put(COL_6,movie.getmovie_Rating());
        //check box returns boolean, but his value saved as integer in data base: 0 (false) or 1(true)
        int bol; if (movie.isChecked()) bol=1; else bol=0;
        contentValues.put(COL_7,bol);

        try {
            // the data was not insert to data base table when res value is equal to -1
            Long res = db.insert(TABLE_NAME, null, contentValues);
            if (res == -1) return false;
            else return true;
        }catch(SQLException e){
            Log.d("insert", e.getMessage());
        }
        return true;

    }

    //returns(cursor) all the data which is within the data base table
    public Cursor getAlldata(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=db.rawQuery("select * from "+TABLE_NAME,null);
        return cursor;
    }

    // update an existing data inside the data base table (by checking the movie id )
    public boolean updateData(MovieObject movie){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(COL_1,movie.getMovie_id());
        contentValues.put(COL_2,movie.getMovie_subject());
        contentValues.put(COL_3,movie.getMovie_body());
        contentValues.put(COL_4,movie.getMovie_url());
        contentValues.put(COL_6,movie.getmovie_Rating());
        //check box returns boolean, but his value saved as integer in data base: 0 (false) or 1(true)
        int bol; if (movie.isChecked()) bol=1; else bol=0;
        contentValues.put(COL_7,bol);

        db.update(TABLE_NAME,contentValues, "ID = ? ",new String[] {String.valueOf(movie.getMovie_id())});

        return true;
    }


    // to update the check box value in data base table for an existing movie
    public void updateCheck(String id,boolean isChecked){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        int bol; if (isChecked) bol=1; else bol=0;
        contentValues.put(COL_7,bol);
        db.update(TABLE_NAME,contentValues, "ID = ? ",new String[] {id});

    }


    // deleting one movie data from the data base table
    public void delete(String id){
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(TABLE_NAME,"ID = ?",new String[] {id});
    }

    //deleting all the movies data in data base table
    public void deleteAll(){

        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(TABLE_NAME,null,null);

    }
}
