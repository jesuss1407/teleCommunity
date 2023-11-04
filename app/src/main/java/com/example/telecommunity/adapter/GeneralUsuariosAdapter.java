package com.example.telecommunity.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.telecommunity.DelegadoGeneral.VerUsuario;
import com.example.telecommunity.DetailActivity;
import com.example.telecommunity.R;
import com.example.telecommunity.entity.GeneralActividadesdto;
import com.example.telecommunity.entity.GeneralUsuariosdto;
import com.example.telecommunity.entity.UsuariosDto;

import java.util.List;
public class GeneralUsuariosAdapter extends RecyclerView.Adapter<GeneralUsuariosAdapter.ViewHolder>{


    private List<UsuariosDto> usuariosList;

    private Context context;

    public GeneralUsuariosAdapter(Context context, List<UsuariosDto> usuariosList) {
        this.usuariosList = usuariosList;
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
        UsuariosDto usuario = usuariosList.get(position);
        String nombreCompleto = usuariosList.get(holder.getAdapterPosition()).getNombre() + " " + usuariosList.get(holder.getAdapterPosition()).getApellido();

        holder.tvnombreusuario.setText(nombreCompleto);
        holder.tvcodigo.setText("Código: " + usuario.getCodigo() );
        holder.tvcondicion.setText("Condición: " + usuario.getCondicion() );

        // Cargar la imagen utilizando Glide
        Glide.with(context)
                .load(usuario.getFoto()) // URL de la imagen
                .into(holder.imagenusuario); // imageView donde se mostrará la imagen



        holder.recCard.setOnClickListener(view -> {
            Intent intent = new Intent(context, VerUsuario.class);

            Number userCode = usuariosList.get(holder.getAdapterPosition()).getCodigo();
            String userCodeStr = userCode.toString();
            if (userCode != null) {
                intent.putExtra("UserCode",userCodeStr);
            } else {
                // Manejo de un valor nulo o inválido
                intent.putExtra("UserCode", 0L); // Puedes cambiar 0L por otro valor predeterminado
            }


            intent.putExtra("Image", usuariosList.get(holder.getAdapterPosition()).getFoto());
            intent.putExtra("Nombre", nombreCompleto);
            intent.putExtra("Correo", usuariosList.get(holder.getAdapterPosition()).getCorreo());
            intent.putExtra("Condicion", usuariosList.get(holder.getAdapterPosition()).getCondicion());

            context.startActivity(intent);
        });

    }


    @Override
    public int getItemCount() {
        if (usuariosList != null) {
            return usuariosList.size();
        } else {
            return 0; // Manejo del caso en el que actividadList es nula
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvnombreusuario;
        public TextView tvcodigo;
        public TextView tvcondicion;
        public ImageView imagenusuario;
        CardView recCard;

        public ViewHolder(View itemView) {
            super(itemView);
            tvnombreusuario = itemView.findViewById(R.id.tvnombreusuario);
            imagenusuario = itemView.findViewById(R.id.imagenusuario);
            tvcodigo = itemView.findViewById(R.id.tvcodigo);
            tvcondicion = itemView.findViewById(R.id.tvcondicion);
            recCard = itemView.findViewById(R.id.recCard);
        }
    }
}
