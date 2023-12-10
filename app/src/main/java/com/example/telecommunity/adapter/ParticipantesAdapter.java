package com.example.telecommunity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.telecommunity.R;
import com.example.telecommunity.entity.Comentario;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.telecommunity.R;
import com.example.telecommunity.entity.Participantedto;

import java.util.List;

public class ParticipantesAdapter extends RecyclerView.Adapter<ParticipantesAdapter.ParticipanteViewHolder> {

    private List<Participantedto> participantes;
    private Context context;

    public ParticipantesAdapter(List<Participantedto> participantes, Context context) {
        this.participantes = participantes;
        this.context = context;
    }

    @NonNull
    @Override
    public ParticipanteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_participante, parent, false);
        return new ParticipanteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipanteViewHolder holder, int position) {
        Participantedto participante = participantes.get(position);
        holder.bind(participante);
    }

    @Override
    public int getItemCount() {
        return participantes.size();
    }

    public class ParticipanteViewHolder extends RecyclerView.ViewHolder {
        // Define aquí los elementos de la UI, como TextViews para el nombre del participante y su rol

        public ParticipanteViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inicializa los elementos de la UI aquí
        }

        public void bind(Participantedto participante) {
            // Vincula la información del participante con los elementos de la UI
        }
    }
}
