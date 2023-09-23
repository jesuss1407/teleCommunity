package com.example.telecommunity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.telecommunity.R;
import com.example.telecommunity.entity.GeneralActividadesdto;

import java.util.List;

public class GeneralActividadesadapter extends RecyclerView.Adapter<GeneralActividadesadapter.ViewHolder> {

    private List<GeneralActividadesdto> actividadList;

    public GeneralActividadesadapter(List<GeneralActividadesdto> actividadList) {
        this.actividadList = actividadList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_actividad, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GeneralActividadesdto actividad = actividadList.get(position);
        holder.tvnombreactividad.setText(actividad.getName());
        holder.tvnumeventos.setText("Evento(s): " + actividad.getNumeventos() );
        holder.tvnumapoyos.setText("Apoyo(s): " + actividad.getNumapoyos() );

    }


    @Override
    public int getItemCount() {
        return actividadList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvnombreactividad;
        public TextView tvnumeventos;
        public TextView tvnumapoyos;

        public ViewHolder(View itemView) {
            super(itemView);
            tvnombreactividad = itemView.findViewById(R.id.tvnombreactividad);
            tvnumeventos = itemView.findViewById(R.id.tvnumeventos);
            tvnumapoyos = itemView.findViewById(R.id.tvnumapoyos);
        }
    }

}
