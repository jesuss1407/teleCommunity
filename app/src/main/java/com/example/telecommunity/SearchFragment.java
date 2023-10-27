package com.example.telecommunity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.example.telecommunity.entity.Evento;
import com.example.telecommunity.entity.Publicaciondto;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.QuerySnapshot;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.appcompat.widget.SearchView;


import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class SearchFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap map;
    private SearchView searchView;
    private FirebaseFirestore db;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Inicializa y configura el campo de búsqueda
        searchView = view.findViewById(R.id.searchView);
        searchView.setQueryHint("Buscar");

        // Inicializar el SupportMapFragment
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();

        // Reemplazar el FragmentContainerView con el mapa
        getChildFragmentManager().beginTransaction().replace(R.id.maps, mapFragment).commit();

        // Obtener una referencia al mapa
        mapFragment.getMapAsync(this);

        // Configura un OnClickListener para mostrar el teclado cuando se hace clic en el campo de búsqueda
        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showKeyboard();
            }
        });

        // Inicializa Firestore
        db = FirebaseFirestore.getInstance();

        // Inicializa el FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        // Configura el listener para el texto de búsqueda
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                buscarPublicaciones(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Aquí puedes implementar la búsqueda en tiempo real si lo deseas
                return false;
            }
        });

        return view;
    }

    private void buscarPublicaciones(String query) {
        db.collection("publicaciones")
                .orderBy("nombre")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        map.clear();  // Limpiar el mapa antes de añadir los nuevos marcadores
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Publicaciondto publicacion = document.toObject(Publicaciondto.class);
                            agregarMarcador(map, publicacion);
                        }
                    } else {
                        Log.w("SearchFragment", "Error getting documents.", task.getException());
                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        // Configura el tipo de mapa
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Configura la ubicación por defecto y el nivel de zoom
        LatLng defaultLocation = new LatLng(-12.072257, -77.079859);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15));
        // Verifica los permisos de ubicación
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }
        // Obtén la ubicación actual
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        map.addMarker(new MarkerOptions()
                                .position(currentLocation)
                                .title("Mi Ubicación")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                    }
                })
                .addOnFailureListener(e -> Log.d("SearchFragment", "Error al obtener la ubicación actual", e));
        // Cargar datos de Firestore
        cargarPublicaciones();
        // Configurar listener para clic en marcador
        map.setOnMarkerClickListener(marker -> {
            Publicaciondto publicacion = (Publicaciondto) marker.getTag();
            if (publicacion != null) {
                Intent intent = new Intent(getContext(), DetallePublicacionActivity.class);
                intent.putExtra("publicacionId", publicacion.getId());
                startActivity(intent);
            }
            return true;
        });
    }

    private void cargarPublicaciones() {
        db.collection("publicaciones")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Publicaciondto publicacion = document.toObject(Publicaciondto.class);
                            agregarMarcador(map, publicacion);
                        }
                    } else {
                        Log.d("SearchFragment", "Error obteniendo publicaciones: ", task.getException());
                    }
                });
    }

    private Marker agregarMarcador(GoogleMap map, Publicaciondto publicacion) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(publicacion.getLatitud(), publicacion.getLongitud()));
        markerOptions.title(publicacion.getNombre());
        markerOptions.snippet("Descripción: " + publicacion.getContenido());
        Marker marker = map.addMarker(markerOptions);
        marker.setTag(publicacion);
        return marker;
    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);
    }
}
