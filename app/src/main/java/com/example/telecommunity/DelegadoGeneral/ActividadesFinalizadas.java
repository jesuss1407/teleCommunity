package com.example.telecommunity.DelegadoGeneral;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.telecommunity.R;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class ActividadesFinalizadas extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_actividades_finalizadas);


        //Navbar

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.navigation_actividades);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.navigation_actividades) {
                    // Redirigir a AdmActividades
                    Intent intent = new Intent(ActividadesFinalizadas.this, AdmActividades.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // Para asegurar que AdmActividades sea la única actividad en la pila
                    startActivity(intent);
                    return true; // devuelve true para indicar que el evento ha sido manejado
                }

                if (menuItem.getItemId() == R.id.navigation_estadistica) {
                    // Ir a la actividad Estadisticas
                    startActivity(new Intent(getApplicationContext(), Estadisticas.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                else if (menuItem.getItemId() == R.id.navigation_admdonacion) {
                    // Ir a la actividad AdministrarDonaión
                    startActivity(new Intent(getApplicationContext(), AdmDonacion.class));
                    overridePendingTransition(0, 0);
                    return true;
                }

                return false; // devuelve false para indicar que el evento no ha sido manejado
            }


        });



    }


    @Override
    public void onBackPressed() {
        // Redirigir a AdmActividades
        Intent intent = new Intent(this, AdmActividades.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Para asegurar que AdmActividades sea la única actividad en la pila
        startActivity(intent);
    }






}
