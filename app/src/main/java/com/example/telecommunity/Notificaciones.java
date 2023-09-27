package com.example.telecommunity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.telecommunity.adapter.NotificationAdapter;
import com.example.telecommunity.entity.NotificationItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class Notificaciones extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificaciones);

        recyclerView = findViewById(R.id.recyclerView); // Añade un RecyclerView en tu XML
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<NotificationItem> notificationList = new ArrayList<>();
        notificationList.add(new NotificationItem(R.drawable.gato, "Marcelo ha comentado", "Hace 5 minutos", "🔥Necesitamos barra para basket masculinoooo!!"));
        notificationList.add(new NotificationItem(R.drawable.gato, "Sara irá a un evento", "Hace 10 minutos", "¡Mira esta foto increíble que Sara ha compartido en su perfil! 😍"));
        notificationList.add(new NotificationItem(R.drawable.bell, "Angel participará una nueva actividad", "Hace 20 minutos", "¡Hay que Dotear!!!!😍"));

        notificationAdapter = new NotificationAdapter(notificationList,this);
        recyclerView.setAdapter(notificationAdapter);


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
                else if (menuItem.getItemId() == R.id.navigation_buscar) {
                    // Ir a la actividad BUscar
                    startActivity(new Intent(getApplicationContext(), BuscarActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }

                // Agrega más casos 'if' para otros items si es necesario
                return false;
            }
        });
    }







}
