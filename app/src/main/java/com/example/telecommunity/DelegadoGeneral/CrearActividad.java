package com.example.telecommunity.DelegadoGeneral;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.telecommunity.CrearPublicacionActivity;
import com.example.telecommunity.R;
import com.example.telecommunity.entity.ActividadDto;
import com.example.telecommunity.entity.Publicaciondto;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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


        // Configurar botón para guardar publicación
        btnGuardarPublicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtén los datos del formulario
                String nombre = etActividad.getText().toString().trim();
                String contenido = etContenido.getText().toString().trim();
                String codigoDelegadoStr = etCodigoDelegado.getText().toString().trim();
                int codigoDelegado = Integer.parseInt(codigoDelegadoStr);

                // Obtén el usuario logueado
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    String userEmail = user.getEmail();
                    // Busca el nombre del usuario en la colección de usuarios
                    db.collection("usuarios")
                            .whereEqualTo("correo", userEmail)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
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
                                                                    // Crea y guarda la publicación con la URL de la imagen
                                                                    guardarActividad(codigoDelegado, nombre, contenido, uri.toString());
                                                                }))
                                                        .addOnFailureListener(e -> {
                                                            // Ocurrió un error al subir la imagen
                                                            // Puedes mostrar un mensaje o hacer algo más aquí
                                                            Toast.makeText(CrearActividad.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                                                        });
                                            } else {
                                                // Crea y guarda la publicación sin URL de imagen
                                                guardarActividad(codigoDelegado, nombre, contenido, "");
                                            }
                                        }
                                    } else {
                                        // Ocurrió un error al buscar el usuario
                                        // Puedes mostrar un mensaje o hacer algo más aquí
                                        Toast.makeText(CrearActividad.this, "Error al buscar el usuario", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

    }

    private void guardarActividad(int delegadoCodigo, String nombre, String contenido, String fotoLink) {
        String id = UUID.randomUUID().toString();
        ActividadDto actividad = new ActividadDto(
                id,
                delegadoCodigo,
                nombre,
                contenido,
                fotoLink
        );

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



}