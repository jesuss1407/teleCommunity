package com.example.telecommunity.DelegadoGeneral;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.telecommunity.IniciarSesion;
import com.example.telecommunity.Notificaciones;
import com.example.telecommunity.R;

import com.example.telecommunity.RealizarDonacion;
import com.example.telecommunity.RegistroUsuario;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdmActividades extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.navigation_actividades);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_actividades);

        ListView listView = findViewById(R.id.opcionesActividades); // Reemplaza con el ID de tu ListView

        String[] actividadesOptions = {"Actividades en Curso", "Actividades Finalizadas", "Actividades Eliminadas"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item_actividad, R.id.tvActividadName, actividadesOptions);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0:
                    startActivity(new Intent(AdmActividades.this, ActividadesCurso.class));
                    break;
                case 1:
                    startActivity(new Intent(AdmActividades.this, ActividadesFinalizadas.class));
                    break;
                case 2:
                    startActivity(new Intent(AdmActividades.this, ActividadesEliminadas.class));
                    break;

            }
        });



        Button btnCrearPublicacion = findViewById(R.id.btnGuardarPublicacion);
        btnCrearPublicacion.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent intent = new Intent(AdmActividades.this, CrearActividad.class);
                startActivity(intent);
            }
        });

        //Navbar

        // Encontrar la BottomNavigationView y setear el item seleccionado
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.navigation_actividades);

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
                else if (menuItem.getItemId() == R.id.navigation_admdonacion) {
                    // Ir a la actividad AdministrarDonaión
                    startActivity(new Intent(getApplicationContext(), AdmDonacion.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                else if (menuItem.getItemId() == R.id.navigation_alumnos) {
                    // Ir a la actividad ver usuario
                    startActivity(new Intent(getApplicationContext(), AdmUsuarios.class));
                    overridePendingTransition(0, 0);
                    return true;
                }

                return false;
            }
        });

    }
    @Override
    public void onBackPressed() {
        finishAffinity();  // Esto cerrará todas las actividades y saldrá de la aplicación
    }
}
