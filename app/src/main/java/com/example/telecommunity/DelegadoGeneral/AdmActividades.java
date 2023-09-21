package com.example.telecommunity.DelegadoGeneral;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.telecommunity.R;

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
                    // Navegar a ActividadesCurso.java
                    startActivity(new Intent(AdmActividades.this, ActividadesCurso.class));
                    break;
                // ... otros casos
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

                // Agrega más casos 'if' para otros items si es necesario
                return false;
            }
        });

    }
}
