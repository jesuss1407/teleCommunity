package com.example.telecommunity.DelegadoGeneral;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

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

public class AlumnosBaneadosFragment extends Fragment implements SearchView.OnQueryTextListener{

    private RecyclerView recyclerView;
    private GeneralUsuariosAdapter adapter;
    private FirebaseFirestore db;
    private StorageReference storageRef;
    final Context context = getContext();
    private SearchView searchView;
    public AlumnosBaneadosFragment() {
        // Constructor público vacío requerido
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflar el layout para este fragmento
        View view = inflater.inflate(R.layout.fragment_alumnos_activos, container, false);

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        searchView = view.findViewById(R.id.buscarActividades);
        searchView.setOnQueryTextListener(this);

        // Obtén el usuario logueado
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            //String userEmail = user.getEmail();
            // Consulta la colección
            db.collection("usuarios")
                    .whereEqualTo("estado", "baneado")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                ArrayList<UsuariosDto> usuariosList = new ArrayList<>();
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
            ((BaseGeneralActivity) getActivity()).setTitleTextView("Alumnos baneados");
        }


        return view;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        adapter.filtrado(s);
        return false;
    }
}