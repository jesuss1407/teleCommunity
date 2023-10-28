package com.example.telecommunity.adapter;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.telecommunity.DelegadoGeneral.VerActividad;
import com.example.telecommunity.DelegadoGeneral.VerUsuario;
import com.example.telecommunity.R;
import com.example.telecommunity.entity.ActividadDto;
import com.example.telecommunity.entity.GeneralActividadesdto;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class GeneralActividadesadapter extends RecyclerView.Adapter<GeneralActividadesadapter.ViewHolder> {

    private List<ActividadDto> actividadList;
    private Context context;

    public GeneralActividadesadapter(Context context, List<ActividadDto> actividadList) {
        this.actividadList = actividadList;

        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_actividad, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ActividadDto actividad = actividadList.get(position);
        holder.tvnombreactividad.setText(actividad.getNombre());
        //holder.tvnumeventos.setText("Evento(s): " + actividad.getNumeventos() );
        holder.tvdelegado.setText(actividad.getDelegadoName() );

        // Cargar la imagen utilizando Glide
        Glide.with(context)
                .load(actividad.getFotoLink()) // URL de la imagen
                .into(holder.imagenactividad); // imageView donde se mostrará la imagen





        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, VerActividad.class);
                intent.putExtra("Image", actividadList.get(holder.getAdapterPosition()).getFotoLink());
                intent.putExtra("Title", actividadList.get(holder.getAdapterPosition()).getNombre());
                //intent.putExtra("Numeventos", actividadList.get(holder.getAdapterPosition()).getNumeventos());
                intent.putExtra("Delegado", actividadList.get(holder.getAdapterPosition()).getDelegadoCode());
                intent.putExtra("Descripcion", actividadList.get(holder.getAdapterPosition()).getDescripcion());
                intent.putExtra("Id", actividadList.get(holder.getAdapterPosition()).getId());
                context.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        if (actividadList != null) {
            return actividadList.size();
        } else {
            return 0; // Manejo del caso en el que actividadList es nula
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvnombreactividad;
        //public TextView tvnumeventos;
        public TextView tvdelegado;

        public ImageView imagenactividad;
        CardView recCard;

        public ViewHolder(View itemView) {
            super(itemView);
            imagenactividad = itemView.findViewById(R.id.imagenactividad);
            tvnombreactividad = itemView.findViewById(R.id.tvnombreactividad);
            //tvnumeventos = itemView.findViewById(R.id.tvnumeventos);
            tvdelegado = itemView.findViewById(R.id.tvdelegado);
            recCard = itemView.findViewById(R.id.recCard);
        }
    }

}
