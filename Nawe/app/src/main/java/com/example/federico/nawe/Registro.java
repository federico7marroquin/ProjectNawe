package com.example.federico.nawe;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.Statement;

public class Registro extends AppCompatActivity {

    private EditText nombre, apellido, contraseña,contraseñaConfirm, fecha,correo, celular;
    private Button registrar;
    private ProgressDialog progressDialog;
    private ConnectionClass connectionClass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        nombre=(EditText)findViewById(R.id.IdName);
        apellido = (EditText)findViewById(R.id.IdApellido);
        contraseña=(EditText)findViewById(R.id.IdContraseña);
        contraseñaConfirm=(EditText)findViewById(R.id.IdContraseñaConfirm);
        fecha=(EditText)findViewById(R.id.IdFecha);
        correo=(EditText)findViewById(R.id.IdCorreo);
        celular=(EditText)findViewById(R.id.IdPhone);
        registrar= (Button)findViewById(R.id.IdBotonRegistro);

        connectionClass = new ConnectionClass();

        progressDialog = new ProgressDialog(this);

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Registro.Doregister doregister = new Registro.Doregister();
                doregister.execute("");
            }
        });
    }

    public class Doregister extends AsyncTask<String, String, String> {


        String name = nombre.getText().toString();
        String lastName=apellido.getText().toString();
        String email= correo.getText().toString();
        String cel= celular.getText().toString();
        String da=fecha.getText().toString();
        String contra = contraseña.getText().toString();
        String contraConfirm= contraseñaConfirm.getText().toString();
        String z = "";
        boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            if (name.trim().equals("") || lastName.trim().equals("")|| email.trim().equals("")|| cel.trim().equals("")|| da.trim().equals("")
                    || contra.trim().equals("")|| contraConfirm.trim().equals(""))
                z = "Please enter all fields....";
            else {
                if(contra.equals(contraConfirm))
                {

                try {
                    Connection con = connectionClass.CONN();
                    if (con == null) {
                        z = "Please check your internet connection";
                    } else {

                        String query = "insert into registro(nombre,apellido,contraseña,fecha,correo,celular) values('" + name + "','" + lastName + "','"+contra+"','"+da+"','"+email+"','"+cel+"')";

                        Statement stmt = con.createStatement();
                        stmt.executeUpdate(query);

                        z = "Register successfull";
                        isSuccess = true;


                    }
                } catch (Exception ex) {
                    isSuccess = false;
                    z = "Exceptions" + ex ;
                }

                }
                else
                    z="las contraseñas no coinciden";
            }
            return z;
        }

        @Override
        protected void onPostExecute(String s) {

            Toast.makeText(getBaseContext(), "" + z, Toast.LENGTH_LONG).show();


            if (isSuccess) {
                startActivity(new Intent(Registro.this, DispositivosBT.class));

            }


            progressDialog.hide();
        }
    }
}
