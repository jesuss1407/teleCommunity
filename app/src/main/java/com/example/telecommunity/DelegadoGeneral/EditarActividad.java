package com.example.telecommunity.DelegadoGeneral;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.telecommunity.IniciarSesion;
import com.example.telecommunity.R;
import com.example.telecommunity.entity.ActividadDto;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditarActividad extends AppCompatActivity {


    private static final int SELECT_IMAGE_REQUEST_CODE = 1001;
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private String delegadoNombre;
    private String estado;

    EditText etActividad, etContenido, etCodigoDelegado, etEstado;

    Button btnGuardarPublicacion;
    ImageView ivSelectedImage;
    Button btnSelectImage;
    private String idActividad;

    Uri selectedImageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_actividad);
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        etActividad = findViewById(R.id.etActividad);
        etCodigoDelegado= findViewById(R.id.delegadoCodigo);
        etContenido = findViewById(R.id.etContenido);
        btnGuardarPublicacion = findViewById(R.id.btnGuardarPublicacion);
        ivSelectedImage = findViewById(R.id.ivSelectedImage);
        btnSelectImage = findViewById(R.id.btnSelectImage);


        //Cancelar
        Button goBackButton = findViewById(R.id.btnCancelar);
        goBackButton.setOnClickListener(view -> {
            finish();
        });

        // Configurar botón para seleccionar imagen
        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, SELECT_IMAGE_REQUEST_CODE);
        });


        //mostrar la info

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            idActividad=bundle.getString("IdAct");

        }

        FirebaseFirestore db2 = FirebaseFirestore.getInstance();
        db2.collection("actividades")
                .whereEqualTo("id", idActividad)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            etActividad.setText(document.getString("nombre"));
                            etContenido.setText(document.getString("descripcion"));

                            // Obteniendo el código como un Long
                            Long codigo = document.getLong("delegadoCode");
                            // Si el código no es nulo, convertirlo a String para mostrarlo en el EditText
                            if (codigo != null) {
                                etCodigoDelegado.setText(String.valueOf(codigo));
                            }
                            estado = document.getString("estado");
                            String linkFoto = document.getString("fotoLink");

                            Picasso.get().load(linkFoto).into(ivSelectedImage);

                        }
                    } else {
                        Log.d(TAG, "No coincide");
                    }
                });


        // Configurar botón para editar actividad
        btnGuardarPublicacion.setOnClickListener(v -> {

            // Obtén los datos del formulario
            String nombre = etActividad.getText().toString().trim();
            String contenido = etContenido.getText().toString().trim();
            String codigoDelegadoStr = etCodigoDelegado.getText().toString().trim();
            FirebaseFirestore db1 = FirebaseFirestore.getInstance();
            DocumentReference docRef1 = db1.collection("usuarios").document(codigoDelegadoStr);


            if (nombre.isEmpty() || contenido.isEmpty() ||codigoDelegadoStr.isEmpty() ){
                Toast.makeText(EditarActividad.this, "Llene todos los campos ", Toast.LENGTH_SHORT).show();
            } else if(!(!TextUtils.isEmpty(codigoDelegadoStr) && TextUtils.isDigitsOnly(codigoDelegadoStr))){
                Toast.makeText(EditarActividad.this, "Ingrese solo numeros en el Código", Toast.LENGTH_SHORT).show();
            } else if(docRef1.getId().isEmpty()){
                Toast.makeText(EditarActividad.this, "Ingrese un codigo de usuario existente", Toast.LENGTH_SHORT).show();
            }else {

                int codigoDelegado = Integer.parseInt(codigoDelegadoStr);



                // Crea una referencia a la colección "usuarios" y al documento específico con el ID en mycode
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("usuarios").document(codigoDelegadoStr);
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (!documentSnapshot.exists()) {
                            Toast.makeText(EditarActividad.this, "Ingrese un codigo de usuario existente", Toast.LENGTH_SHORT).show();
                        }else{
                            // El documento existe, ahora puedes obtener el campo "nombre"
                            String nombredele = documentSnapshot.getString("nombre");
                            String apellido = documentSnapshot.getString("apellido");
                            delegadoNombre = nombredele + " "+apellido;
                            String fotoactual = documentSnapshot.getString("fotoLink");


                            // Busca la actividad en la colección de actividades
                            db.collection("actividades")
                                    .whereEqualTo("id", idActividad)
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            QuerySnapshot querySnapshot = task.getResult();
                                            if (!querySnapshot.isEmpty()) {
                                                // Verifica si se seleccionó una imagen
                                                if (selectedImageUri != null) {
                                                    // Sube la imagen a Firebase Storage y obtén el URL
                                                    StorageReference fileRef = storageRef.child("images/" + System.currentTimeMillis() + ".jpg");
                                                    fileRef.putFile(selectedImageUri)
                                                            .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl()
                                                                    .addOnSuccessListener(uri -> {

                                                                        //estas seguro?
                                                                        // Crea un diálogo de alerta para confirmar la acción.
                                                                        new AlertDialog.Builder(EditarActividad.this)
                                                                                .setTitle("Confirmar acción")
                                                                                .setMessage("¿Estás seguro de que deseas asignar a '"+delegadoNombre+"' como delegado de esta actividad")
                                                                                .setPositiveButton("Sí", (dialog, which) -> {
                                                                                    // Usuario ha confirmado la acción.
                                                                                    // Crea y guarda la publicación con la URL de la imagen
                                                                                    editarActividad(idActividad, codigoDelegado, delegadoNombre, nombre, contenido, uri.toString(),estado);
                                                                                })
                                                                                .setNegativeButton("Cancelar", (dialog, which) -> {
                                                                                    // Usuario ha cancelado la acción.
                                                                                    dialog.dismiss(); // Cierra el diálogo.
                                                                                })
                                                                                .show();
                                                                    }))
                                                            .addOnFailureListener(e -> {
                                                                // Ocurrió un error al subir la imagen
                                                                // Puedes mostrar un mensaje o hacer algo más aquí
                                                                Toast.makeText(EditarActividad.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                                                            });
                                                } else {
                                                    //estas seguro?
                                                    // Crea un diálogo de alerta para confirmar la acción.
                                                    new AlertDialog.Builder(EditarActividad.this)
                                                            .setTitle("Confirmar acción")
                                                            .setMessage("¿Estás seguro de que deseas asignar a '"+delegadoNombre+"' como delegado de esta actividad")
                                                            .setPositiveButton("Sí", (dialog, which) -> {
                                                                // Usuario ha confirmado la acción.
                                                                // Crea y guarda la publicación sin URL de imagen
                                                                editarActividad(idActividad, codigoDelegado, delegadoNombre, nombre, contenido, fotoactual,estado);
                                                            })
                                                            .setNegativeButton("Cancelar", (dialog, which) -> {
                                                                // Usuario ha cancelado la acción.
                                                                dialog.dismiss(); // Cierra el diálogo.
                                                            })
                                                            .show();

                                                }
                                            }
                                        } else {
                                            // Ocurrió un error al buscar el usuario
                                            // Puedes mostrar un mensaje o hacer algo más aquí
                                            Toast.makeText(EditarActividad.this, "Error al buscar el usuario", Toast.LENGTH_SHORT).show();
                                        }
                                    });


                        }

                    }
                });





            }
        });

    }

    private void editarActividad(String actividadId,int nuevoDelegadoCodigo, String nuevoDelegadoNombre, String nuevoNombre, String nuevoContenido, String nuevoFotoLink, String nuevoEstado) {
        // Obtén una referencia al documento que deseas editar
        DocumentReference docRef = db.collection("actividades").document(actividadId);

        // Crea un mapa con los campos que deseas actualizar
        Map<String, Object> updates = new HashMap<>();
        updates.put("nombre", nuevoNombre);
        updates.put("descripcion", nuevoContenido);
        updates.put("fotoLink", nuevoFotoLink);
        updates.put("estado", nuevoEstado);
        updates.put("delegadoCode", nuevoDelegadoCodigo);
        updates.put("delegadoName", nuevoDelegadoNombre);

        // Actualiza el documento con los nuevos valores
        docRef.update(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Actividad editada con éxito
                        Toast.makeText(EditarActividad.this, "Actividad editada con éxito", Toast.LENGTH_SHORT).show();


                        startActivity(new Intent(EditarActividad.this, BaseGeneralActivity.class));
                        finish(); // Opcionalmente puedes finalizar la actividad actual si no quieres que el usuario regrese a ella.

                    } else {
                        // Ocurrió un error al editar la actividad
                        Toast.makeText(EditarActividad.this, "Error al editar la actividad", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            ivSelectedImage.setImageURI(selectedImageUri);
        }
    }

}