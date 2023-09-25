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

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private List<NotificationItem> notificationList;
    private Context context;

    public NotificationAdapter(List<NotificationItem> notificationList, Context context) {
        this.notificationList = notificationList;
        this.context = context;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationItem notification = notificationList.get(position);

        // Rellena los elementos de notificación con los datos
        holder.photoImageView.setImageResource(notification.getPhotoResId());
        holder.titleTextView.setText(notification.getTitle());
        holder.timestampTextView.setText(notification.getTimestamp());
        holder.contentTextView.setText(notification.getContent());

        // Configura un OnClickListener para los elementos de notificación
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    NotificationItem clickedItem = notificationList.get(position);

                    // Inicia la actividad de detalle y pasa los datos de la notificación
                    Intent intent = new Intent(context, NotificationDetailActivity.class);
                    intent.putExtra("title", clickedItem.getTitle());
                    intent.putExtra("desc", clickedItem.getContent());
                    intent.putExtra("imageResource", clickedItem.getPhotoResId());
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView photoImageView;
        TextView titleTextView;
        TextView timestampTextView;
        TextView contentTextView;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.notification_photo);
            titleTextView = itemView.findViewById(R.id.notification_titulo);
            timestampTextView = itemView.findViewById(R.id.notification_tiempo);
            contentTextView = itemView.findViewById(R.id.notification_contenido);
        }
    }
}

