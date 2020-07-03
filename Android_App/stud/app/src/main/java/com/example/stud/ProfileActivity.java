package com.example.stud;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ProfileActivity extends Activity {


    private static final String url = "jdbc:mysql://skillup-team-06.cxgok3weok8n.ap-south-1.rds.amazonaws.com:3306/project?characterEncoding=utf8&useSSL=false&useUnicode=true";
    private static final String user = "admin";
    private static final String pass = "coscskillup";
    TextView rollno,name,branch,year,grade,email;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        rollno = (TextView)findViewById(R.id.rollno);
        name = (TextView)findViewById(R.id.name);
        branch =(TextView)findViewById(R.id.branch);
        year = (TextView)findViewById(R.id.year);
        grade = (TextView)findViewById(R.id.grade);
        email = (TextView)findViewById(R.id.email);
        Bundle extras = getIntent().getExtras();
        final String Roll = extras.getString("nam");
        rollno.setText("Roll No: "+Roll);
        Profiler profiler = new Profiler();
        profiler.execute(Roll);
    }
    String[] result = new String[5];
    private class Profiler extends AsyncTask<String,Void,String>
    {
        String res ="";
        @Override
        protected String doInBackground(String... strings) {
            try
            {
                String rolln = strings[0];
                int r = Integer.parseInt(rolln);
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                int ye=0;
                int gr=0;
                PreparedStatement st = con.prepareStatement("select name,branch,year,grade,emailid from profile where stuid = ? ");
                st.setInt(1,r);
                ResultSet rs = st.executeQuery();
                while (rs.next())
                {
                    result[0]= rs.getString(1).toString();
                    result[1]= rs.getString(2).toString();
                    ye = rs.getInt(3);
                    gr= rs.getInt(4);
                    result[2] = Integer.toString(ye);
                    result[3] = Integer.toString(gr);
                    result[4]= rs.getString(5).toString();
                }
                res = "done";
            }
            catch (Exception e)
            {
                e.printStackTrace();
                res = null;
            }
            return res;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(ProfileActivity.this,"Loading Profile",Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s!=null)
            {
                name.setText("Name: "+result[0]);
                branch.setText("Branch: "+result[1]);
                year.setText("Year: "+result[2]);
                grade.setText("Current CGPA: "+result[3]);
                email.setText("EmailId: "+result[4]);
            }
        }
    }
}
