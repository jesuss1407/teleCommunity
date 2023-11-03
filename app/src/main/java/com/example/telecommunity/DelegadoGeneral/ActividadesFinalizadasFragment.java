package com.example.telecommunity.DelegadoGeneral;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.telecommunity.R;
import com.example.telecommunity.adapter.GeneralActividadesadapter;
import com.example.telecommunity.entity.ActividadDto;
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
import java.util.List;

public class ActividadesFinalizadasFragment extends Fragment {

    private static final int SELECT_IMAGE_REQUEST_CODE = 1001;
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private RecyclerView recyclerView;
    private GeneralActividadesadapter adapter;
    private List<ActividadDto> actividadList;
    private List<ActividadDto> activityList;
    final Context context = getContext();

    Uri selectedImageUri;

    private static final String TAG = "YourActivity";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_actividades_finalizadas, container, false);

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        // Obtén el usuario logueado
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userEmail = user.getEmail();
            // Consulta la colección
            db.collection("actividades")
                    .whereEqualTo("estado", "Finalizado")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<ActividadDto> actividadList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ActividadDto activity = document.toObject(ActividadDto.class);
                                actividadList.add(activity);
                            }

                            // Pasar la lista filtrada al adaptador
                            recyclerView = view.findViewById(R.id.listarActividades);
                            recyclerView.setLayoutManager(new LinearLayoutManager(context));
                            adapter = new GeneralActividadesadapter(getActivity(), actividadList);
                            recyclerView.setAdapter(adapter);

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    });
        }

        if (getActivity() instanceof BaseGeneralActivity) {
            ((BaseGeneralActivity) getActivity()).setTitleTextView("Actividades finalizadas");
        }


        return view;
    }
}
