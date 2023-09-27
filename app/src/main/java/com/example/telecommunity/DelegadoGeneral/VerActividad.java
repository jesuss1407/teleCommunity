package com.example.telecommunity.DelegadoGeneral;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.telecommunity.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class VerActividad extends AppCompatActivity {

    TextView detailApoyos, detailTitle, detailEventos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_actividad);


        //mandar info del usuario seleccionado
        detailEventos= findViewById(R.id.numeventos);
        detailTitle = findViewById(R.id.nombre);
        //detailImage = findViewById(R.id.foto);
        detailApoyos = findViewById(R.id.numapoyos);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            detailEventos.setText(bundle.getString("Numeventos"));
            detailApoyos.setText(bundle.getString("Numapoyos"));
            detailTitle.setText(bundle.getString("Title"));
        }

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
                    // Ir a la actividad AdministrarDonaión
                    startActivity(new Intent(getApplicationContext(), AdmUsuarios.class));
                    overridePendingTransition(0, 0);
                    return true;
                }

                return false;
            }
        });

    }
}