package com.example.federico.nawe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Login extends AppCompatActivity {

    private EditText correo, contraseña;
    private Button ingresar, registrarse;
    private ProgressDialog progressDialog;
    private ConnectionClass connectionClass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        correo = (EditText) findViewById(R.id.IdLoginCorreo);
        contraseña = (EditText) findViewById(R.id.IdLoginContraseña);

        ingresar = (Button) findViewById(R.id.IdBotonIngresar);
        registrarse = (Button) findViewById(R.id.IdRegistroLogin);

        connectionClass = new ConnectionClass();

        progressDialog = new ProgressDialog(this);

        ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Login.this, DispositivosBT.class);
                startActivity(i);
                //Dologin dologin=new Dologin();
               // dologin.execute();
            }
        });

        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, Registro.class);
                startActivity(i);
            }
        });

    }



    private class Dologin extends AsyncTask<String,String,String>{


        String namestr=correo.getText().toString();
        String passstr=contraseña.getText().toString();
        String z="";
        boolean isSuccess=false;

        String nm,password;


        @Override
        protected void onPreExecute() {


            progressDialog.setMessage("Loading...");
            progressDialog.show();


            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            if(namestr.trim().equals("") ||passstr.trim().equals(""))
                z = "Please enter all fields....";
            else
            {
                try {
                    Connection con = connectionClass.CONN();
                    if (con == null) {
                        z = "Please check your internet connection";
                    } else {

                        String query=" select * from registro where correo='"+namestr+"' and contraseña = '"+passstr+"'";


                        Statement stmt = con.createStatement();
                        ResultSet rs=stmt.executeQuery(query);

                        while (rs.next())

                        {
                            nm= rs.getString(5);
                            password=rs.getString(3);




                            if(nm.equals(namestr)&& password.equals(passstr))
                            {

                                isSuccess=true;
                                z = "Login successfull";

                            }

                            else

                                isSuccess=false;



                        }
                    }
                }
                catch (Exception ex)
                {
                    isSuccess = false;
                    z = "Exceptions"+ex + "hasdasdfadsf";
                }
            }
            return z;        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getBaseContext(),""+z,Toast.LENGTH_LONG).show();


            if(isSuccess) {

                Intent intent=new Intent(Login.this,DispositivosBT.class);

                intent.putExtra("name",namestr);

                startActivity(intent);
            }


            progressDialog.hide();

        }
    }
}
