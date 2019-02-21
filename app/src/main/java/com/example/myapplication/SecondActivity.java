package com.example.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SecondActivity extends AppCompatActivity {



    DatabaseHelper myDb; // baza danych
    LocationService myService; //usluga
    static boolean status;
    LocationManager locationManager;
    static TextView dist, time, speed, userName, date; //pola tekstowe
    Button start, pause, stop, logout, saveScore, showScore; //buttony
    static long startTime, endTime; //czasy
    ImageView image; //obrazek
    static ProgressDialog locate; //okno progresu wyswietlane gdy pobiera sie lokalizacje
    static int p = 0;
    private Calendar calendar; //kalendarz
    private SimpleDateFormat dateFormat; //format wyswietlania daty
    private String currentDate; //aktualna data, do pobrania
    private  String insertName, insertDate, insertDistance, insertTime; //zmienne do pobierania i wstawiania w bazie danych

    //podlaczenie do uslugi
    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            myService = binder.getService();
            status = true;
        }

        @Override //rozlaczenie uslugi
        public void onServiceDisconnected(ComponentName name) {
            status = false;
        }
    };
    @Override //dla nowszych api za pierwszym uruchomieniem trzeba podac zgode na dostep do karty i gps, metoda do tego
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case 1000:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED )
                {
                    Toast.makeText(this, "GRANTED", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(this, "DENIED", Toast.LENGTH_SHORT).show();
            }
            return;

        }
    }
//usluga bindowania
    void bindService() {
        if (status == true)
            return;
        Intent i = new Intent(getApplicationContext(), LocationService.class);
        bindService(i, sc, BIND_AUTO_CREATE);
        status = true;
        startTime = System.currentTimeMillis();
    }

    void unbindService() {
        if (status == false)
            return;
        Intent i = new Intent(getApplicationContext(), LocationService.class);
        unbindService(sc);
        status = false;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (status == true)
            unbindService();
    }

    @Override
    public void onBackPressed() {
        if (status == false)
            super.onBackPressed();
        else
            moveTaskToBack(true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_layout); //uzywanie dobrego layoutu
        Intent intent = getIntent(); //pobranie aktywnosci
        userName = (TextView)findViewById(R.id.UserNameGet); //pole z nazwa uzytkownika
        //pobranie nazwy uzytkownika z poprzedniego okna logowania (Main Activity) i wpisanie jej automatycznie w okno aplikacji
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = mPreferences.edit();


        final String name = mPreferences.getString(getString(R.string.name), "");
        userName.setText(name);

        //sprawdzenie dostepow
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        )
        {
            requestPermissions(new String[]
                    {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    }, 1000);
        }

        //przypisanie polom tekstowym i buttonom odpowiednych elementow z GUI
        dist = (TextView) findViewById(R.id.distancetext);
        time = (TextView) findViewById(R.id.timetext);
        speed = (TextView) findViewById(R.id.speedtext);
        date = (TextView)findViewById(R.id.date);

        start = (Button) findViewById(R.id.start); //button rozpoczenia dzialania aplikacji
        pause = (Button) findViewById(R.id.pause); //jak wyzej, pauza
        stop = (Button) findViewById(R.id.stop); //jak wyzej, stop
        logout = (Button)findViewById(R.id.btnLogout); //button do wylogowania danego uzytkownika
        saveScore = (Button)findViewById(R.id.btnSaveScore); //zapis wyniku
        showScore = (Button)findViewById(R.id.btnShowScore); //pokazanie wynikow wszystkich uzytkownikow

        image = (ImageView) findViewById(R.id.image);

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        currentDate = dateFormat.format(calendar.getTime());
        date.setText(currentDate);

        //zmienna dla bazy danych
        myDb = new DatabaseHelper(this);


//powrot do okna logowania po kliknieciu
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });
//zapisanie wyniku po kliknieciu buttonu, poprzez pobranie z pol tekstowych wartosci i wpisanie ich do bazy danych
        saveScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                insertName = userName.getText().toString().trim();
                dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
                Calendar cal = Calendar.getInstance();
                currentDate = dateFormat.format(calendar.getTime());
                insertDate = dateFormat.format(cal.getTime());
                insertDistance = dist.getText().toString().trim();
                insertTime = time.getText().toString();

                boolean isInserted = myDb.insertData(insertName,
                        insertDate,
                        insertDistance,
                        insertTime);
                if(isInserted == true)
                    Toast.makeText(SecondActivity.this,"Data Inserted",Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(SecondActivity.this,"Data not Inserted",Toast.LENGTH_LONG).show();
            }
        });
//wyswietlenie wszystkich wynikow w bazie danych, otwarcie nowej aktywnosci z tabela bazy danych
        showScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor res = myDb.getAllData();
                if (res.getCount() == 0)
                {
                    Toast.makeText(SecondActivity.this,"Error, nothing found",Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent (SecondActivity.this, DatabaseActivity.class);
                startActivity(intent);
            }
        });
//rozpoczecie dzialania aplikacji
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* metoda sprawdza, czy lokalizacja jest włączona na urzadzeniu, czy nie.
                 jesli nie, pojawi się okno dialogowe z opcją właczania gps */
                checkGPS();
                locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                    return;
                }


                if (status == false)
                    //usługa lokalizacji zostaje przywiązana, a pomiar predkosci przez gps staje sie aktywny.
                    bindService();
                locate = new ProgressDialog(SecondActivity.this);
                locate.setIndeterminate(true);
                locate.setCancelable(false);
                locate.setMessage("Getting Location...");
                locate.show();
                start.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
                pause.setText("Pause");
                stop.setVisibility(View.VISIBLE);


            }
        });
//wstrzymanie pomiaru, w przeciwienstwie do stopu nie resetuje wyniku, po kliknieciu w resume dalej mierzy od momentu zatrzymania
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pause.getText().toString().equalsIgnoreCase("pause")) {
                    pause.setText("Resume");
                    p = 1;

                } else if (pause.getText().toString().equalsIgnoreCase("Resume")) {
                    checkGPS();
                    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        //Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    pause.setText("Pause");
                    p = 0;

                }
            }
        });


        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status == true)
                    unbindService();
                start.setVisibility(View.VISIBLE);
                pause.setText("Pause");
                pause.setVisibility(View.GONE);
                stop.setVisibility(View.GONE);
                p = 0;
            }
        });
    }





    //metoda do okna dialogowego z alertem, ze gps nie dziala
    private void checkGPS(){
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {


            showGPSDisabledAlertToUser();
        }
    }


    //konfiguracja okna dialogowego z alertami
    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Enable GPS to use application")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

}