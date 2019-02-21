package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

// LocationService - klasa do obliczania predkości, odległości i czasu
//Zrobiona jako service (usluga) - przetwarzanie zadan bez ingerencji uzytkownika
public class LocationService extends Service implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final long INTERVAL = 1000 * 2;
    private static final long FASTEST_INTERVAL = 1000 * 1;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation, lStart, lEnd;
    static double distance = 0;
    double speed;


    private final IBinder mBinder = new LocalBinder();

    @Nullable
    @Override
    //bindowanie
    public IBinder onBind(Intent intent) {
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
        return mBinder;
    }

    //metoda do tworzenia zadania podania lokalizacji
    protected void createLocationRequest() {
        // mLocationRequest = new LocationRequest();
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onConnected(Bundle bundle) {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException e) {
        }
    }

//zarzymanie aktualizacji lokalizacji
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        distance = 0;
    }


    @Override
    public void onConnectionSuspended(int i) {

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override //Zmiana lokalizacji - obliczanie pokonanej odleglosci
    public void onLocationChanged(Location location) {
        SecondActivity.locate.dismiss();
        mCurrentLocation = location;
        if (lStart == null) {
            lStart = mCurrentLocation;
            lEnd = mCurrentLocation;
        } else
            lEnd = mCurrentLocation;

        //Wywolanie metody aktualizuje wartosci biezace odleglosci i predkosci do TextViews
        updateUI();
        //getSpeed zwraca m/s, przeliczenie na km/h
        speed = location.getSpeed() * 18 / 5;
       // speed = location.getSpeed();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public class LocalBinder extends Binder {

        public LocationService getService() {
            return LocationService.this;
        }


    }

    //metoda do aktualizacjo na zywo odleglosci i predkosci  .
    private void updateUI() {
        if (SecondActivity.p == 0) {
            distance = distance + (lStart.distanceTo(lEnd) / 1000.00);
            SecondActivity.endTime = System.currentTimeMillis();
            long diff = SecondActivity.endTime - SecondActivity.startTime;
            diff = TimeUnit.MILLISECONDS.toSeconds(diff);
            //speed = distance / diff;
            SecondActivity.time.setText("Total Time: " + diff + " seconds");
            SecondActivity.dist.setText("Total distance: " + distance + "meters");
            speed = distance/diff;
            if (speed > 0.0)
                SecondActivity.speed.setText("Current speed: " + new DecimalFormat("#.##").format(speed) + " km/hr");
            else
                SecondActivity.speed.setText(".......");

            SecondActivity.dist.setText(new DecimalFormat("#.###").format(distance) + " m's.");

            lStart = lEnd;

        }

    }


    @Override
    public boolean onUnbind(Intent intent) {
        stopLocationUpdates();
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
        lStart = null;
        lEnd = null;
        distance = 0;
        return super.onUnbind(intent);
    }
}