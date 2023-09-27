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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Publicaciondto> publicacionList = new ArrayList<>();
        publicacionList.add(new Publicaciondto(R.drawable.gato, "Marcelo Rojas", "Basquet Damas","25/09/23","13:30",2,"Pabellón","aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));
        publicacionList.add(new Publicaciondto(R.drawable.gato, "Marcelo Rojas", "Basquet Damas","25/09/23","13:30",2,"Pabellón","aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));
        publicacionList.add(new Publicaciondto(R.drawable.gato, "Marcelo Rojas", "Basquet Damas","25/09/23","13:30",2,"Pabellón","aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));

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
                if (menuItem.getItemId() == R.id.navigation_mi_perfil) {
                    // Ir a la actividad RealizarDonacion
                    startActivity(new Intent(getApplicationContext(), MiPerfil.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                else if (menuItem.getItemId() == R.id.navigation_donacion) {
                    // Ir a la actividad RealizarDonacion
                    startActivity(new Intent(getApplicationContext(), RealizarDonacion.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                // Agrega más casos 'if' para otros items si es necesario
                return false;
            }
        });
    }
}