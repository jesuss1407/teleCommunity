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
import com.example.telecommunity.DetallePublicacionActivity;
import com.example.telecommunity.R;
import com.example.telecommunity.entity.Comentario;
import com.example.telecommunity.entity.Publicaciondto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import android.widget.TextView;

public class ComentariosAdapter extends RecyclerView.Adapter<ComentariosAdapter.ViewHolder> {

    private List<Comentario> comentarios;
    public ComentariosAdapter() {
        this.comentarios = new ArrayList<>(); // Inicializa la lista aquí

    }

    public void setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
        notifyDataSetChanged();
    }

    public ComentariosAdapter(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comentario, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comentario comentario = comentarios.get(position);
        holder.bind(comentario);
    }

    @Override
    public int getItemCount() {
        return comentarios.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView nombreTextView;
        TextView postContenido;
        TextView postHora;
        TextView postFecha;
        ImageView userPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.nombre_text_view);
            postHora = itemView.findViewById(R.id.hora_text_view);
            postFecha = itemView.findViewById(R.id.fecha_text_view);
            postContenido = itemView.findViewById(R.id.contenido_text_view);
            userPhoto = itemView.findViewById(R.id.user_photo);
        }

        public void bind(Comentario comentario) {
            String nombreCompleto = comentario.getNombre() + " " + comentario.getApellido();
            nombreTextView.setText(nombreCompleto);

            long timestamp = comentario.getHora();
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

            postContenido.setText(comentario.getContenido());

            // Cargar la imagen del perfil del usuario
            Glide.with(itemView.getContext())
                    .load(comentario.getFotoUsuario())
                    .into(userPhoto);
        }
    }
}

