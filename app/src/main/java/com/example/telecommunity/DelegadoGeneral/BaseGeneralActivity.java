package com.example.telecommunity.DelegadoGeneral;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.telecommunity.ProfileFragment;
import com.example.telecommunity.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseGeneralActivity extends AppCompatActivity {

    private TextView titleTextView;
    private int fragmentIndex;
    BottomNavigationView navView;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_general);

        titleTextView = findViewById(R.id.titleTextView);
        navView = findViewById(R.id.bottomNavigationView);



        // Verificar si hay información adicional en la Intent
        if (getIntent().hasExtra("fragmentToLoad")) {
            fragmentIndex = getIntent().getIntExtra("fragmentToLoad", 0);

            // Mostrar el fragmento correspondiente
            /*Fragment fragment = new AlumnosActivosFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();*/
        }


        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                int id = item.getItemId();
                if (id == R.id.navigation_actividades) {
                    selectedFragment = new ActividadesGeneralFragment();
                    titleTextView.setText("Actividades");
                } else if (id == R.id.navigation_alumnos) {
                    selectedFragment = new AlumnosGeneralFragment();
                    titleTextView.setText("Alumnos");
                } else if (id == R.id.navigation_admdonacion) {
                    selectedFragment = new DonacionesGeneralFragment();
                    titleTextView.setText("Donaciones");
                } else if (id == R.id.navigation_estadistica) {
                    selectedFragment = new EstadisticaGeneralFragment();
                    titleTextView.setText("Estadísticas");
                } else if (id == R.id.navigation_mi_perfil) {
                    selectedFragment = new ProfileFragment();
                    titleTextView.setText("Perfil");
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                    currentFragment = selectedFragment;
                }

                return true;
            }
        });

        // Cargar el fragmento inicial
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ActividadesGeneralFragment()).commit();
            navView.setSelectedItemId(R.id.navigation_actividades);
            titleTextView.setText("Actividades");
        }


    }

    public void setTitleTextView(String title) {
        if (titleTextView != null) {
            titleTextView.setText(title);
        }
    }

    public void onBackPressed() {
        if (currentFragment instanceof ActividadesGeneralFragment || currentFragment instanceof AlumnosGeneralFragment || currentFragment instanceof DonacionesGeneralFragment || currentFragment instanceof EstadisticaGeneralFragment || currentFragment instanceof PerfilGeneralFragment) {
            finish();
        } else {
            super.onBackPressed();
        }
    }


    private void showFragment(int fragmentIndex) {
        Fragment fragment = null;

        // Determinar qué fragmento mostrar según el índice proporcionado
        switch (fragmentIndex) {
            case 1:
                fragment = new AlumnosActivosFragment();
                break;

        }

        // Reemplazar el contenedor del fragmento con el fragmento correspondiente
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}