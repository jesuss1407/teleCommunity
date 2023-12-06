package com.example.telecommunity.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.telecommunity.DelegadoGeneral.EventoDetalleFragment;
import com.example.telecommunity.R;
import com.example.telecommunity.entity.Donacion;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GeneralDonacionAdapter extends RecyclerView.Adapter<GeneralDonacionAdapter.DonacionViewHolder> implements Filterable {

    private Context context;
    private List<Donacion> donacionesList;
    private List<Donacion> donacionesListFull;

    private List<Donacion> donacionesListFullCopy;

    public GeneralDonacionAdapter(Context context, List<Donacion> donacionesList) {
        this.context = context;
        this.donacionesList = donacionesList;
        this.donacionesListFull = new ArrayList<>(donacionesList);
        this.donacionesListFullCopy = new ArrayList<>(donacionesList);
    }

    static class DonacionViewHolder extends RecyclerView.ViewHolder {
        TextView textViewCodigo;
        TextView textViewTimestamp;
        ImageView imageViewDonacion;

        DonacionViewHolder(View itemView) {
            super(itemView);
            textViewCodigo = itemView.findViewById(R.id.textViewCodigo);
            textViewTimestamp = itemView.findViewById(R.id.textViewTimestamp);
            imageViewDonacion = itemView.findViewById(R.id.imageViewDonacion);
        }
    }

    @NonNull
    @Override
    public DonacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donacion, parent, false);
        return new DonacionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DonacionViewHolder holder, int position) {
        Donacion donacion = donacionesList.get(position);

        holder.textViewCodigo.setText(donacion.getCodigo());
        Date date = donacion.getTimestamp().toDate();

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String formattedDate = formatter.format(date);

        holder.textViewTimestamp.setText(formattedDate);


        holder.itemView.setOnClickListener(v -> {
            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            EventoDetalleFragment eventoDetalleFragment = new EventoDetalleFragment();

            Bundle bundle = new Bundle();
            bundle.putSerializable("donacion", (Serializable) donacion);
            eventoDetalleFragment.setArguments(bundle);

            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, eventoDetalleFragment)
                    .addToBackStack(null)
                    .commit();
        });



        Glide.with(context)
                .load(donacion.getUrl())
                .placeholder(R.drawable.blanco)
                .error(R.drawable.blanco)
                .into(holder.imageViewDonacion);
    }

    @Override
    public int getItemCount() {
        return donacionesList.size();
    }

    @Override
    public Filter getFilter() {
        return donacionFilter;
    }

    public void filterByDateRange(List<Donacion> donacionesFiltradas) {
        Log.d("Adapter", "Número de donaciones recibidas para filtrar: " + donacionesFiltradas.size());

        donacionesList.clear();
        donacionesList.addAll(donacionesFiltradas);
        notifyDataSetChanged();
    }


    public List<Donacion> getDonacionesListFull() {
        return donacionesListFullCopy;
    }

    private Filter donacionFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Donacion> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(donacionesListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Donacion donacion : donacionesListFull) {
                    if (donacion.getCodigo().toLowerCase().contains(filterPattern)) {
                        filteredList.add(donacion);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            donacionesList.clear();
            donacionesList.addAll((List) results.values);
            notifyDataSetChanged();
        }



    };
}



