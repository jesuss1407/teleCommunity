package com.example.telecommunity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.telecommunity.R;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        BottomNavigationView navView = findViewById(R.id.bottomNavigationView);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                int id = item.getItemId();
                if (id == R.id.navigation_inicio) {
                    selectedFragment = new HomeFragment();
                } else if (id == R.id.navigation_buscar) {
                    selectedFragment = new SearchFragment();
                } else if (id == R.id.navigation_notificacion) {
                    selectedFragment = new NotificationFragment();
                } else if (id == R.id.navigation_donacion) {
                    selectedFragment = new DonationFragment();
                } else if (id == R.id.navigation_mi_perfil) {
                    selectedFragment = new ProfileFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                }

                return true;
            }
        });

                if (savedInstanceState == null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                    navView.setSelectedItemId(R.id.navigation_inicio);
                }









    }
}