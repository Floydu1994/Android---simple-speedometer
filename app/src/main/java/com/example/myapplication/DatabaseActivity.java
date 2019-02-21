package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


public class DatabaseActivity extends AppCompatActivity {

    private Context context;
    DatabaseHelper myDb;
    SQLiteDatabase myDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.db_layout);
        Intent intent = getIntent();
        context = this;
        myDb = new DatabaseHelper(getApplicationContext());

        myDb.getAllData();

//deklaracja tabeli
        TableLayout tableLayout = (TableLayout) findViewById(R.id.tablelayout);
        TableRow rowHeader = new TableRow(context);
        rowHeader.setBackgroundColor(Color.parseColor("#c0c0c0"));
        rowHeader.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        String[] headerText = {"ID", "NAME", "DATE", "TOTAL DISTANCE", "TIME"};
        for (String c : headerText) {
            TextView tv = new TextView(this);
            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(18);
            tv.setPadding(5, 5, 5, 5);
            tv.setText(c);
            rowHeader.addView(tv);
        }
        tableLayout.addView(rowHeader);
// pobranie danych z bazy danych sqlite i dodanie ich do tabeli
        // otwarcie baze danych do odczytu
        // tworzenie instancje DatabaseHelper
        DatabaseHelper dataHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dataHelper.getReadableDatabase();
        // Start the transaction.
        db.beginTransaction();

        try {
            String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_NAME;
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    // odczyt danych z kolumn
                    int score_id = cursor.getInt(cursor.getColumnIndex("ID"));
                    String score_name = cursor.getString(cursor.getColumnIndex("NAME"));
                    String score_date = cursor.getString(cursor.getColumnIndex("DATE"));
                    String score_distance = cursor.getString(cursor.getColumnIndex("DISTANCE"));
                    String score_time = cursor.getString(cursor.getColumnIndex("TIME"));

                    // kolumny danych
                    TableRow row = new TableRow(context);
                    row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT));
                    String[] colText = {score_id + "", score_name, score_date, score_distance, score_time};
                    for (String text : colText) {
                        TextView tv = new TextView(this);
                        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                TableRow.LayoutParams.WRAP_CONTENT));
                        tv.setGravity(Gravity.CENTER);
                        tv.setTextSize(16);
                        tv.setPadding(5, 5, 5, 5);
                        tv.setText(text);
                        row.addView(tv);
                    }
                    tableLayout.addView(row);

                }

            }
            db.setTransactionSuccessful();

        } catch (SQLiteException e) {
            e.printStackTrace();

        } finally {
            db.endTransaction();
            // End the transaction.
            db.close();
            // Close database
        }

        Button back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (DatabaseActivity.this, SecondActivity.class);
                startActivity(intent);
            }
        });
    }
}