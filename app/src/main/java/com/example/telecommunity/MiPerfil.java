package com.example.telecommunity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import androidx.annotation.NonNull;

import com.example.telecommunity.DelegadoGeneral.AdmActividades;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MiPerfil extends AppCompatActivity {

    // ... Tus otros métodos y variables de instancia

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, InicioActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_perfil);

        //logout
        Button logout = findViewById(R.id.logout);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(MiPerfil.this, IniciarSesion.class));

            }
        });

        // Encontrar la BottomNavigationView y setear el item seleccionado
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.navigation_mi_perfil);

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
                else if (menuItem.getItemId() == R.id.navigation_inicio){
                    startActivity(new Intent(getApplicationContext(), InicioActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                }
                // Agrega más casos 'if' para otros items si es necesario
                return false;
            }
        });


        //-------temporal--------------:

    }



}
