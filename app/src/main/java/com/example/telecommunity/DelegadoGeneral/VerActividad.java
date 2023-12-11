package com.example.telecommunity.DelegadoGeneral;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.telecommunity.R;
import com.example.telecommunity.entity.ActividadDto;
import com.example.telecommunity.entity.Publicaciondto;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class VerActividad extends AppCompatActivity {

    TextView detailApoyos, detailTitle, detailEventos,detailDescripcion,detailDelegado, numEventos;
    ImageView detailImagen;
    private String idActividad, nombreActividad;
    private String state;
    private int contadorEventos;
    private FirebaseFirestore db;
    private Context context;
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
        numEventos = findViewById(R.id.numDeEventos);
        //detailApoyos = findViewById(R.id.numapoyos);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            String imageUrl = bundle.getString("Image");
            Picasso.get().load(imageUrl).into(detailImagen);
            //detailEventos.setText(bundle.getString("Numeventos"));
            //detailApoyos.setText(bundle.getString("Numapoyos"));
            nombreActividad = bundle.getString("Title");
            detailTitle.setText(bundle.getString("Title"));
            detailDescripcion.setText(bundle.getString("Descripcion"));
            detailDelegado.setText(bundle.getString("Delegado")+ " - "+bundle.getString("DelegadoCode") );
            idActividad=bundle.getString("Id");

        }


        //editar actividad
        Button editarButton = findViewById(R.id.editarButton);
        editarButton.setOnClickListener(v -> {

            Intent intent = new Intent(VerActividad.this, EditarActividad.class);
            intent.putExtra("IdAct", idActividad);
            startActivity(intent);

        });


        Button cambiarEstadoButton = findViewById(R.id.cerrarButton);
        //se valida el estado actual de la actividad
        FirebaseFirestore dbAct = FirebaseFirestore.getInstance();
        dbAct.collection("actividades").document(idActividad).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Obtiene el valor del campo "tipo"
                        state = documentSnapshot.getString("estado");

                        if ("En curso".equals(state)) {
                            cambiarEstadoButton.setText("Cerrar actividad");

                        } else if ("Finalizado".equals(state)) {
                            // Crea un diálogo de alerta para confirmar la acción.
                            cambiarEstadoButton.setText("Reabrir actividad");

                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Maneja los errores si la lectura del documento falla
                });



        FirebaseFirestore db2 = FirebaseFirestore.getInstance();

        //ver eventos de actividad
        db2.collection("publicaciones")
                .whereEqualTo("idActividad", nombreActividad)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    contadorEventos = 0; // Inicializa el contador

                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        if (documentSnapshot.exists()) {
                            contadorEventos++; // Incrementa el contador por cada documento que existe
                        }
                    }

                    // Aquí puedes usar el contador. Por ejemplo, mostrarlo en un TextView o tomar alguna decisión.
                    // Ejemplo: textViewContador.setText("Número de documentos: " + contadorDocumentos);

                    numEventos.setText("Se tiene " + contadorEventos+" eventos activos");

                })
                .addOnFailureListener(e -> {
                    // Manejo de errores
                });


        //cerrar actividad
        cambiarEstadoButton.setOnClickListener(v -> {
            //se valida el estado actual de la actividad
            if ("En curso".equals(state)) {
                // Crea un diálogo de alerta para confirmar la acción.
                new AlertDialog.Builder(VerActividad.this)
                        .setTitle("Confirmar acción")
                        .setMessage("¿Estás seguro de que deseas cambiar el estado de esta actividad a 'Finalizado'?")
                        .setPositiveButton("Sí", (dialog, which) -> {
                            // Usuario ha confirmado la acción.
                            if (contadorEventos==0) {
                                cerrarActividad(); // Función para cambiar el estado.

                            } else {

                                Toast.makeText(VerActividad.this, "No se pudo cerrar Actividad, hay eventos activos", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .setNegativeButton("Cancelar", (dialog, which) -> {
                            // Usuario ha cancelado la acción.
                            dialog.dismiss(); // Cierra el diálogo.
                        })
                        .show();
            } else if ("Finalizado".equals(state)) {
                // Crea un diálogo de alerta para confirmar la acción.
                new AlertDialog.Builder(VerActividad.this)
                        .setTitle("Confirmar acción")
                        .setMessage("¿Estás seguro de que deseas cambiar el estado de esta actividad a 'En curso'?")
                        .setPositiveButton("Sí", (dialog, which) -> {
                            // Usuario ha confirmado la acción.
                            reabrirActividad(); // Función para cambiar el estado.
                        })
                        .setNegativeButton("Cancelar", (dialog, which) -> {
                            // Usuario ha cancelado la acción.
                            dialog.dismiss(); // Cierra el diálogo.
                        })
                        .show();
            }
        });

















        //Navbar



    }


    private void cerrarActividad() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference actividadRef = db.collection("actividades").document(idActividad);

//                              Actualiza el campo "estado" a "Finalizado".
        actividadRef.update("estado", "Finalizado")
                .addOnSuccessListener(aVoid -> {
                    // El estado se actualizó con éxito en Firestore.
                    Toast.makeText(VerActividad.this, "Actividad finalizada con éxito", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(VerActividad.this, BaseGeneralActivity.class);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // Para asegurar que AdmActividades sea la única actividad en la pila
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Ocurrió un error al actualizar el estado en Firestore.
                    // Puedes mostrar un mensaje de error o realizar acciones de manejo de errores.
                });



    }

    private void reabrirActividad() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference actividadRef = db.collection("actividades").document(idActividad);

//                              Actualiza el campo "estado" a "Finalizado".
        actividadRef.update("estado", "En curso")
                .addOnSuccessListener(aVoid -> {
                    // El estado se actualizó con éxito en Firestore.
                    Toast.makeText(VerActividad.this, "Actividad reabierta con éxito", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(VerActividad.this, BaseGeneralActivity.class);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // Para asegurar que AdmActividades sea la única actividad en la pila
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Ocurrió un error al actualizar el estado en Firestore.
                    // Puedes mostrar un mensaje de error o realizar acciones de manejo de errores.
                });



    }
   }