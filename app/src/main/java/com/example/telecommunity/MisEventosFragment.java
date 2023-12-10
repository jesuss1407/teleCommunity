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

import com.example.telecommunity.adapter.EventoChatAdapter;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;


public class MisEventosFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private EventoChatAdapter publicacionAdapter;
    private List<Publicaciondto> publicaciones;
    private FirebaseFirestore db;
    private static final String TAG = "MisEventosFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mis_eventos, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        publicaciones = new ArrayList<>();
        publicacionAdapter = new EventoChatAdapter(publicaciones, getContext());
        recyclerView.setAdapter(publicacionAdapter);

        db = FirebaseFirestore.getInstance();
        verificarUnionEvento();

        swipeRefreshLayout.setOnRefreshListener(() -> verificarUnionEvento());

        return view;
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
                    .orderBy("horaCreacion", Query.Direction.DESCENDING)
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