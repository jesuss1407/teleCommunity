package com.example.telecommunity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.telecommunity.entity.Evento;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.gms.maps.CameraUpdateFactory;

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

        // Configura el tipo de mapa (puedes ajustar esto según tus preferencias)
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Configura la ubicación por defecto y el nivel de zoom
        LatLng defaultLocation = new LatLng(-12.072257, -77.079859);
        float zoomLevel = 17;
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, zoomLevel));

        // Crea dos eventos de ejemplo
        Evento evento1 = new Evento("Evento 1", "Descripción del Evento 1", "10:00 AM", "Juan Pérez");
        Evento evento2 = new Evento("Evento 2", "Descripción del Evento 2", "2:00 PM", "María García");

        // Agrega marcadores para los eventos
        Marker marker1 = agregarMarcador(map, evento1, -12.072230, -77.079859);
        Marker marker2 = agregarMarcador(map, evento2, -12.070525, -77.079739);

        // Configura un oyente para el clic en el marcador
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                // Recupera la información del evento asociada al marcador
                Evento evento = (Evento) marker.getTag();

                if (evento != null) {
                    // Abre una nueva actividad para mostrar información detallada
                    Intent intent = new Intent(getApplicationContext(), DetalleUbicacionActivity.class);
                    intent.putExtra("evento", evento);
                    startActivity(intent);
                }
                return true;
            }
        });
    }
    // Esta función agrega un marcador para un evento y devuelve el marcador creado
    private Marker agregarMarcador(GoogleMap map, Evento evento, double latitud, double longitud) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(latitud, longitud));
        markerOptions.title(evento.getTitulo());
        markerOptions.snippet("Descripción: " + evento.getDescripcion() + "\nHora: " + evento.getHora() + "\nCreador: " + evento.getCreador());

        // Asocia el evento con el marcador usando setTag
        Marker marker = map.addMarker(markerOptions);
        marker.setTag(evento);

        return marker;
    }



    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);
    }
}
