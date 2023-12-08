package com.example.telecommunity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.telecommunity.adapter.PublicacionAdapter;
import com.example.telecommunity.entity.Publicaciondto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class MisEventosDeActividadFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private PublicacionAdapter publicacionAdapter;
    private List<Publicaciondto> publicaciones;
    private FirebaseFirestore db;
    private static final String TAG = "MisEventosFragment";
    private String idActividad;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mis_eventos, container, false);

        // Inicializar Firebase y RecyclerView
        db = FirebaseFirestore.getInstance();
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        publicaciones = new ArrayList<>();
        publicacionAdapter = new PublicacionAdapter(publicaciones, getContext());
        recyclerView.setAdapter(publicacionAdapter);

        // Obtener el ID de la actividad desde los argumentos
        if (getArguments() != null) {
            idActividad = getArguments().getString("idActividad");
            if (idActividad != null && !idActividad.isEmpty()) {
                cargarEventosDeActividad(idActividad);
            }
        }

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (idActividad != null && !idActividad.isEmpty()) {
                cargarEventosDeActividad(idActividad);
            }
        });

        return view;
    }


    private void cargarPublicacionesDeActividad(String idActividad) {
        if (idActividad != null && !idActividad.isEmpty()) {
            db.collection("actividades").document(idActividad).collection("publicaciones")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            publicaciones.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Suponiendo que cada documento de la subcolección tiene los datos necesarios para crear un objeto Publicaciondto
                                Publicaciondto publicacion = document.toObject(Publicaciondto.class);
                                publicaciones.add(publicacion);
                            }
                            publicacionAdapter.notifyDataSetChanged();
                        } else {
                            Log.e(TAG, "Error al obtener publicaciones de la actividad", task.getException());
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    });
        } else {
            publicaciones.clear();
            publicacionAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        }
    }
    private void cargarEventosDeActividad(String idActividad) {
        db.collection("actividades").document(idActividad).collection("publicaciones")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> idsDePublicaciones = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String idPublicacion = document.getString("idpublicacion");
                            if (idPublicacion != null) {
                                idsDePublicaciones.add(idPublicacion);
                            }
                        }
                        if (!idsDePublicaciones.isEmpty()) {
                            cargarDetallePublicaciones(idsDePublicaciones);
                        } else {
                            publicaciones.clear();
                            publicacionAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.e(TAG, "Error al obtener publicaciones de la actividad", task.getException());
                    }
                    swipeRefreshLayout.setRefreshing(false);
                });
    }


    private void cargarDetallePublicaciones(List<String> idsDePublicaciones) {
        // Limpiar la lista antes de cargar nuevos datos
        publicaciones.clear();

        for (String idPublicacion : idsDePublicaciones) {
            db.collection("publicaciones").document(idPublicacion)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Publicaciondto publicacion = documentSnapshot.toObject(Publicaciondto.class);
                        if (publicacion != null) {
                            publicaciones.add(publicacion);
                            publicacionAdapter.notifyDataSetChanged();
                        }
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Error al cargar la publicación con ID: " + idPublicacion, e));
        }
    }

    private void verificarUnionEvento() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getEmail() != null) {
            String userEmail = user.getEmail();
            db.collection("usuarios")
                    .whereEqualTo("correo", userEmail)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot userDocument = queryDocumentSnapshots.getDocuments().get(0);
                            Long codigo = userDocument.getLong("codigo");
                            if (codigo != null) {
                                cargarEventosUnidos(codigo.toString());
                            } else {
                                Log.e(TAG, "No se pudo obtener el código del usuario");
                            }
                        } else {
                            Log.e(TAG, "No se encontró el documento del usuario");
                        }
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Error obteniendo el documento del usuario", e));
        } else {
            Log.e(TAG, "Usuario no autenticado");
        }
    }

    private void cargarEventosUnidos(String userCodigo) {
        db.collection("usuarios").document(userCodigo).collection("eventos")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> idsEventosUnidos = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            idsEventosUnidos.add(document.getId());
                        }
                        cargarPublicaciones(idsEventosUnidos);
                    } else {
                        Log.e(TAG, "Error al obtener eventos unidos", task.getException());
                    }
                });
    }

    private void cargarPublicaciones(List<String> idsEventosUnidos) {
        if (!idsEventosUnidos.isEmpty()) {
            db.collection("publicaciones")
                    .whereIn("id", idsEventosUnidos)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            publicaciones.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Publicaciondto publicacion = document.toObject(Publicaciondto.class);
                                publicaciones.add(publicacion);
                            }
                            publicacionAdapter.notifyDataSetChanged();
                        } else {
                            Log.e(TAG, "Error al obtener publicaciones", task.getException());
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    });
        } else {
            publicaciones.clear();
            publicacionAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}