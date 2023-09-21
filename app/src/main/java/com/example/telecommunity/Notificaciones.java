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

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Notificaciones extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificaciones);

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
