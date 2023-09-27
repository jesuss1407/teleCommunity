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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import android.widget.SearchView;
import android.view.inputmethod.InputMethodManager;

public class BuscarActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private com.google.android.material.search.SearchBar searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);


        // Inicializa y configura el campo de búsqueda
        searchView = findViewById(R.id.searchView); // Cambiado a search_bar
        searchView.setHint("Buscar");

        // Inicializar el SupportMapFragment
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();

        // Reemplazar el FragmentContainerView con el mapa
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.maps, mapFragment);
        transaction.commit();

        // Obtener una referencia al mapa
        mapFragment.getMapAsync(this);

        // Navbar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.navigation_notificacion);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.navigation_mi_perfil) {
                    startActivity(new Intent(getApplicationContext(), MiPerfil.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (menuItem.getItemId() == R.id.navigation_donacion) {
                    startActivity(new Intent(getApplicationContext(), RealizarDonacion.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (menuItem.getItemId() == R.id.navigation_notificacion) {
                    startActivity(new Intent(getApplicationContext(), Notificaciones.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });






        // Configura un OnClickListener para mostrar el teclado cuando se hace clic en el campo de búsqueda
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKeyboard();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        // Mueve la cámara a la ubicación por defecto
        LatLng defaultLocation = new LatLng(-12.070525, -77.079739);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15));

        // Agrega un marcador en las coordenadas indicadas
        LatLng customLocation = new LatLng(-12.072257, -77.079859);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(customLocation);
        markerOptions.title("Partido de Basket masculino");

        // Personaliza el marcador si es necesario (icono, color, etc.)
        // markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        map.addMarker(markerOptions);
    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);
    }
}
