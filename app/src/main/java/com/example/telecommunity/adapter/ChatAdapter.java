package com.example.telecommunity.adapter;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cometchat.chat.models.Attachment;
import com.cometchat.chat.models.BaseMessage;
import com.cometchat.chat.models.MediaMessage;
import com.cometchat.chat.models.TextMessage;
import com.example.telecommunity.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private List<BaseMessage> messageList;
    private String currentUserID;

    public ChatAdapter(List<BaseMessage> messageList, String currentUserID) {
        this.messageList = messageList;
        this.currentUserID = currentUserID;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        BaseMessage baseMessage = messageList.get(position); // Cambiado a BaseMessage
        boolean isOutgoing = baseMessage.getSender().getUid().equals(currentUserID);

        holder.senderNameTextView.setText(baseMessage.getSender().getName());

        if (isOutgoing) {
            holder.messageTextView.setBackgroundResource(R.drawable.message_background_outgoing);
            holder.messageContentContainer.setGravity(Gravity.END);
            holder.senderNameTextView.setVisibility(View.GONE);
        } else {
            holder.messageTextView.setBackgroundResource(R.drawable.message_background);
            holder.messageContentContainer.setGravity(Gravity.START);
            holder.senderNameTextView.setVisibility(View.VISIBLE);
        }

        if (baseMessage instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) baseMessage;
            holder.messageTextView.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.GONE);
            holder.messageTextView.setText(textMessage.getText());
        } else if (baseMessage instanceof MediaMessage) {
            MediaMessage mediaMessage = (MediaMessage) baseMessage;
            Attachment attachment = mediaMessage.getAttachment();
            if (attachment != null) {
                String mediaUrl = attachment.getFileUrl();
                holder.messageTextView.setVisibility(View.GONE);
                holder.imageView.setVisibility(View.VISIBLE);
                Glide.with(context).load(mediaUrl).into(holder.imageView);

                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downloadImage(holder.itemView.getContext(), mediaUrl);
                    }
                });

            }
        }
    }
    private void downloadImage(Context context, String downloadUrl) {
        // Crear un AlertDialog para confirmar la descarga
        new AlertDialog.Builder(context)
                .setTitle("Descargar Imagen")
                .setMessage("¿Deseas descargar la imagen?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    // Mostrar un Toast antes de iniciar la descarga
                    Toast.makeText(context, "Descargando imagen...", Toast.LENGTH_LONG).show();

                    // Continuar con la descarga
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
                    request.setDescription("Descargando imagen...");
                    request.setTitle("Descarga de imagen");
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, Uri.parse(downloadUrl).getLastPathSegment());

                    DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    manager.enqueue(request);
                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> {
                    // Cancelar la descarga
                    dialog.dismiss();
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView senderNameTextView;
        ImageView imageView;
        LinearLayout messageContentContainer;
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            senderNameTextView = itemView.findViewById(R.id.senderNameTextView);
            imageView = itemView.findViewById(R.id.imageView);
            messageContentContainer = itemView.findViewById(R.id.todoLayout);
        }
    }
}