package com.example.telecommunity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class ProfileFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //logout
        Button logout = view.findViewById(R.id.logout);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(getActivity(), IniciarSesion.class));
            }
        });

        // Encontrar la BottomNavigationView y setear el item seleccionado
        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.navigation_mi_perfil);

        // Configurar el Listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.navigation_donacion) {
                    // Ir a la actividad RealizarDonacion
                    startActivity(new Intent(getActivity(), RealizarDonacion.class));
                    getActivity().overridePendingTransition(0, 0);
                    return true;
                } else if (menuItem.getItemId() == R.id.navigation_notificacion) {
                    // Ir a la actividad RealizarDonacion
                    startActivity(new Intent(getActivity(), Notificaciones.class));
                    getActivity().overridePendingTransition(0, 0);
                    return true;
                } else if (menuItem.getItemId() == R.id.navigation_buscar) {
                    // Ir a la actividad BUscar
                    startActivity(new Intent(getActivity(), BuscarActivity.class));
                    getActivity().overridePendingTransition(0, 0);
                    return true;
                } else if (menuItem.getItemId() == R.id.navigation_inicio) {
                    startActivity(new Intent(getActivity(), InicioActivity.class));
                    getActivity().overridePendingTransition(0, 0);
                    return true;
                }
                // Agrega más casos 'if' para otros items si es necesario
                return false;
            }
        });

        // ... El resto de tu código

        return view;
    }
}