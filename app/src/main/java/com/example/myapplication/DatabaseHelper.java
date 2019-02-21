package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Tworzenie bazy danych SQLite
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Score.db"; //nazwa bazy danych
    public static final String TABLE_NAME = "score_table"; //nazwa tabeli
    //publiczne stringi do nazw kolumn, aby nie pisac za kazdym razem i sie nie walnac
    public static final String COL_1 = "ID"; //id
    public static final String COL_2 = "NAME"; //imie uzytkownika
    public static final String COL_3 = "DATE"; //data wyniku
    public static final String COL_4 = "DISTANCE"; //zapisany dystans
    public static final String COL_5 = "TIME"; //zapisany czas

    public DatabaseHelper(Context context) { //konstruktor
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) { //tworzenie bazy danych
        db.execSQL("CREATE TABLE " + TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,DATE TEXT, DISTANCE TEXT, TIME TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { //aktualizacja bazy
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
//metoda do wstawiania danych do bazy (zapis wynikow)
    public boolean insertData(String nameDb, String dateDb, String distanceDb, String timeDb) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, nameDb);
        contentValues.put(COL_3, dateDb);
        contentValues.put(COL_4, distanceDb);
        contentValues.put(COL_5, timeDb);
        long result = db.insert(TABLE_NAME,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }
//pobranie wszystkich danych, select all do wyswietlenia
    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+TABLE_NAME,null);
        return res;
    }
//update danych, na razie nieuzywany
    public boolean updateData(String id, String nameDb, String dateDb, String distanceDb, String timeDb) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,id);
        contentValues.put(COL_2, nameDb);
        contentValues.put(COL_3, dateDb);
        contentValues.put(COL_4, distanceDb);
        contentValues.put(COL_5, timeDb);
        db.update(TABLE_NAME, contentValues, "ID = ?",new String[] { id });
        return true;
    }
//usuwanie danych, jak wyzej
    public Integer deleteData (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?",new String[] {id});
    }
}