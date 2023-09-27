package com.example.telecommunity.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
        Button comentariosButton = holder.itemView.findViewById(R.id.button_comentar);
        String mensaje = "Comentarios (" + publicacion.getComentarios() + ")";
        comentariosButton.setText(mensaje);
        holder.fotoAdjunta.setImageResource(publicacion.getFotoId());
        holder.fotoAdjunta.setVisibility(publicacion.getFotoId() != 0 ? View.VISIBLE : View.GONE);
        int fotoId = publicacion.getFotoId();
        if (fotoId != 0) {
            Drawable fotoDrawable = holder.itemView.getContext().getDrawable(fotoId);
            if (fotoDrawable != null) {
                int anchoReal = fotoDrawable.getIntrinsicWidth();
                int altoReal = fotoDrawable.getIntrinsicHeight();

                // Establecer un límite de altura máximo para la imagen
                int alturaMaxima = holder.itemView.getResources().getDimensionPixelSize(R.dimen.max_image_height);
                if (altoReal > alturaMaxima) {
                    // Si la altura real de la imagen es mayor que la altura máxima permitida, redimensionarla
                    float proporcion = (float) alturaMaxima / (float) altoReal;
                    anchoReal = (int) (anchoReal * proporcion);
                    altoReal = alturaMaxima;
                }

                // Establece el tamaño del ImageView en función de las dimensiones reales o redimensionadas de la imagen
                holder.fotoAdjunta.getLayoutParams().width = anchoReal;
                holder.fotoAdjunta.getLayoutParams().height = altoReal;
                holder.fotoAdjunta.setImageResource(fotoId);
                holder.fotoAdjunta.setVisibility(View.VISIBLE);
            }
        } else {
            holder.fotoAdjunta.setVisibility(View.GONE);
        }


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
        ImageView fotoAdjunta;

        public PublicacionViewHolder(@NonNull View itemView) {
            super(itemView);
            fotouser = itemView.findViewById(R.id.user_photo);
            nameuser = itemView.findViewById(R.id.post_nombre);
            timestamp = itemView.findViewById(R.id.post_hora);
            date = itemView.findViewById(R.id.post_fecha);
            location = itemView.findViewById(R.id.post_ubicacion);
            actividad = itemView.findViewById(R.id.post_activity);
            contenido = itemView.findViewById(R.id.post_contenido);
            fotoAdjunta = itemView.findViewById(R.id.photo_space);
        }
    }
}

