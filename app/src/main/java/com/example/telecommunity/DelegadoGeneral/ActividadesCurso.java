package com.example.telecommunity.DelegadoGeneral;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.telecommunity.R;

import com.example.telecommunity.adapter.GeneralActividadesadapter;
import com.example.telecommunity.entity.GeneralActividadesdto;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ActividadesCurso extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GeneralActividadesadapter adapter;
    private List<GeneralActividadesdto> actividadList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_actividades_curso);



        // Hardcoded data
        List<GeneralActividadesdto> actividadList = Arrays.asList(
                new GeneralActividadesdto(80, "FutbolFemenino", 56, 1, "some_link"),
                new GeneralActividadesdto(52, "FutbolMasculino", 24, 2, "some_link"),
                new GeneralActividadesdto(100, "BasquetFemenino", 49, 2, "some_link"),
                new GeneralActividadesdto(21, "BasquetMasculino", 85, 2, "some_link"),
                new GeneralActividadesdto(96, "Ajedrez", 6, 2, "some_link"),
                new GeneralActividadesdto(65, "Valorant", 3, 1, "some_link"),
                new GeneralActividadesdto(96, "Dota", 85, 3, "some_link"),
                new GeneralActividadesdto(1, "VoleyballFemenino", 3, 2, "some_link"),
                new GeneralActividadesdto(73, "VoleyballMasculino", 98, 2, "some_link"),
                new GeneralActividadesdto(62, "NatacionFemenino", 56, 1, "some_link"),
                new GeneralActividadesdto(27, "NatacionMasculino", 34, 3, "some_link"),
                new GeneralActividadesdto(47, "TenisFemenino", 15, 2, "some_link"),
                new GeneralActividadesdto(41, "TenisMasculino", 3, 2, "some_link"),
                new GeneralActividadesdto(69, "PingPong", 89, 3, "some_link"),
                new GeneralActividadesdto(53, "KarateFemenino", 88, 3, "some_link"),
                new GeneralActividadesdto(45, "KarateMasculino", 53, 3, "some_link"),
                new GeneralActividadesdto(13, "Judo", 29, 2, "some_link"),
                new GeneralActividadesdto(32, "Boxeo", 65, 3, "some_link"),
                new GeneralActividadesdto(15, "GimnasiaFemenino", 33, 1, "some_link"),
                new GeneralActividadesdto(90, "GimnasiaMasculino", 22, 1, "some_link"),
                new GeneralActividadesdto(34, "Yoga", 9, 2, "some_link"),
                new GeneralActividadesdto(25, "CrossFit", 70, 2, "some_link"),
                new GeneralActividadesdto(83, "Ciclismo", 89, 2, "some_link"),
                new GeneralActividadesdto(56, "AtletismoFemenino", 48, 2, "some_link"),
                new GeneralActividadesdto(34, "AtletismoMasculino", 63, 2, "some_link"),
                new GeneralActividadesdto(37, "RugbyFemenino", 86, 1, "some_link"),
                new GeneralActividadesdto(95, "RugbyMasculino", 49, 1, "some_link"),
                new GeneralActividadesdto(7, "HockeyFemenino", 67, 1, "some_link"),
                new GeneralActividadesdto(68, "HockeyMasculino", 27, 3, "some_link"),
                new GeneralActividadesdto(1, "Golf", 94, 2, "some_link")
        );



        List<GeneralActividadesdto> filteredList = new ArrayList<>();
        for (GeneralActividadesdto actividad : actividadList) {
            if (actividad.getEstado() == 1) {
                filteredList.add(actividad);
            }
        }

// Pasar la lista filtrada al adaptador
        recyclerView = findViewById(R.id.listarActividades);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GeneralActividadesadapter(filteredList);
        recyclerView.setAdapter(adapter);





        //Navbar

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.navigation_actividades);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.navigation_actividades) {
                    // Redirigir a AdmActividades
                    Intent intent = new Intent(ActividadesCurso.this, AdmActividades.class);
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
