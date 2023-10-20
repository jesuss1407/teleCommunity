package com.example.telecommunity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.example.telecommunity.entity.Evento;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class SearchFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap map;
    private com.google.android.material.search.SearchBar searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Inicializa y configura el campo de búsqueda
        searchView = view.findViewById(R.id.searchView); // Cambiado a search_bar
        searchView.setHint("Buscar");

        // Inicializar el SupportMapFragment
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();

        // Reemplazar el FragmentContainerView con el mapa
        getChildFragmentManager().beginTransaction().replace(R.id.maps, mapFragment).commit();

        // Obtener una referencia al mapa
        mapFragment.getMapAsync(this);

        // Configura un OnClickListener para mostrar el teclado cuando se hace clic en el campo de búsqueda
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKeyboard();
            }
        });

        return view;
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
                    Intent intent = new Intent(getContext(), DetalleUbicacionActivity.class);
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
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);
    }


}