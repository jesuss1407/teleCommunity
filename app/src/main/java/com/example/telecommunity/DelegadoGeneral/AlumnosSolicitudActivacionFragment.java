package com.example.telecommunity.DelegadoGeneral;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.telecommunity.R;
import com.example.telecommunity.adapter.GeneralActividadesadapter;
import com.example.telecommunity.adapter.GeneralUsuariosAdapter;
import com.example.telecommunity.entity.ActividadDto;
import com.example.telecommunity.entity.GeneralUsuariosdto;
import com.example.telecommunity.entity.UsuariosDto;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlumnosSolicitudActivacionFragment extends Fragment {

    private RecyclerView recyclerView;
    private GeneralUsuariosAdapter adapter;
    private FirebaseFirestore db;
    private StorageReference storageRef;
    final Context context = getContext();
    public AlumnosSolicitudActivacionFragment() {
        // Constructor público vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout para este fragmento
        View view = inflater.inflate(R.layout.fragment_alumnos_solicitud_activacion, container, false);

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();



        // Obtén el usuario logueado
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            //String userEmail = user.getEmail();
            // Consulta la colección
            db.collection("usuarios")
                    .whereEqualTo("estado", "pendiente")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<UsuariosDto> usuariosList = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    UsuariosDto activity = document.toObject(UsuariosDto.class);
                                    usuariosList.add(activity);
                                }

                                // Pasar la lista filtrada al adaptador
                                recyclerView = view.findViewById(R.id.listarActividades);
                                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                                adapter = new GeneralUsuariosAdapter(getContext(), usuariosList);
                                recyclerView.setAdapter(adapter);

                            } else {
                                //Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });
        }


        if (getActivity() instanceof BaseGeneralActivity) {
            ((BaseGeneralActivity) getActivity()).setTitleTextView("Solicitudes de activación");
        }


        return view;
    }
}