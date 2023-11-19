package com.example.telecommunity.DelegadoGeneral;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.telecommunity.R;

import com.example.telecommunity.adapter.GeneralActividadesadapter;
import com.example.telecommunity.entity.ActividadDto;
import com.example.telecommunity.entity.GeneralActividadesdto;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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


public class ActividadesCurso extends AppCompatActivity {

    private static final int SELECT_IMAGE_REQUEST_CODE = 1001;
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private RecyclerView recyclerView;
    private GeneralActividadesadapter adapter;
    private ArrayList<ActividadDto> actividadList;
    private ArrayList<ActividadDto> activityList;
    final Context context = this;

    Uri selectedImageUri;

    private static final String TAG = "YourActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_actividades_curso);
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();


        // Obtén el usuario logueado
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userEmail = user.getEmail();
            // Consulta la colección
            db.collection("actividades")
                    .whereEqualTo("estado", "En curso")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                ArrayList<ActividadDto> actividadList = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    ActividadDto activity = document.toObject(ActividadDto.class);
                                    actividadList.add(activity);
                                }

                                // Ahora activityList contiene todos tus documentos como objetos ActivityDTO
                                // Puedes procesarlos como desees

                                // Pasar la lista filtrada al adaptador
                                recyclerView = findViewById(R.id.listarActividades);
                                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                                adapter = new GeneralActividadesadapter(ActividadesCurso.this, actividadList);
                                recyclerView.setAdapter(adapter);


                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });
        }






        //Navbar

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.navigation_actividades);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.navigation_actividades) {
                    // Redirigir a AdmActividades
                    Intent intent = new Intent(ActividadesCurso.this, AdmActividades.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // Para asegurar que AdmActividades sea la única actividad en la pila
                    startActivity(intent);
                    return true; // devuelve true para indicar que el evento ha sido manejado
                }
                if (menuItem.getItemId() == R.id.navigation_estadistica) {
                    // Ir a la actividad Estadisticas
                    startActivity(new Intent(getApplicationContext(), Estadisticas.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                else if (menuItem.getItemId() == R.id.navigation_admdonacion) {
                    // Ir a la actividad AdministrarDonaión
                    startActivity(new Intent(getApplicationContext(), AdmDonacion.class));
                    overridePendingTransition(0, 0);
                    return true;
                }


                return false; // devuelve false para indicar que el evento no ha sido manejado
            }
        });



    }


    @Override
    public void onBackPressed() {
        // Redirigir a AdmActividades
        Intent intent = new Intent(this, AdmActividades.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Para asegurar que AdmActividades sea la única actividad en la pila
        startActivity(intent);
    }






}
