package com.example.telecommunity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class MisEventosDeActividadFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Publicaciondto> publicacionList;

    private RecyclerView recyclerView;
    private PublicacionAdapter publicacionAdapter;
    private List<Publicaciondto> publicaciones;
    private FirebaseFirestore db;
    private static final String TAG = "MisEventosDeActividadFragment";
    private String idActividad;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        publicaciones = new ArrayList<>();
        publicacionAdapter = new PublicacionAdapter(publicaciones, getContext());
        recyclerView.setAdapter(publicacionAdapter);

        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            idActividad = getArguments().getString("idActividad");
        }

        cargarDatos();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cargarDatos();
            }
        });

        // Obtiene el tipo de usuario de las preferencias compartidas
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        String tipoUsuario = sharedPreferences.getString("tipoUsuario", "usuario");


        // Muestra u oculta el botón de crear evento según el tipo de usuario
        FloatingActionButton btnCrearEvento = view.findViewById(R.id.btnCrearPublicacion);
        if ("Delegado de actividad".equals(tipoUsuario)) {
            btnCrearEvento.setVisibility(View.VISIBLE);
        } else {
            btnCrearEvento.setVisibility(View.GONE);
        }

        return view;
    }

    private void cargarDatos() {
        // Obtiene el nombre de la actividad del Bundle
        String nombreActividad = getArguments().getString("nombreActividad", "");

        db.collection("publicaciones")
                .whereEqualTo("idActividad", nombreActividad) // Asegúrate de que "idActividad" se escribe exactamente igual que en Firestore
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            publicaciones.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Publicaciondto publicacion = document.toObject(Publicaciondto.class);
                                publicaciones.add(publicacion);
                            }
                            publicacionAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                        // Detener la animación de recarga
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    private void cargarDetallePublicacion(String idPublicacion) {
        db.collection("publicaciones")
                .document(idPublicacion)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Publicaciondto publicacion = documentSnapshot.toObject(Publicaciondto.class);
                        if (publicacion != null) {
                            publicaciones.add(publicacion);
                            publicacionAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error getting publication details.", e);
                    }
                });
    }

    private void cargarPublicaciones() {
        db.collection("publicaciones")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            publicaciones.clear();
                            for (DocumentSnapshot document : task.getResult()) {
                                Publicaciondto publicacion = document.toObject(Publicaciondto.class);
                                publicaciones.add(publicacion);
                            }
                            publicacionAdapter.notifyDataSetChanged();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }


                });
    }
}