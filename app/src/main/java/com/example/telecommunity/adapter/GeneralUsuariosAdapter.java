package com.example.telecommunity.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.telecommunity.DelegadoGeneral.VerUsuario;
import com.example.telecommunity.DetailActivity;
import com.example.telecommunity.R;
import com.example.telecommunity.entity.GeneralActividadesdto;
import com.example.telecommunity.entity.GeneralUsuariosdto;

import java.util.List;
public class GeneralUsuariosAdapter extends RecyclerView.Adapter<GeneralUsuariosAdapter.ViewHolder>{


    private List<GeneralUsuariosdto> actividadList;

    private Context context;

    public GeneralUsuariosAdapter(Context context, List<GeneralUsuariosdto> actividadList) {
        this.actividadList = actividadList;
        this.context = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GeneralUsuariosdto actividad = actividadList.get(position);
        holder.tvnombreusuario.setText(actividad.getNombre());
        holder.tvcodigo.setText("Código: " + actividad.getCodigo() );
        holder.tvcondicion.setText("Condición: " + actividad.getCondicion() );

        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, VerUsuario.class);
                intent.putExtra("Image", actividadList.get(holder.getAdapterPosition()).getLink());
                intent.putExtra("Title", actividadList.get(holder.getAdapterPosition()).getNombre());
                intent.putExtra("Desc", actividadList.get(holder.getAdapterPosition()).getCondicion());

                context.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return actividadList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvnombreusuario;
        public TextView tvcodigo;
        public TextView tvcondicion;
        CardView recCard;

        public ViewHolder(View itemView) {
            super(itemView);
            tvnombreusuario = itemView.findViewById(R.id.tvnombreusuario);
            tvcodigo = itemView.findViewById(R.id.tvcodigo);
            tvcondicion = itemView.findViewById(R.id.tvcondicion);
            recCard = itemView.findViewById(R.id.recCard);
        }
    }
}
