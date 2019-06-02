package com.example.federico.nawe;

import android.Manifest;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserInterfaz extends AppCompatActivity {


    private EditText nombreC,numeroC,msm;
    private ProgressDialog progressDialog;
    private ConnectionClass connectionClass;
    private ListView lista;
    private ArrayAdapter ADP;
    private Switch gps, pulsera;
    private List<String > items;
    private List<String> numeros;
    private Button IdEncender,agregar;
    private TextView IdBufferIn;
    private MapsActivity map;
    private Handler bluetoothIn;
    final int handlerState = 0;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder DataStringIN = new StringBuilder();
    private ConnectedThread MyConexionBT;
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // String para la direccion MAC
    private static String address = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_interfaz);


        connectionClass = new ConnectionClass();

        progressDialog = new ProgressDialog(this);
        map = new MapsActivity();
        map.getDeviceLocation();
        map.getLocation();
        Log.d("HOLA", map.getLocation() + "// HAJKSDASDFADFGSDFG" );
        if(ActivityCompat.checkSelfPermission(
                UserInterfaz.this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED&& ActivityCompat.checkSelfPermission(
                UserInterfaz.this,Manifest
                        .permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(UserInterfaz.this,new String[]
                    { Manifest.permission.SEND_SMS,},1000);
        }
        //Enlaza los controles con sus respectivas vistas
        IdEncender = (Button) findViewById(R.id.IdEncender);
        IdBufferIn = (TextView) findViewById(R.id.IdBufferIn);
        agregar= (Button) findViewById(R.id.IdAgregarContacto);
        gps = (Switch) findViewById(R.id.switch4);
        pulsera = (Switch) findViewById(R.id.switch1);

        boolean pulse= pulsera.isChecked();
        boolean gp= gps.isChecked();

                bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {
                    String readMessage = (String) msg.obj;
                    DataStringIN.append(readMessage);

                    int endOfLineIndex = DataStringIN.indexOf("#");

                    if (endOfLineIndex > 0) {
                        String dataInPrint = DataStringIN.substring(0, endOfLineIndex);
                        IdBufferIn.setText("Dato: " + dataInPrint);
                        if(dataInPrint.equals("Se envía petición"))
                        {
                            String ubi= map.getLocation().toString();
                            String[] ubic = ubi.split(":");
                            String a = ubic[1].substring(2);
                            String b = a.substring(0,a.length()-1);
                            String gog= "https://www.google.com/maps/@" +b+",15z";
                            for(String num: numeros){
                                if(gp==true)
                                {
                                    enviarMensaje(num, gog);
                                }
                                    enviarMensaje(num, "");
                            }

                        }

                        DataStringIN.delete(0, DataStringIN.length());
                    }
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter(); // get Bluetooth adapter
        VerificarEstadoBT();

        // Configuracion onClick listeners para los botones
        // para indicar que se realizara cuando se detecte
        // el evento de Click
        IdEncender.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                MyConexionBT.write("1");
            }
        });



        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.IdHomeNav:
                        Intent intent1 = new Intent(UserInterfaz.this, DispositivosBT.class);
                        startActivity(intent1);
                        break;

                    case R.id.IdMapNav:

                        Intent intent4 = new Intent(UserInterfaz.this, MapsActivity.class);
                        startActivity(intent4);
                        break;

                    case R.id.IdPulseraNav:

                        break;

                    case R.id.IdAyudaNav:
                        Intent intent3 = new Intent(UserInterfaz.this, Login.class);
                        startActivity(intent3);
                        break;

                    case R.id.IdPerfilNav:
                        Intent intent2 = new Intent(UserInterfaz.this, Perfil.class);
                        startActivity(intent2);
                        break;
                }


                return false;
            }
        });

        nombreC= findViewById(R.id.IdNombrePulsera);
        numeroC= findViewById(R.id.IdNumeroPulsera);
        msm= findViewById(R.id.IdMensaje);
        lista = findViewById(R.id.IdListaContactos);
        items= new ArrayList<>();
        ADP= new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1,items);
        lista.setAdapter(ADP);

        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                items.add(nombreC.getText().toString()+" - "+ numeroC.getText().toString());
                ADP.notifyDataSetChanged();
                nombreC.setText("");
                numeroC.setText("");
                msm.setText("");
                UserInterfaz.registrarContacto registrar = new UserInterfaz.registrarContacto();
                registrar.execute("");

            }
        });

    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException
    {

        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //Consigue la direccion MAC desde DeviceListActivity via intent
        Intent intent = getIntent();
        //Consigue la direccion MAC desde DeviceListActivity via EXTRA
        address = intent.getStringExtra(DispositivosBT.EXTRA_DEVICE_ADDRESS);//<-<- PARTE A MODIFICAR >->->
        //Setea la direccion MAC
//        BluetoothDevice device = btAdapter.getRemoteDevice(address);

//        try
//        {
//            btSocket = createBluetoothSocket(device);
//        } catch (IOException e) {
//            Toast.makeText(getBaseContext(), "La creacción del Socket fallo", Toast.LENGTH_LONG).show();
//        }
//        // Establece la conexión con el socket Bluetooth.
//        try
//        {
//            btSocket.connect();
//        } catch (IOException e) {
//            try {
//                btSocket.close();
 //           } catch (IOException e2) {}
  //      }
 //       MyConexionBT = new ConnectedThread(btSocket);
//        MyConexionBT.start();
    }

//    @Override
//    public void onPause()
//    {
//        super.onPause();
//        try
//        { // Cuando se sale de la aplicación esta parte permite
//            // que no se deje abierto el socket
//            btSocket.close();
//        } catch (IOException e2) {}
//    }

    //Comprueba que el dispositivo Bluetooth Bluetooth está disponible y solicita que se active si está desactivado
    private void VerificarEstadoBT() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "El dispositivo no soporta bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }


    //Crea la clase que permite crear el evento de conexion
    private class ConnectedThread extends Thread
    {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket)
        {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try
            {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run()
        {
            byte[] buffer = new byte[256];
            int bytes;

            // Se mantiene en modo escucha para determinar el ingreso de datos
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    // Envia los datos obtenidos hacia el evento via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //Envio de trama
        public void write(String input)
        {
            try {
                mmOutStream.write(input.getBytes());
            }
            catch (IOException e)
            {
                //si no es posible enviar datos se cierra la conexión
                Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void enviarMensaje (String numero, String mensaje){
        try {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(numero,null,mensaje,null,null);
            Toast.makeText(getApplicationContext(), "Mensaje Enviado.", Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Mensaje no enviado, datos incorrectos.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public class registrarContacto extends AsyncTask<String, String, String> {


        String name = nombreC.getText().toString();
        String mensaje=msm.getText().toString();
        String GPS= gps.getText().toString();
        String cel= numeroC.getText().toString();
        String z = "";
        boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            if (name.trim().equals("") || mensaje.trim().equals("") || GPS.trim().equals("") || cel.trim().equals(""))
                z = "Please enter all fields....";
            else {

                try {
                    Connection con = connectionClass.CONN();
                    if (con == null) {
                        z = "Please check your internet connection";
                    } else {

                        String query = "insert into contactos(nombre,numero,gps,mensaje) values('" + name + "','" + cel + "','" + gps + "','" + mensaje + "')";

                        Statement stmt = con.createStatement();
                        stmt.executeUpdate(query);

                        z = "Register successfull";
                        isSuccess = true;


                    }
                } catch (Exception ex) {
                    isSuccess = false;
                    z = "Exceptions" + ex;
                }

            }
                return z;
            }

        @Override
        protected void onPostExecute(String s) {

            Toast.makeText(getBaseContext(), "" + z, Toast.LENGTH_LONG).show();


            if (isSuccess) {
                startActivity(new Intent(UserInterfaz.this, DispositivosBT.class));

            }


            progressDialog.hide();
        }
    }

}