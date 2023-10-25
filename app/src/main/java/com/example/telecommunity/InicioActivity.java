package com.example.telecommunity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.telecommunity.adapter.NotificationAdapter;
import com.example.telecommunity.adapter.PublicacionAdapter;
import com.example.telecommunity.entity.NotificationItem;
import com.example.telecommunity.entity.Publicaciondto;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class InicioActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PublicacionAdapter publicacionAdapter;


    @Override
    public void onBackPressed() {
        finishAffinity();  // Esto cerrará todas las actividades y saldrá de la aplicación
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Publicaciondto> publicacionList = new ArrayList<>();
        //publicacionList.add(new Publicaciondto(R.drawable.gato, "Marcelo Rojas", "Basquet Damas","25/09/23","13:30",2,"Pabellón V","Únanse a basquet damas, pronto empezaremos con las prácticas en la cancha de minas, nos falta barra tambien!!!",0,  -34.6083, -58.3712));
        //publicacionList.add(new Publicaciondto(R.drawable.gato, "Marcelo Rojas", "Futsal Damas","02/09/23","08:27",3,"Pabellón V","En futsal damas ya tenemos el equipo formado, pero igual estamos buscando practicar las barras, los esperamos",R.drawable.futsal_damas,  -34.6083, -58.3712));
        //publicacionList.add(new Publicaciondto(R.drawable.gato, "Marcelo Rojas", "Ajedrez","18/08/23","19:55",4,"Pabellón V","No hay ni un solo teleco que juegue ajedrez, por favor, necesitamos al menos a una persona que vaya camotito",0,  -34.6083, -58.3712));

        publicacionAdapter = new PublicacionAdapter(publicacionList,this);
        recyclerView.setAdapter(publicacionAdapter);













        //Navbar

        // Encontrar la BottomNavigationView y setear el item seleccionado
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.navigation_inicio);

        // Configurar el Listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.navigation_donacion) {
                    // Ir a la actividad RealizarDonacion
                    startActivity(new Intent(getApplicationContext(), RealizarDonacion.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                else if (menuItem.getItemId() == R.id.navigation_notificacion) {
                    // Ir a la actividad RealizarDonacion
                    startActivity(new Intent(getApplicationContext(), Notificaciones.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                else if (menuItem.getItemId() == R.id.navigation_buscar) {
                    // Ir a la actividad BUscar
                    startActivity(new Intent(getApplicationContext(), BuscarActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                else if (menuItem.getItemId() == R.id.navigation_mi_perfil){
                    startActivity(new Intent(getApplicationContext(), MiPerfil.class));
                    overridePendingTransition(0,0);
                    return true;
                }
                // Agrega más casos 'if' para otros items si es necesario
                return false;
            }
        });


    }




}