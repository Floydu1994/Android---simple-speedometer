package com.example.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private EditText mName, mPassword;
    private Button btnLogin;
    private CheckBox mCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mName = (EditText) findViewById(R.id.etName);
        mPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        mCheckBox = (CheckBox) findViewById(R.id.checkbox);


        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //mPreferences = getSharedPreferences("tabian.com.sharedpreferencestest", Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();

        checkSharedPreferences();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //save the checkbox preference
                if(mCheckBox.isChecked()){
                    //ustawienie pola wyboru przy wyborze aplikacji
                    //mEditor.putString(getString(R.string.checkbox), "True");
                    mEditor.commit();

                    //zapis imienia
                    String name = mName.getText().toString();
                    mEditor.putString(getString(R.string.name), name);
                    mEditor.commit();

                    //zapis hasla
                    String password = mPassword.getText().toString();
                    mEditor.putString(getString(R.string.password), password);
                    mEditor.commit();

                    //Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                    Intent intent = new Intent (MainActivity.this, SecondActivity.class);
                    startActivity(intent);

                }else{
                    //ustawienie pola wyboru przy wyborze aplikacji
                    mEditor.putString(getString(R.string.checkbox), "False");

                    mEditor.commit();

                    //zapis imienia
                    mEditor.putString(getString(R.string.name), "");
                    mEditor.commit();

                    //zapis hasla
                    mEditor.putString(getString(R.string.password), "");
                    mEditor.commit();
                }
            }
        });

    }

    /**
     * sprawdzenie SharedPreferencec i ich odpowiednie ustawienie
     */
    private void checkSharedPreferences(){
        String checkbox = mPreferences.getString(getString(R.string.checkbox), "False");
        String name = mPreferences.getString(getString(R.string.name), "");
        String password = mPreferences.getString(getString(R.string.password), "");



        mName.setText(name);
        mPassword.setText(password);

        if(checkbox.equals("True")){
            mCheckBox.setChecked(true);
        }else{
            mCheckBox.setChecked(false);
        }

    }
}
