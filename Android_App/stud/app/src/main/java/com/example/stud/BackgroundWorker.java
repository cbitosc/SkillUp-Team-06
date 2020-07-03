package com.example.stud;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BackgroundWorker extends AsyncTask<String,Void,String>  {


    private static final String url = "jdbc:mysql://skillup-team-06.cxgok3weok8n.ap-south-1.rds.amazonaws.com:3306/project?characterEncoding=utf8&useSSL=false&useUnicode=true";
    private static final String user = "admin";
    private static final String pass = "coscskillup";

    Context context;

    BackgroundWorker(Context ctx) {
        context = ctx;
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            String user_name = params[0];
            String password = params[1];
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, pass);
            System.out.println("Databasecction success");

            String result = "Database Connection Successful\n";
            PreparedStatement st = con.prepareStatement("select id from Login where username = ? and password = ?");
            st.setString(1,user_name);
            st.setString(2,password);
            ResultSet rs = st.executeQuery();
           while(rs.next())
           {
               result = rs.getString(1).toString();
           }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


        @Override
        protected void onPreExecute () {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute (String s){
            super.onPostExecute(s);
            Intent i = new Intent(context, Main2Activity.class);
            if (s != null) {
                Toast.makeText(context, "Welcome", Toast.LENGTH_LONG).show();
                i.putExtra("name", s);
                context.startActivity(i);
            }
            else
            {
                Toast.makeText(context,"Invalid UserName or Password",Toast.LENGTH_LONG).show();
            }

        }
    }

