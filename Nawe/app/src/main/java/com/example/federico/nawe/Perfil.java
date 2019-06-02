package com.example.federico.nawe;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class Perfil extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(4);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.IdHomeNav:
                        Intent intent1 = new Intent(Perfil.this, DispositivosBT.class);
                        startActivity(intent1);
                        break;

                    case R.id.IdMapNav:

                        Intent intent4 = new Intent(Perfil.this, MapsActivity.class);
                        startActivity(intent4);
                        break;

                    case R.id.IdPulseraNav:
                        Intent intent2 = new Intent(Perfil.this, UserInterfaz.class);
                        startActivity(intent2);
                        break;

                    case R.id.IdAyudaNav:
                        Intent intent3 = new Intent(Perfil.this, Login.class);
                        startActivity(intent3);
                        break;

                    case R.id.IdPerfilNav:

                        break;
                }


                return false;
            }
        });

    }

}
