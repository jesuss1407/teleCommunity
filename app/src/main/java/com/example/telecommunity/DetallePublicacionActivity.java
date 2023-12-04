package com.example.telecommunity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.telecommunity.adapter.ComentariosAdapter;
import com.example.telecommunity.entity.Comentario;
import com.example.telecommunity.entity.Publicaciondto;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.telecommunity.DetallePublicacionActivity;
import com.example.telecommunity.R;
import com.example.telecommunity.entity.Publicaciondto;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import com.google.firebase.firestore.Query;

public class DetallePublicacionActivity extends AppCompatActivity {

    // ...
    private static final String TAG = "DetallePublicacionActivity";
    private ComentariosAdapter adapter;
    private EditText editTextComentario;
    private Button buttonEnviarComentario;
    TextView tvUbicacionNombre;
    private Button btnUnirse;
    private String publicacionId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_publicacion);

        tvUbicacionNombre = findViewById(R.id.tvUbicacionNombre);
        // Obtén el ID de la publicación desde el Intent
        publicacionId = getIntent().getStringExtra("publicacionId");
        Log.d(TAG, "Recibido Publicacion ID: " + publicacionId);

        if (publicacionId == null) {
            Log.e(TAG, "No se ha proporcionado el ID de la publicación");
            finish();
            return;
        } else {
            Log.d(TAG, "publicacionId: " + publicacionId);
        }


        // Obtén referencias a los TextView e ImageView
        TextView postNombre = findViewById(R.id.post_nombre);
        TextView postHora = findViewById(R.id.post_hora);
        TextView postFecha = findViewById(R.id.post_fecha);
        TextView postUbicacion = findViewById(R.id.tvUbicacionNombre);
        TextView postActivity = findViewById(R.id.post_activity);
        TextView postContenido = findViewById(R.id.post_contenido);
        ImageView userPhoto = findViewById(R.id.user_photo);
        ImageView photoSpace = findViewById(R.id.photo_space);

        // Obtén la referencia a la colección de publicaciones en Firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference publicacionesRef = db.collection("publicaciones");

        // Inicialización del RecyclerView y el adapter
        RecyclerView recyclerViewComentarios = findViewById(R.id.recycler_view_comentarios);
        recyclerViewComentarios.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ComentariosAdapter();
        recyclerViewComentarios.setAdapter(adapter);

        // Obtén el documento específico de la publicación
        publicacionesRef.document(publicacionId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Convierte el documento en un objeto Publicaciondto
                            Publicaciondto publicacion = document.toObject(Publicaciondto.class);
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                            // Usa el objeto para cargar los datos en los TextView e ImageView
                            String nombreCompleto = publicacion.getNombreUsuario() + " " + publicacion.getApellidoUsuario();
                            postNombre.setText(nombreCompleto);

                            long timestamp = publicacion.getHoraCreacion();
                            Date date = new Date(timestamp);

                            // Formatear la hora
                            SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");
                            sdfHora.setTimeZone(TimeZone.getDefault());
                            String hora = sdfHora.format(date);
                            postHora.setText(hora);

                            // Formatear la fecha
                            SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yy");
                            sdfFecha.setTimeZone(TimeZone.getDefault());
                            String fecha = sdfFecha.format(date);
                            postFecha.setText(fecha);

                            // Aquí debes mostrar el nombre de la ubicación
                            postUbicacion.setText(publicacion.getNombreUbicacion());

                            postActivity.setText(publicacion.getNombre());
                            postContenido.setText(publicacion.getContenido());

                            // Cargar la imagen del perfil del usuario
                            Glide.with(DetallePublicacionActivity.this)
                                    .load(publicacion.getFotoUsuario())
                                    .into(userPhoto);

                            // Cargar la imagen de la publicación si existe
                            if (publicacion.getUrlImagen() != null && !publicacion.getUrlImagen().isEmpty()) {
                                photoSpace.setVisibility(View.VISIBLE);
                                Glide.with(DetallePublicacionActivity.this)
                                        .load(publicacion.getUrlImagen())
                                        .into(photoSpace);
                            } else {
                                photoSpace.setVisibility(View.GONE);
                            }
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                });


        // Obtén referencias a los TextView, ImageView, EditText y Button
        editTextComentario = findViewById(R.id.edit_text_comentario);
        buttonEnviarComentario = findViewById(R.id.button_enviar_comentario);

        double latitud = getIntent().getDoubleExtra("latitud", 0);
        double longitud = getIntent().getDoubleExtra("longitud", 0);
        // Configura el OnClickListener para el botón de Maps
        ImageView  btnMaps = findViewById(R.id.btnMaps);
        btnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear una URI para la dirección de Google Maps con las coordenadas de destino
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitud + "," + longitud);

                // Crear un intent para abrir Google Maps con la ruta
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps"); // Forzar el uso de la aplicación de Google Maps

                // Verificar si Google Maps está instalado en el dispositivo
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    // Si Google Maps no está instalado, puedes mostrar un mensaje de error
                    Toast.makeText(DetallePublicacionActivity.this, "Google Maps no está instalado en este dispositivo.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Configura el OnClickListener para el botón de comentar
        Button buttonComentar = findViewById(R.id.button_comentar);
        buttonComentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextComentario.setVisibility(View.VISIBLE);
                buttonEnviarComentario.setVisibility(View.VISIBLE);

                editTextComentario.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editTextComentario, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        // Añadir OnClickListener al Button
        buttonEnviarComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comentarioTexto = editTextComentario.getText().toString().trim();
                if (!comentarioTexto.isEmpty()) {
                    enviarComentario(comentarioTexto);
                }
            }
        });




        // Obtén la referencia a la colección de comentarios en Firebase
        CollectionReference comentariosRef = db.collection("comentarios");

        // Obtén los comentarios de la publicación
        comentariosRef.document(publicacionId)
                .collection("comentarios")
                .orderBy("hora", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Comentario> comentarios = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Comentario comentario = document.toObject(Comentario.class);
                                comentarios.add(comentario);
                            }
                            // Actualiza el RecyclerView con los comentarios
                            if (comentarios.size() > 0) {
                                adapter.setComentarios(comentarios);
                                adapter.notifyDataSetChanged();
                            } else {
                                // Maneja el caso en que no hay comentarios
                                // Puedes mostrar un mensaje o dejar el RecyclerView vacío
                            }
                        } else {
                            Log.w(TAG, "Error getting comments.", task.getException());
                        }
                    }
                });

        btnUnirse = findViewById(R.id.button_unirse);

        verificarUnionEvento();

    }

    private void verificarUnionEvento() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (publicacionId != null) {

            if (user != null && user.getEmail() != null) {
                String userEmail = user.getEmail();

                db.collection("usuarios").whereEqualTo("correo", userEmail).get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                DocumentSnapshot userDocument = queryDocumentSnapshots.getDocuments().get(0);
                                Long codigo = userDocument.getLong("codigo");

                                if (codigo != null) {
                                    String userCodigoAsString = codigo.toString();

                                    db.collection("usuarios")
                                            .document(userCodigoAsString)
                                            .collection("eventos")
                                            .document(publicacionId)
                                            .get()
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful() && task.getResult().exists()) {
                                                    btnUnirse.setText("Unido");
                                                    btnUnirse.setEnabled(false);
                                                } else {
                                                    btnUnirse.setText("Unirse");
                                                    btnUnirse.setEnabled(true);
                                                    btnUnirse.setOnClickListener(v -> showJoinEventDialog(userCodigoAsString));
                                                }
                                            });
                                }
                            }
                        });
            }
        }
    }


    private void showJoinEventDialog(String userCodigo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetallePublicacionActivity.this);
        builder.setMessage("¿Desea unirse a este evento?")
                .setPositiveButton("Aceptar", (dialog, id) -> unirseAlEvento(userCodigo))
                .setNegativeButton("Cancelar", (dialog, id) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void unirseAlEvento(String userCodigo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> eventToJoin = new HashMap<>();
        eventToJoin.put("idEvento", publicacionId);

        db.collection("usuarios")
                .document(userCodigo)
                .collection("eventos")
                .document(publicacionId)
                .set(eventToJoin)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User successfully joined the event!");
                    btnUnirse.setText("Unido");
                    btnUnirse.setEnabled(false);
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error joining event", e));
    }


    private void enviarComentario(String comentarioTexto) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference comentariosRef = db.collection("comentarios");

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

                                    Map<String, Object> comentario = new HashMap<>();
                                    comentario.put("nombre", userName);
                                    comentario.put("apellido", userApellido);
                                    comentario.put("fotoUsuario", userFotoPerfil);
                                    comentario.put("contenido", comentarioTexto);
                                    comentario.put("hora", System.currentTimeMillis());

                                    // Obtén el ID de la publicación desde el Intent

                                    comentariosRef.document(publicacionId)
                                            .collection("comentarios")
                                            .add(comentario)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    // Limpiar EditText
                                                    editTextComentario.setText("");

                                                    // Recargar comentarios
                                                    cargarComentarios();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(DetallePublicacionActivity.this, "Error al enviar el comentario", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            } else {
                                // Ocurrió un error al buscar el usuario
                                // Puedes mostrar un mensaje o hacer algo más aquí
                                Toast.makeText(DetallePublicacionActivity.this, "Error al buscar el usuario", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


    private void cargarComentarios() {
        // Obtén la referencia a la colección de comentarios en Firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference comentariosRef = db.collection("comentarios");

        // Obtén el ID de la publicación desde el Intent

        // Comprueba que el ID de la publicación no sea nulo
        if (publicacionId == null) {
            Log.e(TAG, "No se ha proporcionado el ID de la publicación");
            return;
        }

        // Obtén los comentarios de la publicación
        comentariosRef.document(publicacionId)
                .collection("comentarios")
                .orderBy("hora", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Comentario> comentarios = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Comentario comentario = document.toObject(Comentario.class);
                                comentarios.add(comentario);
                            }
                            // Actualiza el RecyclerView con los comentarios
                            if (comentarios.size() > 0) {
                                adapter.setComentarios(comentarios);
                                adapter.notifyDataSetChanged();
                            } else {
                                // Maneja el caso en que no hay comentarios
                                // Puedes mostrar un mensaje o dejar el RecyclerView vacío
                            }
                        } else {
                            Log.w(TAG, "Error getting comments.", task.getException());
                        }
                    }
                });
    }



}
