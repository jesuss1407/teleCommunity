package com.example.telecommunity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.telecommunity.entity.Publicaciondto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.telecommunity.entity.Publicaciondto;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class CrearPublicacionActivity extends AppCompatActivity {

    private static final int SELECT_IMAGE_REQUEST_CODE = 1001;
    private FirebaseFirestore db;
    private StorageReference storageRef;

    EditText etActividad, etContenido;
    Spinner spinnerUbicacion;
    Button btnGuardarPublicacion;
    ImageView ivSelectedImage;
    Button btnSelectImage;

    Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_publicacion);
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        etActividad = findViewById(R.id.etActividad);
        etContenido = findViewById(R.id.etContenido);
        spinnerUbicacion = findViewById(R.id.spinnerUbicacion);
        btnGuardarPublicacion = findViewById(R.id.btnGuardarPublicacion);
        ivSelectedImage = findViewById(R.id.ivSelectedImage);
        btnSelectImage = findViewById(R.id.btnSelectImage);

        // Configurar botón para seleccionar imagen
        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, SELECT_IMAGE_REQUEST_CODE);
        });

        // Poblar el Spinner con ubicaciones
        List<Ubicacion> ubicaciones = new ArrayList<>();
        ubicaciones.add(new Ubicacion("Pabellón V", -12.073054384334577, -77.08193894376977));
        ubicaciones.add(new Ubicacion("DigiMundo", -12.072986534574376, -77.08130425461972));
        ubicaciones.add(new Ubicacion("Pabellón O", -12.07272368237049, -77.08256651243227));
        ubicaciones.add(new Ubicacion("Cancha Minas", -12.072181100279822, -77.0819714566139));
        ubicaciones.add(new Ubicacion("CIA", -12.072046089823811, -77.08035723983103));
        ubicaciones.add(new Ubicacion("Pastitos de Arqui", -12.071438702349496, -77.08122130635161));
        ubicaciones.add(new Ubicacion("Comedor Central", -12.07059754831056, -77.08100220132977));

        // Agrega más ubicaciones según necesites

        ArrayAdapter<Ubicacion> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ubicaciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUbicacion.setAdapter(adapter);

        // Configurar botón para guardar publicación
        btnGuardarPublicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtén los datos del formulario
                String nombre = etActividad.getText().toString().trim();
                String contenido = etContenido.getText().toString().trim();
                Ubicacion ubicacionSeleccionada = (Ubicacion) spinnerUbicacion.getSelectedItem();
                double latitud = ubicacionSeleccionada.latitud;
                double longitud = ubicacionSeleccionada.longitud;

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
                                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                            String userName = document.getString("nombre");
                                            String userApellido = document.getString("apellido");
                                            String userFotoPerfil = document.getString("foto");

                                            // Verifica si se seleccionó una imagen
                                            if (selectedImageUri != null) {
                                                // Sube la imagen a Firebase Storage y obtén el URL
                                                StorageReference fileRef = storageRef.child("images/" + System.currentTimeMillis() + ".jpg");
                                                fileRef.putFile(selectedImageUri)
                                                        .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl()
                                                                .addOnSuccessListener(uri -> {
                                                                    // Crea y guarda la publicación con la URL de la imagen
                                                                    guardarPublicacion(nombre, contenido, latitud, longitud, userName, userApellido, userFotoPerfil, uri.toString());
                                                                }))
                                                        .addOnFailureListener(e -> {
                                                            // Ocurrió un error al subir la imagen
                                                            // Puedes mostrar un mensaje o hacer algo más aquí
                                                            Toast.makeText(CrearPublicacionActivity.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                                                        });
                                            } else {
                                                // Crea y guarda la publicación sin URL de imagen
                                                guardarPublicacion(nombre, contenido, latitud, longitud, userName, userApellido, userFotoPerfil, "");
                                            }
                                        }
                                    } else {
                                        // Ocurrió un error al buscar el usuario
                                        // Puedes mostrar un mensaje o hacer algo más aquí
                                        Toast.makeText(CrearPublicacionActivity.this, "Error al buscar el usuario", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void guardarPublicacion(String nombre, String contenido, double latitud, double longitud, String userName, String userApellido, String userFotoPerfil, String urlImagen) {
        Publicaciondto publicacion = new Publicaciondto(
                nombre,
                System.currentTimeMillis(),
                contenido,
                urlImagen,
                latitud,
                longitud,
                userName,
                userApellido,
                userFotoPerfil
        );

        db.collection("publicaciones")
                .add(publicacion)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            // Publicación guardada con éxito
                            // Puedes mostrar un mensaje o hacer algo más aquí
                            Toast.makeText(CrearPublicacionActivity.this, "Publicación guardada con éxito", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            // Ocurrió un error al guardar la publicación
                            // Puedes mostrar un mensaje o hacer algo más aquí
                            Toast.makeText(CrearPublicacionActivity.this, "Error al guardar la publicación", Toast.LENGTH_SHORT).show();
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

    // Clase para representar una ubicación con nombre, latitud y longitud
    public static class Ubicacion {
        String nombre;
        double latitud;
        double longitud;

        public Ubicacion(String nombre, double latitud, double longitud) {
            this.nombre = nombre;
            this.latitud = latitud;
            this.longitud = longitud;
        }

        @Override
        public String toString() {
            return nombre;
        }
    }
}
