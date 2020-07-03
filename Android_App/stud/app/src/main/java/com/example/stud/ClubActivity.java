package com.example.stud;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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


public class ClubActivity extends Activity {
    TextView tvc;

    private static final String url = "jdbc:mysql://skillup-team-06.cxgok3weok8n.ap-south-1.rds.amazonaws.com:3306/project?characterEncoding=utf8&useSSL=false&useUnicode=true&allowMultiQueries=true";
    private static final String user = "admin";
    private static final String pass = "coscskillup";
    ListView lstData, lstdat1;
    SimpleAdapter ADAhere;
    Button but;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club);
        tvc = (TextView) findViewById(R.id.tvc);
        Bundle extras = getIntent().getExtras();
        final String name = extras.getString("nam");
        tvc.setText("My CLUBS");
        ConnectMySql connectMySql = new ConnectMySql();
        connectMySql.execute(name);
        lstData = (ListView) findViewById(R.id.lstData);
        but = (Button) findViewById(R.id.clubbut);
        lstData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, Object> obj = (HashMap<String, Object>) ADAhere.getItem(position);
                String itemname = obj.get("A").toString();
                Intent i = new Intent(ClubActivity.this,ActiveActivity.class);
                i.putExtra("cname",itemname);
                i.putExtra("name",name);
                startActivity(i);
            }
        });
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClubApply clubApply = new ClubApply();
                clubApply.execute(name);
            }
        });
        lstdat1 = (ListView) findViewById(R.id.applylist);
        lstdat1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, Object> obj = (HashMap<String, Object>) ADAhere.getItem(position);
                String itemname = obj.get("A").toString();
                ClubCheck clubCheck = new ClubCheck();
                clubCheck.execute(name, itemname);
            }
        });
    }

    private class ConnectMySql extends AsyncTask<String, Void, String> {
        String res = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(ClubActivity.this, "Loading clubs", Toast.LENGTH_SHORT).show();//.makeText(ListActivity.this, "Please wait...", Toast.LENGTH_SHORT)
            // .show();

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String name = params[0];
                int p = Integer.parseInt(name);
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);

                String result = "Database Connection Successful\n";
                PreparedStatement st = con.prepareStatement("select clubname from student where acceptstatus = 1 and stuid=?");
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
                ADAhere = new SimpleAdapter(ClubActivity.this, data,
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
            //txtData.setText(result);
            lstData.setAdapter(ADAhere);
        }
    }

    private class ClubApply extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String res = "";
            try {
                String name = strings[0];
                int p = Integer.parseInt(name);
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);

                String result = "Database Connection Successful\n";
                PreparedStatement st = con.prepareStatement("select distinct clubname from student where clubname not in(select clubname from student where acceptstatus = 1 and stuid=?)");
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
                ADAhere = new SimpleAdapter(ClubActivity.this, data,
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
            //txtData.setText(result);
            lstdat1.setAdapter(ADAhere);
        }
    }

    public class ClubCheck extends AsyncTask<String, Void, String> {
        String res = "";

        protected String doInBackground(String... strings) {
            try {
                String id = strings[0];
                String cname = strings[1];
                int stuid = Integer.parseInt(id);
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                System.out.println("Databasecction success");
                boolean check = false;
                String result = "Database Connection Successful\n";
                PreparedStatement stmt1 = con.prepareStatement(" select acceptstatus from student where stuid = ? and clubname=?");
                stmt1.setInt(1, stuid);
                stmt1.setString(2, cname);
                ResultSet rs1 = stmt1.executeQuery();
                if (!rs1.next()) {
                    PreparedStatement stmt = con.prepareStatement("insert into student(stuid,clubname,crole,acceptstatus) values(?,?,'member','0')");
                    stmt.setInt(1, stuid);
                    stmt.setString(2, cname);
                    int out = stmt.executeUpdate();
                    if (out == 0)
                        return "0";
                    else
                        return "1";
                } else
                    return "2";

            } catch (Exception e) {
                e.printStackTrace();
                return String.valueOf(e);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("0")) {
                Toast.makeText(ClubActivity.this, "Error", Toast.LENGTH_SHORT).show();
            } else if (s.equals("1")) {
                Toast.makeText(ClubActivity.this, "Applied", Toast.LENGTH_SHORT).show();
            } else if (s.equals("2")) {
                Toast.makeText(ClubActivity.this, "already applied", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ClubActivity.this, s, Toast.LENGTH_SHORT).show();
            }

        }

    }
}