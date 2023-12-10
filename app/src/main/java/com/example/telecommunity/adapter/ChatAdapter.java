package com.example.telecommunity.adapter;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.chat.models.TextMessage;
import com.example.telecommunity.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private List<TextMessage> messageList;
    private String currentUserID;

    public ChatAdapter(List<TextMessage> messageList, String currentUserID) {
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
        TextMessage message = messageList.get(position);
        boolean isOutgoing = message.getSender().getUid().equals(currentUserID);

        holder.senderNameTextView.setText(message.getSender().getName());

        if (isOutgoing) {
            holder.messageTextView.setBackgroundResource(R.drawable.message_background_outgoing);
            holder.messageContentContainer.setGravity(Gravity.END);
            holder.senderNameTextView.setVisibility(View.GONE);
        } else {
            holder.messageTextView.setBackgroundResource(R.drawable.message_background);
            holder.messageContentContainer.setGravity(Gravity.START);
            holder.senderNameTextView.setVisibility(View.VISIBLE);
        }

        holder.messageTextView.setText(message.getText());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView senderNameTextView;
        LinearLayout messageContentContainer;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            senderNameTextView = itemView.findViewById(R.id.senderNameTextView);
            messageContentContainer = itemView.findViewById(R.id.todoLayout);
        }
    }
}