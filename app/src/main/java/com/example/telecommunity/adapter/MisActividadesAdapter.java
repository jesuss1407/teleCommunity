package com.example.telecommunity.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.telecommunity.R;
import com.example.telecommunity.entity.ActividadDto;
import java.util.List;

public class MisActividadesAdapter extends RecyclerView.Adapter<MisActividadesAdapter.ViewHolder> {

    private List<ActividadDto> actividadList;
    private Context context;

    // Constructor
    public MisActividadesAdapter(Context context, List<ActividadDto> actividadList) {
        this.context = context;
        this.actividadList = actividadList;
    }

    // Crear nuevos views (invocado por el layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_actividad, parent, false);
        return new ViewHolder(itemView);
    }

    // Reemplazar el contenido de un view (invocado por el layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ActividadDto actividad = actividadList.get(position);
        holder.tvNombreActividad.setText(actividad.getNombre());
        holder.tvDelegado.setText("Delegado: " + actividad.getDelegadoName());
        holder.tvDescripcion.setText(actividad.getDescripcion());

        Glide.with(context)
                .load(actividad.getFotoLink()) // URL de la imagen
                .placeholder(R.drawable.regalito) // Una imagen de placeholder
                .error(R.drawable.regalito) // Una imagen de error
                .into(holder.imagenActividad); // ImageView donde se quiere cargar la imagen

        // Aquí deberías cargar la imagen usando la librería que prefieras, como Glide o Picasso
        // Por ejemplo, si usas Glide:
        // Glide.with(context).load(actividad.getFotoLink()).into(holder.imagenActividad);

        // Si no usas imágenes dinámicas, puedes omitir esta parte o poner una imagen por defecto.
    }

    // Devuelve el tamaño de tu dataset (invocado por el layout manager)
    @Override
    public int getItemCount() {
        return actividadList.size();
    }

    // Proporcionar una referencia a las vistas para cada ítem de datos
    // Los ítems de datos complejos pueden necesitar más de una vista por ítem, y
    // proporcionas acceso a todas las vistas para un ítem de datos en un holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imagenActividad, ivChevronRight;
        public TextView tvNombreActividad, tvDelegado, tvDescripcion;

        public ViewHolder(View view) {
            super(view);
            imagenActividad = view.findViewById(R.id.imagenactividad);
            ivChevronRight = view.findViewById(R.id.ivChevronRight);
            tvNombreActividad = view.findViewById(R.id.tvnombreactividad);
            tvDelegado = view.findViewById(R.id.tvdelegado);
            tvDescripcion = view.findViewById(R.id.tvdelegado0); // Asegúrate de que este ID corresponde al de la descripción
        }
    }
}
