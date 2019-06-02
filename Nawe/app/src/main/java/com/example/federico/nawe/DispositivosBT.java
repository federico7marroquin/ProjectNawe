package com.example.federico.nawe;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Set;


public class DispositivosBT extends AppCompatActivity {


    private static final String TAG = "DispositivosBT"; //<-<- PARTE A MODIFICAR >->->


    Button salir, educacion, mapa;

    ListView IdLista;

    public static String EXTRA_DEVICE_ADDRESS = "device_address";


    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter mPairedDevicesArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispositivos_bt);
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();


        salir= (Button)findViewById(R.id.salir);
        educacion= (Button)findViewById(R.id.educación);
        mapa= (Button)findViewById(R.id.IdMapaBT);

        mapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(DispositivosBT.this, MapsActivity.class);
                startActivity(i);
            }
        });


        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(DispositivosBT.this, Inicio.class);
                startActivity(i);
            }
        });
        educacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(DispositivosBT.this, MapsActivity.class);
                startActivity(i);
            }
        });

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        removeNavigationShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.IdHomeNav:

                        break;

                    case R.id.IdMapNav:
                        Intent intent1 = new Intent(DispositivosBT.this, MapsActivity.class);
                        startActivity(intent1);
                        break;

                    case R.id.IdPulseraNav:
                        Intent intent2 = new Intent(DispositivosBT.this, UserInterfaz.class);
                        startActivity(intent2);
                        break;

                    case R.id.IdAyudaNav:
                        Intent intent3 = new Intent(DispositivosBT.this, Login.class);
                        startActivity(intent3);
                        break;

                    case R.id.IdPerfilNav:
                        Intent intent4 = new Intent(DispositivosBT.this, Perfil.class);
                        startActivity(intent4);
                        break;
                }


                return false;
            }
        });

    }

    @SuppressLint("RestrictedApi")
    public static void removeNavigationShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        menuView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        menuView.buildMenuView();
    }
    @Override
    public void onResume()
    {
        super.onResume();
        VerificarEstadoBT();

        mPairedDevicesArrayAdapter = new ArrayAdapter(this, R.layout.nombre_dispositivos);//<-<- PARTE A MODIFICAR >->->
        IdLista = (ListView) findViewById(R.id.IdLista);
        IdLista.setAdapter(mPairedDevicesArrayAdapter);
        IdLista.setOnItemClickListener(mDeviceClickListener);

//             Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

  //          if (pairedDevices.size() > 0) {
   //             for (BluetoothDevice device : pairedDevices) {
      //            mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
  //             }
    //     }
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView av, View v, int arg2, long arg3) {

            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            Intent i = new Intent(DispositivosBT.this, UserInterfaz.class);//<-<- PARTE A MODIFICAR >->->
            i.putExtra(EXTRA_DEVICE_ADDRESS, address);
            startActivity(i);
            //oe prro donde está la consola no la veo :v ... wait
        }
    };

    private void VerificarEstadoBT() {
        mBtAdapter= BluetoothAdapter.getDefaultAdapter();
        if(mBtAdapter==null) {
            Toast.makeText(getBaseContext(), "El dispositivo no soporta Bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            if (mBtAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth Activado...");
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);

            }
        }
    }
}