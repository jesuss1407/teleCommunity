package com.example.telecommunity;

import android.content.Intent;
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
import android.widget.Button;

import com.example.telecommunity.adapter.PublicacionAdapter;
import com.example.telecommunity.entity.Publicaciondto;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;


public class HomeFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Publicaciondto> publicacionList;

    private RecyclerView recyclerView;
    private PublicacionAdapter publicacionAdapter;
    private List<Publicaciondto> publicaciones;
    private FirebaseFirestore db;
    private static final String TAG = "HomeFragment";

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
        // Aquí añade el código para cargar los datos que quieres mostrar en tu RecyclerView
        db.collection("publicaciones")
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