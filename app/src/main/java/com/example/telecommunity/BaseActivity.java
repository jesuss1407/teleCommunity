package com.example.telecommunity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.telecommunity.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.View;
public class BaseActivity extends AppCompatActivity {
    private TextView titleTextView;
    BottomNavigationView navView;
    private boolean enMisActividades = false;
    private String userRole;
    private ImageView iconSync;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        userRole = sharedPreferences.getString("tipoUsuario", "usuario");


        iconSync = findViewById(R.id.iconSync);
        titleTextView = findViewById(R.id.titleTextView);
        navView = findViewById(R.id.bottomNavigationView);

        titleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enMisActividades) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                    titleTextView.setText("Inicio");
                    enMisActividades = false;
                } else {
                    Fragment fragment = "Usuario".equals(userRole) ? new MisEventosFragment() : new MisActividadesFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                    titleTextView.setText("Usuario".equals(userRole) ? "Mis Eventos" : "Mis Actividades");
                    enMisActividades = true;
                }
            }
        });

        iconSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enMisActividades) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                    titleTextView.setText("Inicio");
                    enMisActividades = false;
                } else {
                    Fragment fragment = "Usuario".equals(userRole) ? new MisEventosFragment() : new MisActividadesFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                    titleTextView.setText("Usuario".equals(userRole) ? "Mis Eventos" : "Mis Actividades");
                    enMisActividades = true;
                }
            }
        });



        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                int id = item.getItemId();
                if (id == R.id.navigation_inicio) {
                    if (enMisActividades) {
                        selectedFragment = "Usuario".equals(userRole) ? new MisEventosFragment() : new MisActividadesFragment();
                        titleTextView.setText("Usuario".equals(userRole) ? "Mis Eventos" : "Mis Actividades");
                    } else {
                        selectedFragment = new HomeFragment();
                        titleTextView.setText("Inicio");
                    }
                } else if (id == R.id.navigation_buscar) {
                    selectedFragment = new SearchFragment();
                    titleTextView.setText("Buscar");
                } else if (id == R.id.navigation_notificacion) {
                    selectedFragment = new NotificationFragment();
                    titleTextView.setText("Notificaciones");
                } else if (id == R.id.navigation_donacion) {
                    selectedFragment = new DonationFragment();
                    titleTextView.setText("Donaciones");
                } else if (id == R.id.navigation_mi_perfil) {
                    selectedFragment = new ProfileFragment();
                    titleTextView.setText("Mi Perfil");
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                    updateIconVisibility(selectedFragment); // Agregar esta línea para actualizar la visibilidad del ícono

                }

                return true;
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navView.setSelectedItemId(R.id.navigation_inicio);
            titleTextView.setText("Inicio");
        }

    }
    public void crearPublicacion(View view) {
        Intent intent = new Intent(this, CrearPublicacionActivity.class);
        startActivity(intent);
    }

    private void updateIconVisibility(Fragment activeFragment) {
        if (activeFragment instanceof HomeFragment || activeFragment instanceof MisActividadesFragment || activeFragment instanceof MisEventosFragment) {
            iconSync.setVisibility(View.VISIBLE);
        } else {
            iconSync.setVisibility(View.GONE);
        }
    }

}