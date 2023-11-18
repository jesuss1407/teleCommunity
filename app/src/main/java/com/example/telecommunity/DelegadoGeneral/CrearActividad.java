package com.example.telecommunity.DelegadoGeneral;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import com.example.telecommunity.CrearPublicacionActivity;
import com.example.telecommunity.R;
import com.example.telecommunity.RegistroUsuario;
import com.example.telecommunity.entity.ActividadDto;
import com.example.telecommunity.entity.Publicaciondto;
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

import java.util.UUID;

public class CrearActividad extends AppCompatActivity {

    private static final int SELECT_IMAGE_REQUEST_CODE = 1001;
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private String delegadoNombre, userCodeStr;
    private int userCode;


    EditText etActividad, etContenido, etCodigoDelegado;

    Button btnGuardarPublicacion;
    ImageView ivSelectedImage;
    Button btnSelectImage;

    Uri selectedImageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_actividad);
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        etActividad = findViewById(R.id.etActividad);
        etCodigoDelegado= findViewById(R.id.delegadoCodigo);
        etContenido = findViewById(R.id.etContenido);
        btnGuardarPublicacion = findViewById(R.id.btnGuardarPublicacion);
        ivSelectedImage = findViewById(R.id.ivSelectedImage);
        btnSelectImage = findViewById(R.id.btnSelectImage);

        // Configurar botón para seleccionar imagen
        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, SELECT_IMAGE_REQUEST_CODE);
        });


        // Configurar botón para crear actividad
        btnGuardarPublicacion.setOnClickListener(v -> {

            // Obtén los datos del formulario
            String nombre = etActividad.getText().toString().trim();
            String contenido = etContenido.getText().toString().trim();
            String codigoDelegadoStr = etCodigoDelegado.getText().toString().trim();
            String estado = "En curso";
            FirebaseFirestore db1 = FirebaseFirestore.getInstance();
            DocumentReference docRef1 = db1.collection("usuarios").document(codigoDelegadoStr);


            if (nombre.isEmpty() || contenido.isEmpty() ||codigoDelegadoStr.isEmpty() ){
                Toast.makeText(CrearActividad.this, "Llene todos los campos ", Toast.LENGTH_SHORT).show();
            } else if(codigoDelegadoStr.equals("20196324")){
                Toast.makeText(CrearActividad.this, "El código ingresado es el del delegado general", Toast.LENGTH_SHORT).show();
            } else if(!(!TextUtils.isEmpty(codigoDelegadoStr) && TextUtils.isDigitsOnly(codigoDelegadoStr))){
                Toast.makeText(CrearActividad.this, "Ingrese solo numeros en el Código", Toast.LENGTH_SHORT).show();
            } else if(docRef1.getId().isEmpty()){
                Toast.makeText(CrearActividad.this, "Ingrese un codigo de usuario existente", Toast.LENGTH_SHORT).show();
            }else {

                int codigoDelegado = Integer.parseInt(codigoDelegadoStr);



                // Crea una referencia a la colección "usuarios" y al documento específico con el ID en mycode
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("usuarios").document(codigoDelegadoStr);
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (!documentSnapshot.exists()) {
                            Toast.makeText(CrearActividad.this, "Ingrese un codigo de usuario existente", Toast.LENGTH_SHORT).show();
                        }else{
                            // El documento existe, ahora puedes obtener el campo "nombre"
                            String nombredele = documentSnapshot.getString("nombre");
                            String apellido = documentSnapshot.getString("apellido");
                            delegadoNombre = nombredele + " "+apellido;


                            // Obtén el usuario logueado
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                String userEmail = user.getEmail();
                                // Busca el nombre del usuario en la colección de usuarios
                                db.collection("usuarios")
                                        .whereEqualTo("correo", userEmail)
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
                                                                            new AlertDialog.Builder(CrearActividad.this)
                                                                                    .setTitle("Confirmar acción")
                                                                                    .setMessage("¿Estás seguro de que deseas asignar a '"+delegadoNombre+"' como delegado de esta actividad")
                                                                                    .setPositiveButton("Sí", (dialog, which) -> {
                                                                                        // Usuario ha confirmado la acción.
                                                                                        // Crea y guarda la publicación con la URL de la imagen
                                                                                        crearActividad(codigoDelegado, delegadoNombre, nombre, contenido, uri.toString(),estado);
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
                                                                    Toast.makeText(CrearActividad.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                                                                });
                                                    } else {
                                                        //estas seguro?
                                                        // Crea un diálogo de alerta para confirmar la acción.
                                                        new AlertDialog.Builder(CrearActividad.this)
                                                                .setTitle("Confirmar acción")
                                                                .setMessage("¿Estás seguro de que deseas asignar a '"+delegadoNombre+"' como delegado de esta actividad")
                                                                .setPositiveButton("Sí", (dialog, which) -> {
                                                                    // Usuario ha confirmado la acción.
                                                                    // Crea y guarda la publicación sin URL de imagen
                                                                    crearActividad(codigoDelegado, delegadoNombre, nombre, contenido, "https://firebasestorage.googleapis.com/v0/b/telecommunity-cbff5.appspot.com/o/images%2Ffutsal_damas.jpg?alt=media&token=1d5fec95-a240-4d8c-ac5b-626b3d433820",estado);
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
                                                Toast.makeText(CrearActividad.this, "Error al buscar el usuario", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }

                    }
                });





            }
        });

    }

    private void crearActividad(int delegadoCodigo, String delegadoNombre, String nombre, String contenido, String fotoLink, String estado) {
        String id = UUID.randomUUID().toString();
        ActividadDto actividad = new ActividadDto(
                id,
                delegadoCodigo,
                delegadoNombre,
                nombre,
                contenido,
                fotoLink,
                estado
        );

        userCodeStr=Integer.toString(delegadoCodigo);
        cambiarRolDelegado();



        db.collection("actividades")
                .document(id)
                .set(actividad)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Publicación guardada con éxito
                            Toast.makeText(CrearActividad.this, "Actividad creada con éxito", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            // Ocurrió un error al guardar la publicación
                            Toast.makeText(CrearActividad.this, "Error al crear la actividad", Toast.LENGTH_SHORT).show();
                        }
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


    public void cambiarRolDelegado() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference actividadRef = db.collection("usuarios").document(userCodeStr);

//                              Actualiza el campo "estado" a "Finalizado".
        actividadRef.update("rol", "Delegado de actividad")
                .addOnSuccessListener(aVoid -> {
                    // El estado se actualizó con éxito en Firestore.
                    //Toast.makeText(CrearActividad.this, "Delegnado asignado con éxito", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Ocurrió un error al actualizar el estado en Firestore.
                    // Puedes mostrar un mensaje de error o realizar acciones de manejo de errores.
                });




    }

}