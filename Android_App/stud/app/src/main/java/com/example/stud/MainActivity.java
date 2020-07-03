package com.example.stud;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {

    EditText t1,t2;
    Button b1,b2;
    SharedPreferences sharedpreferences;
    public static final String Myper = "MyPrefs" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        t1 = (EditText) findViewById(R.id.t1);
        t2 = (EditText) findViewById(R.id.t2);
        b1 = (Button) findViewById(R.id.bt1);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        sharedpreferences = getSharedPreferences(Myper, Context.MODE_PRIVATE);
    }
            public void onClick(View v) {
                String username = t1.getText().toString();
                String password = t2.getText().toString();

                BackgroundWorker backgroundWorker = new BackgroundWorker(this);
                backgroundWorker.execute(username, password);
                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putString("name", username);
                editor.putString("pwd", password);
                editor.commit();
            }

}
