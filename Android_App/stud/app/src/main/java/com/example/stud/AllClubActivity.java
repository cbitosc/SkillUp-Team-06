package com.example.stud;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.Nullable;

public class AllClubActivity extends Activity {

 String[] ClubArray={"Communicando","ROBOTICS","GCD","IEEECBIT","COSC"};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all);
        final ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.listtemplate,R.id.lblcountryname ,ClubArray);
        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               String clicked = (String) adapter.getItem(position);
               if(clicked.equals(ClubArray[0]))
               {
                   Intent i = new Intent(AllClubActivity.this, Communicando.class);
                   startActivity(i);
               }
               else if(clicked.equals(ClubArray[1]))
               {
                   Intent i = new Intent(AllClubActivity.this, Robotics.class);
                   startActivity(i);
               }
               else if(clicked.equals(ClubArray[2]))
               {
                   Intent i = new Intent(AllClubActivity.this, Gcd.class);
                   startActivity(i);
               }
               else if(clicked.equals(ClubArray[3]))
               {
                   Intent i = new Intent(AllClubActivity.this, Ieee.class);
                   startActivity(i);
               }
               else
               {
                   Intent i = new Intent(AllClubActivity.this, Cosc.class);
                   startActivity(i);
               }
            }
        });
    }
}
