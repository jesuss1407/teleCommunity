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
import com.example.telecommunity.entity.NotificationItem;
import com.example.telecommunity.entity.Publicaciondto;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class InicioActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        recyclerView = findViewById(R.id.recyclerViewInicio);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Publicaciondto> publicacionList = new ArrayList<>();
        publicacionList.add(new Publicaciondto(R.drawable.gato,));
        publicacionList.add(new Publicaciondto(R.drawable.gato,));
        publicacionList.add(new Publicaciondto(R.drawable.bell,));


































        //Navbar

        // Encontrar la BottomNavigationView y setear el item seleccionado
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.navigation_notificacion);

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