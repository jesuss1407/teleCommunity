package com.example.telecommunity.DelegadoGeneral;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.telecommunity.Notificaciones;
import com.example.telecommunity.R;

import com.example.telecommunity.RealizarDonacion;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdmDonacion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_donacion);

        //Navbar

        // Encontrar la BottomNavigationView y setear el item seleccionado
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.navigation_admdonacion);

        // Configurar el Listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if (menuItem.getItemId() == R.id.navigation_estadistica) {
                    // Ir a la actividad Estadisticas
                    startActivity(new Intent(getApplicationContext(), Estadisticas.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                else if (menuItem.getItemId() == R.id.navigation_actividades) {
                    // Ir a la actividad AdmActividades
                    startActivity(new Intent(getApplicationContext(), AdmActividades.class));
                    overridePendingTransition(0, 0);
                    return true;
                }

                return false;
            }
        });

    }
}
