package com.example.federico.nawe;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionClass {
    String classs = "com.mysql.jdbc.Driver";

    String url = "jdbc:mysql://192.168.1.13/nawé";
    String un = "fede";
    String password = "admin";



    @SuppressLint("NewApi")
    public Connection CONN() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;
        try {

            Class.forName(classs);

            conn = DriverManager.getConnection(url, un, password);


            conn = DriverManager.getConnection(ConnURL);
        } catch (SQLException se) {
            Log.e("ERRO", se.getMessage() + "HAAAAAAAAAAAAAAAAAAAAAAAAAA");
        } catch (ClassNotFoundException e) {
            Log.e("ERRO", e.getMessage()+ "HEEEEEEEEEEEEEEEEEEEEEEE");
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage() + "HIIIIIIIIIIIIIIIIIIIII");
        }
        return conn;
    }
}
