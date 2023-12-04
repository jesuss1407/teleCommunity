package com.example.telecommunity.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.telecommunity.DetallePublicacionActivity;
import com.example.telecommunity.R;
import com.example.telecommunity.entity.Publicaciondto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;


public class PublicacionAdapter extends RecyclerView.Adapter<PublicacionAdapter.PublicacionViewHolder> {
    private static final String TAG = "PublicacionAdapter";
    private List<Publicaciondto> publicaciones;
    private Context context;


    public PublicacionAdapter(List<Publicaciondto> publicaciones, Context context) {
        this.publicaciones = publicaciones;
        this.context = context;
    }

    @NonNull
    @Override
    public PublicacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_publicacion, parent, false);
        return new PublicacionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PublicacionViewHolder holder, int position) {
        Publicaciondto publicacion = publicaciones.get(position);
        holder.bind(publicacion);

        // OnClickListener para abrir la actividad de detalle de publicación
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetallePublicacionActivity.class);
            intent.putExtra("publicacionId", publicacion.getId());
            intent.putExtra("latitud", publicacion.getLatitud());
            intent.putExtra("longitud", publicacion.getLongitud());
            Log.d(TAG, "Publicacion ID: " + publicacion.getId());
            context.startActivity(intent);
        });

        holder.btnComentar.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetallePublicacionActivity.class);
            intent.putExtra("publicacionId", publicacion.getId());
            intent.putExtra("latitud", publicacion.getLatitud());
            intent.putExtra("longitud", publicacion.getLongitud());
            Log.d(TAG, "Publicacion ID: " + publicacion.getId());
            context.startActivity(intent);
        });

        holder.btnUnirse.setOnClickListener(v -> {
            showJoinEventDialog(holder.getAdapterPosition());
        });


    }

    private void showJoinEventDialog(int position) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String eventName = publicaciones.get(position).getNombre();
        String eventId = publicaciones.get(position).getId(); // Asume que getId() obtiene el ID del evento

        if (user != null && user.getEmail() != null) {
            String userEmail = user.getEmail();

            // Busca el documento del usuario basado en el correo electrónico
            db.collection("usuarios").whereEqualTo("correo", userEmail).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Obtiene el "codigo" del usuario
                            DocumentSnapshot userDocument = queryDocumentSnapshots.getDocuments().get(0);
                            Long codigo = userDocument.getLong("codigo"); // Obtiene el "codigo" como Long

                            if (codigo != null) {
                                // Con el "codigo" obtenido, procedemos a unir al usuario al evento
                                String userCodigoAsString = codigo.toString(); // Convierte el código a String si es necesario

                                CollectionReference eventRef = db.collection("usuarios")
                                        .document(userCodigoAsString)
                                        .collection("eventos");

                                Map<String, Object> eventToJoin = new HashMap<>();
                                eventToJoin.put("idEvento", eventId);

                                eventRef.document(eventId).set(eventToJoin)
                                        .addOnSuccessListener(aVoid -> Log.d("PublicacionAdapter", "User successfully joined the event!"))
                                        .addOnFailureListener(e -> Log.w("PublicacionAdapter", "Error joining event", e));

                                // Notifica a cualquier listener que el conjunto de datos ha cambiado
                                notifyDataSetChanged();
                            } else {
                                Log.d("PublicacionAdapter", "El código del usuario es nulo");
                            }
                        } else {
                            // No se encontró el documento del usuario
                            Log.d("PublicacionAdapter", "No user document with the email found");
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Error al buscar el usuario
                        Log.w("PublicacionAdapter", "Error fetching user document", e);
                    });

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Unirse al evento " + eventName)
                    .setPositiveButton("Aceptar", (dialog, id) -> {
                        // El código para unir al usuario al evento se maneja en el éxito de la consulta anterior
                    })
                    .setNegativeButton("Cancelar", (dialog, id) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }


    @Override
    public int getItemCount() {
        return publicaciones.size();
    }

    class PublicacionViewHolder extends RecyclerView.ViewHolder {
        TextView postNombre;
        TextView postHora;
        TextView postFecha;
        TextView postUbicacion;
        TextView postActivity;
        TextView postContenido;
        ImageView userPhoto;
        ImageView photoSpace;
        Button btnComentar;
        Button btnUnirse;
        ImageView btnMaps;

        public PublicacionViewHolder(@NonNull View itemView) {
            super(itemView);
            postNombre = itemView.findViewById(R.id.post_nombre);
            postHora = itemView.findViewById(R.id.post_hora);
            postFecha = itemView.findViewById(R.id.post_fecha);
            postUbicacion = itemView.findViewById(R.id.post_ubicacion);
            postActivity = itemView.findViewById(R.id.post_activity);
            postContenido = itemView.findViewById(R.id.post_contenido);
            userPhoto = itemView.findViewById(R.id.user_photo);
            photoSpace = itemView.findViewById(R.id.photo_space);
            btnComentar = itemView.findViewById(R.id.button_comentar);
            btnUnirse = itemView.findViewById(R.id.button_unirse);
            btnMaps = itemView.findViewById(R.id.btnMaps);

            btnMaps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Publicaciondto publicacion = publicaciones.get(position);

                    double latitudDestino = publicacion.getLatitud();
                    double longitudDestino = publicacion.getLongitud();

                    // Crear una URI para la dirección de Google Maps con las coordenadas de destino
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitudDestino + "," + longitudDestino);

                    // Crear un intent para abrir Google Maps con la ruta
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps"); // Forzar el uso de la aplicación de Google Maps

                    // Verificar si Google Maps está instalado en el dispositivo
                    if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(mapIntent);
                    } else {
                        // Si Google Maps no está instalado, puedes mostrar un mensaje de error
                        Toast.makeText(context, "Google Maps no está instalado en este dispositivo.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        public void bind(Publicaciondto publicacion) {
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
            postUbicacion.setText("");

            postActivity.setText(publicacion.getNombre());
            postContenido.setText(publicacion.getContenido());

            // Cargar la imagen del perfil del usuario
            Glide.with(context)
                    .load(publicacion.getFotoUsuario())
                    .into(userPhoto);

            // Cargar la imagen de la publicación si existe
            if (publicacion.getUrlImagen() != null && !publicacion.getUrlImagen().isEmpty()) {
                photoSpace.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(publicacion.getUrlImagen())
                        .into(photoSpace);
            } else {
                photoSpace.setVisibility(View.GONE);
            }
            // Mostrar el nombre de la ubicación
            postUbicacion.setText(publicacion.getNombreUbicacion());
        }
    }
}
