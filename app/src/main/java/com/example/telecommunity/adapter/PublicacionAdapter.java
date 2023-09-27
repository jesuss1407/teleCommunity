package com.example.telecommunity.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.telecommunity.NotificationDetailActivity;
import com.example.telecommunity.R;
import com.example.telecommunity.entity.NotificationItem;
import com.example.telecommunity.entity.Publicaciondto;

import java.util.List;

public class PublicacionAdapter extends RecyclerView.Adapter<PublicacionAdapter.PublicacionViewHolder> {
    private List<Publicaciondto> publicacionList;
    private Context context;

    public PublicacionAdapter(List<Publicaciondto> publicacionList, Context context) {
        this.publicacionList = publicacionList;
        this.context = context;
    }

    @NonNull
    @Override
    public PublicacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_publicacion, parent, false);
        return new PublicacionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PublicacionViewHolder holder, int position) {
        Publicaciondto publicacion = publicacionList.get(position);

        // Rellena los elementos de notificación con los datos
        holder.fotouser.setImageResource(publicacion.getPhotoResId());
        holder.nameuser.setText(publicacion.getUsuario());
        holder.timestamp.setText(publicacion.getTimestamp());
        holder.date.setText(publicacion.getDate());
        holder.location.setText(publicacion.getLocation());
        holder.actividad.setText(publicacion.getActividad());
        holder.contenido.setText(publicacion.getContenido());



    }

    @Override
    public int getItemCount() {
        return publicacionList.size();
    }

    public static class PublicacionViewHolder extends RecyclerView.ViewHolder {
        ImageView fotouser;
        TextView nameuser;
        TextView timestamp;
        TextView date;
        TextView location;
        TextView actividad;
        TextView contenido;

        public PublicacionViewHolder(@NonNull View itemView) {
            super(itemView);
            fotouser = itemView.findViewById(R.id.user_photo);
            nameuser = itemView.findViewById(R.id.post_nombre);
            timestamp = itemView.findViewById(R.id.post_hora);
            date = itemView.findViewById(R.id.post_fecha);
            location = itemView.findViewById(R.id.post_ubicacion);
            actividad = itemView.findViewById(R.id.post_activity);
            contenido = itemView.findViewById(R.id.post_contenido);
        }
    }
}

