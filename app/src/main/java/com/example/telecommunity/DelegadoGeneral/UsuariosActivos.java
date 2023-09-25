package com.example.telecommunity.DelegadoGeneral;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.telecommunity.R;
import com.example.telecommunity.adapter.GeneralActividadesadapter;
import com.example.telecommunity.adapter.GeneralUsuariosAdapter;
import com.example.telecommunity.entity.GeneralUsuariosdto;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UsuariosActivos extends AppCompatActivity {


    private RecyclerView recyclerView;
    private GeneralUsuariosAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios_activos);



        // Hardcoded data
        List<GeneralUsuariosdto> actividadList = Arrays.asList(
                new GeneralUsuariosdto(20171234, "Maria Perez Gonzalez", "Estudiante", 1, "some_link"),
                new GeneralUsuariosdto(20219876, "Juan Carlos Rodriguez", "Egresado", 2, "some_link"),
                new GeneralUsuariosdto(20175678, "Ana Isabel Torres", "Estudiante", 2, "some_link"),
                new GeneralUsuariosdto(20214321, "Carlos Lopez Diaz", "Egresado", 2, "some_link"),
                new GeneralUsuariosdto(20188901, "Sofia Ramirez", "Estudiante", 2, "some_link"),
                new GeneralUsuariosdto(20179012, "Diego Santos", "Estudiante", 1, "some_link"),
                new GeneralUsuariosdto(20220123, "Laura Alvarado Reyes", "Egresado", 3, "some_link"),
                new GeneralUsuariosdto(20182345, "Pedro Vargas", "Estudiante", 2, "some_link"),
                new GeneralUsuariosdto(20193456, "Isabel Marquez Gutierrez", "Egresado", 2, "some_link"),
                new GeneralUsuariosdto(20223344, "Jose Antonio Castro", "Estudiante", 1, "some_link"),
                new GeneralUsuariosdto(20173211, "Carmen Morales", "Egresado", 3, "some_link"),
                new GeneralUsuariosdto(20186788, "Miguel Rios", "Estudiante", 2, "some_link"),
                new GeneralUsuariosdto(20207766, "Sara Mendoza", "Egresado", 2, "some_link"),
                new GeneralUsuariosdto(20178899, "Pablo Molina", "Egresado", 3, "some_link"),
                new GeneralUsuariosdto(20220111, "Daniela Ortiz", "Egresado", 3, "some_link"),
                new GeneralUsuariosdto(20181133, "Eduardo Gutierrez", "Egresado", 3, "some_link"),
                new GeneralUsuariosdto(20195566, "Clara Rodriguez", "Estudiante", 2, "some_link"),
                new GeneralUsuariosdto(20216655, "Ricardo Perez", "Egresado", 3, "some_link"),
                new GeneralUsuariosdto(20192233, "Luisa Hernandez", "Estudiante", 1, "some_link"),
                new GeneralUsuariosdto(20229988, "Hernan Suarez", "Estudiante", 1, "some_link"),
                new GeneralUsuariosdto(20174455, "Victoria Alvarado", "Estudiante", 2, "some_link"),
                new GeneralUsuariosdto(20215544, "Oscar Luna", "Egresado", 2, "some_link"),
                new GeneralUsuariosdto(20182211, "Lucia Vargas", "Estudiante", 2, "some_link"),
                new GeneralUsuariosdto(20203322, "Julia Castillo", "Estudiante", 2, "some_link"),
                new GeneralUsuariosdto(20178877, "Fernando Ruiz", "Egresado", 2, "some_link"),
                new GeneralUsuariosdto(20215544, "Manuel Torres", "Estudiante", 1, "some_link"),
                new GeneralUsuariosdto(20177666, "Cristina Serrano", "Estudiante", 1, "some_link"),
                new GeneralUsuariosdto(20174333, "Antonio Miguel Garcia", "Estudiante", 1, "some_link"),
                new GeneralUsuariosdto(20215566, "Andrea Herrera", "Egresado", 3, "some_link"),
                new GeneralUsuariosdto(20194433, "Gabriel Lopez Alvarez", "Estudiante", 2, "some_link")
        );




        List<GeneralUsuariosdto> filteredList = new ArrayList<>();
        for (GeneralUsuariosdto state : actividadList) {
            if (state.getEstado() == 1) {
                filteredList.add(state);
            }
        }

        // Pasar la lista filtrada al adaptador
        recyclerView = findViewById(R.id.listarActividades);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GeneralUsuariosAdapter(filteredList);
        recyclerView.setAdapter(adapter);



        //Navbar

        // Encontrar la BottomNavigationView y setear el item seleccionado
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.navigation_alumnos);

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
                else if (menuItem.getItemId() == R.id.navigation_actividades) {
                    // Ir a la actividad Actividades
                    startActivity(new Intent(getApplicationContext(), AdmActividades.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                else if (menuItem.getItemId() == R.id.navigation_alumnos) {
                    // Ir a la actividad AdministrarDonaión
                    Intent intent = new Intent(UsuariosActivos.this, AdmUsuarios.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // Para asegurar que AdmActividades sea la única actividad en la pila
                    startActivity(intent);
                    return true;
                }

                return false;
            }
        });


    }
}