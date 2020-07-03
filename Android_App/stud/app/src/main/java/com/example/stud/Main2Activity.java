package com.example.stud;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Main2Activity extends AppCompatActivity {
 Button but1,but2,but3,but4;
 TextView t2,t3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        but1 = (Button)findViewById(R.id.but1);
        but3 = (Button)findViewById(R.id.but3);
        but2 = (Button)findViewById(R.id.but2);
        but4 = (Button)findViewById(R.id.but4);
        t3 = (TextView)findViewById(R.id.tv3);
        Bundle extras=getIntent().getExtras();
        final String name=extras.getString("name");


        but1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               Intent i = new Intent(Main2Activity.this,ClubActivity.class);
                i.putExtra("nam",name);
               startActivity(i);
            }
        });
        but2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Main2Activity.this,AllClubActivity.class);
                i.putExtra("nam",name);
                startActivity(i);
            }
        });
        but3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Main2Activity.this,ProfileActivity.class);
                i.putExtra("nam",name);
                startActivity(i);
            }
        });
        but4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.Myper, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear();
                editor.commit();
                Intent i = new Intent(Main2Activity.this,MainActivity.class);
                startActivity(i);
            }
        });
    }
}
