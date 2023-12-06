package com.example.telecommunity.DelegadoGeneral;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.telecommunity.R;
import com.example.telecommunity.entity.Donacion;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class EventoDetalleFragment extends Fragment {

    private TextView textViewTituloEvento;
    private TextView textViewFechaEvento;

    public EventoDetalleFragment() {
        // Constructor vacío requerido
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_evento_detalle, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textViewTituloEvento = view.findViewById(R.id.textViewTituloEvento);
        textViewFechaEvento = view.findViewById(R.id.textViewFechaEvento);

        Bundle bundle = getArguments();
        if (bundle != null) {
            Donacion donacion = (Donacion) bundle.getSerializable("donacion");
            if (donacion != null) {
                mostrarDetalleEvento(donacion);
            }
        }
    }

    // Método para mostrar los detalles del evento
    public void mostrarDetalleEvento(Donacion donacion) {
        textViewTituloEvento.setText(donacion.getCodigo());
        Date date = donacion.getTimestamp().toDate();

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String formattedDate = formatter.format(date);
        textViewFechaEvento.setText(formattedDate); // Asegúrate de tener un método getDate() o similar en tu modelo Donacion
        // Establece otros detalles del evento en las vistas correspondientes
    }
}