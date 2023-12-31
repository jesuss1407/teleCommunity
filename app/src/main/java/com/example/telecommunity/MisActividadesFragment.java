package com.example.telecommunity;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.telecommunity.adapter.MisActividadesAdapter;
import com.example.telecommunity.entity.ActividadDto;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class MisActividadesFragment extends Fragment implements MisActividadesAdapter.ItemClickListener{


    private RecyclerView recyclerView;
    private MisActividadesAdapter actividadAdapter;
    private List<ActividadDto> actividadList;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mis_actividades, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        actividadList = new ArrayList<>();
        actividadAdapter = new MisActividadesAdapter(getContext(), actividadList, this); // Inicializa correctamente la variable global
        recyclerView.setAdapter(actividadAdapter); // Asigna una vez el adaptador

        db = FirebaseFirestore.getInstance();
        cargarIdDelUsuario();

        return view;
    }

    @Override
    public void onItemClick(ActividadDto actividad) {
        MisEventosDeActividadFragment fragment = new MisEventosDeActividadFragment();
        Bundle bundle = new Bundle();
        bundle.putString("nombreActividad", actividad.getNombre()); // Usa getNombre() en lugar de getId()
        fragment.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }


    private void cargarDatosDesdeFirebase() {
        db.collection("actividades").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    actividadList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        ActividadDto actividad = document.toObject(ActividadDto.class);
                        actividadList.add(actividad);
                    }
                    actividadAdapter.notifyDataSetChanged(); // Ahora puedes llamar a este método sin problemas
                } else {
                    Log.d(TAG, "Error obteniendo documentos: ", task.getException());
                }
            }
        });
    }


    private void cargarDetallesDeActividades(List<String> actividadIds) {
        for (String actividadId : actividadIds) {
            db.collection("actividades").document(actividadId).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            ActividadDto actividad = documentSnapshot.toObject(ActividadDto.class);
                            if (actividad != null) {
                                actividadList.add(actividad);
                                actividadAdapter.notifyDataSetChanged(); // Notificamos al adaptador que los datos han cambiado
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("DetalleActividad", "Error obteniendo detalles de la actividad: ", e);
                        }
                    });
        }
    }


    private void cargarActividadesDelUsuario(String userId) {
        int myCode = Integer.parseInt(userId);
        db.collection("actividades")
                .whereEqualTo("estado", "En curso")
                .whereEqualTo("delegadoCode", myCode)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> actividadIds = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Aquí suponemos que el documento dentro de la subcolección 'actividades' tiene un campo 'idactividad'
                                String actividadId = document.getString("id");
                                if (actividadId != null) {
                                    actividadIds.add(actividadId);
                                }
                            }
                            // Ahora que tenemos los IDs, obtenemos los detalles de cada actividad
                            cargarDetallesDeActividades(actividadIds);

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });


    }



    private void cargarIdDelUsuario() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Si el usuario está logueado, obtenemos su email
            String userEmail = currentUser.getEmail();
            // Ahora buscamos el usuario en Firestore por su correo
            db.collection("usuarios")
                    .whereEqualTo("correo", userEmail)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot querySnapshot) {
                            if (!querySnapshot.isEmpty()) {
                                // Suponemos que el correo es único y obtenemos el primer documento
                                DocumentSnapshot userDocument = querySnapshot.getDocuments().get(0);
                                String userId = userDocument.getId(); // Aquí tenemos el ID del usuario
                                // Con el ID del usuario, ahora podemos cargar sus actividades
                                cargarActividadesDelUsuario(userId);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("MisActividadesFragment", "Error al buscar el usuario", e);
                        }
                    });
        } else {
            // Manejar el caso en que no haya un usuario logueado
        }
    }


}