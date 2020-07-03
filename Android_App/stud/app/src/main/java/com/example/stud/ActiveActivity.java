package com.example.stud;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActiveActivity extends Activity {
    private static final String url = "jdbc:mysql://skillup-team-06.cxgok3weok8n.ap-south-1.rds.amazonaws.com:3306/project?characterEncoding=utf8&useSSL=false&useUnicode=true&allowMultiQueries=true";
    private static final String user = "admin";
    private static final String pass = "coscskillup";
    ListView lstData;
    SimpleAdapter ADAhere;

TextView tv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active);
        lstData= (ListView)findViewById(R.id.lstData1);
        Bundle extras = getIntent().getExtras();
        final String name = extras.getString("name");
        tv = (TextView)findViewById(R.id.tva);
        final String cname = extras.getString("cname");
        Bgm bgm = new Bgm();
        bgm.execute(cname,name);
    }

    private class Bgm extends AsyncTask<String, Void, String> {

        String res = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(ActiveActivity.this, "Loading Activities", Toast.LENGTH_SHORT).show();//.makeText(ListActivity.this, "Please wait...", Toast.LENGTH_SHORT)
            // .show();

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String tab = params[0];
                String can = params[1];
                int p = Integer.parseInt(can);
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                String result = "Database Connection Successful\n";
                PreparedStatement st = con.prepareStatement("select eventname from `"+tab+"` where stuid = ?");
                st.setInt(1, p);
                ResultSet rs = st.executeQuery();

                List<Map<String, String>> data = null;
                data = new ArrayList<Map<String, String>>();

                while (rs.next()) {
                    Map<String, String> datanum = new HashMap<String, String>();
                    datanum.put("A", rs.getString(1).toString());
                    data.add(datanum);
                }

                String[] fromwhere = {"A"};
                int[] viewswhere = {R.id.lblcountryname};
                ADAhere = new SimpleAdapter(ActiveActivity.this, data,
                        R.layout.listtemplate, fromwhere, viewswhere);

                while (rs.next()) {
                    result += rs.getString(1).toString() + "\n";
                }
                res = result;
            } catch (Exception e) {
                e.printStackTrace();
                res = e.toString();
            }
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            lstData.setAdapter(ADAhere);
            Toast.makeText(ActiveActivity.this,"Done",Toast.LENGTH_SHORT).show();
        }
    }
}