package com.example.telecommunity.DelegadoGeneral;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.telecommunity.R;
import com.example.telecommunity.entity.ActividadDto;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class VerActividad extends AppCompatActivity {

    TextView detailApoyos, detailTitle, detailEventos,detailDescripcion,detailDelegado;
    ImageView detailImagen;
    private String idActividad;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_actividad);


        //mandar info del usuario seleccionado
        //detailEventos= findViewById(R.id.numeventos);
        detailTitle = findViewById(R.id.nombre);
        detailDescripcion = findViewById(R.id.recDesc);
        detailDelegado = findViewById(R.id.dele);
        detailImagen = findViewById(R.id.recImage);
        //detailApoyos = findViewById(R.id.numapoyos);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            String imageUrl = bundle.getString("Image");
            Picasso.get().load(imageUrl).into(detailImagen);
            //detailEventos.setText(bundle.getString("Numeventos"));
            //detailApoyos.setText(bundle.getString("Numapoyos"));
            detailTitle.setText(bundle.getString("Title"));
            detailDescripcion.setText(bundle.getString("Descripcion"));
            detailDelegado.setText(bundle.getString("Delegado")+ " - "+bundle.getString("DelegadoCode") );
            idActividad=bundle.getString("Id");

        }

        //cerrar actividad
        Button cambiarEstadoButton = findViewById(R.id.cerrarButton);
        cambiarEstadoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crea un diálogo de alerta para confirmar la acción.
                new AlertDialog.Builder(VerActividad.this)
                        .setTitle("Confirmar acción")
                        .setMessage("¿Estás seguro de que deseas cambiar el estado de esta actividad a 'Finalizado'?")
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Usuario ha confirmado la acción.
                                cerrarActividad(idActividad); // Función para cambiar el estado.
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Usuario ha cancelado la acción.
                                dialog.dismiss(); // Cierra el diálogo.
                            }
                        })
                        .show();
            }
        });

















        //Navbar

        // Encontrar la BottomNavigationView y setear el item seleccionado
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.navigation_actividades);

        // Configurar el Listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

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
                else if (menuItem.getItemId() == R.id.navigation_alumnos) {
                    // Ir a la actividad AdministrarDonaión
                    startActivity(new Intent(getApplicationContext(), AdmUsuarios.class));
                    overridePendingTransition(0, 0);
                    return true;
                }

                return false;
            }
        });

    }


    private void cerrarActividad(String idAct) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference actividadRef = db.collection("actividades").document(idActividad);

//                              Actualiza el campo "estado" a "Finalizado".
        actividadRef.update("estado", "Finalizado")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // El estado se actualizó con éxito en Firestore.
                        Intent intent = new Intent(VerActividad.this, ActividadesCurso.class);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // Para asegurar que AdmActividades sea la única actividad en la pila
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Ocurrió un error al actualizar el estado en Firestore.
                        // Puedes mostrar un mensaje de error o realizar acciones de manejo de errores.
                    }
                });



    }
   }