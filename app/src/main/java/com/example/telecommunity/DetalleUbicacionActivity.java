package com.example.telecommunity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.telecommunity.entity.Evento;
import com.example.telecommunity.entity.Publicaciondto;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DetalleUbicacionActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private String publicacionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_ubicacion);

        db = FirebaseFirestore.getInstance();

        // Recupera el ID de la publicación de la actividad anterior
        publicacionId = getIntent().getStringExtra("publicacionId");

        // Obtiene los datos de la publicación de Firebase Firestore
        db.collection("publicaciones")
                .document(publicacionId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Publicaciondto publicacion = documentSnapshot.toObject(Publicaciondto.class);

                        // Muestra la información en la interfaz de usuario
                        TextView tituloTextView = findViewById(R.id.tituloTextView);
                        TextView descripcionTextView = findViewById(R.id.descripcionTextView);
                        TextView horaTextView = findViewById(R.id.horaTextView);
                        TextView creadorTextView = findViewById(R.id.creadorTextView);

                        String nombreCompleto = publicacion.getNombreUsuario() + " " + publicacion.getApellidoUsuario();
                        long timestamp = publicacion.getHoraCreacion();
                        Date date = new Date(timestamp);

                        // Formatear la hora
                        SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");
                        sdfHora.setTimeZone(TimeZone.getDefault());
                        String hora = sdfHora.format(date);

                        // Formatear la fecha
                        SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yy");
                        sdfFecha.setTimeZone(TimeZone.getDefault());
                        String fecha = sdfFecha.format(date);

                        tituloTextView.setText(publicacion.getNombre());
                        descripcionTextView.setText(publicacion.getContenido());
                        horaTextView.setText("Hora: " + hora);
                        creadorTextView.setText("Creado por: " + nombreCompleto);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("DetalleUbicacionActivity", "Error al obtener los datos de la publicación", e);
                });
    }
}

