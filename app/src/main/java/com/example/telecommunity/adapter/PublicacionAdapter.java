package com.example.telecommunity.adapter;

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
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.telecommunity.R;
import com.example.telecommunity.entity.Publicaciondto;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class PublicacionAdapter extends RecyclerView.Adapter<PublicacionAdapter.PublicacionViewHolder> {
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
        }
    }
}
