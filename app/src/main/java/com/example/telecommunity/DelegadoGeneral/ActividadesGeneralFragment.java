package com.example.telecommunity.DelegadoGeneral;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.telecommunity.R;


public class ActividadesGeneralFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_actividades_general, container, false);

        ListView listView = view.findViewById(R.id.opcionesActividades);

        String[] actividadesOptions = {"Actividades en Curso", "Actividades Finalizadas", "Actividades Eliminadas"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.list_item_actividad, R.id.tvActividadName, actividadesOptions);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            switch (position) {
                case 0:
                    startActivity(new Intent(getContext(), ActividadesCurso.class));
                    break;
                case 1:
                    startActivity(new Intent(getContext(), ActividadesFinalizadas.class));
                    break;
                case 2:
                    startActivity(new Intent(getContext(), ActividadesEliminadas.class));
                    break;
            }
        });

        Button btnCrearPublicacion = view.findViewById(R.id.btnGuardarPublicacion);
        btnCrearPublicacion.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CrearActividad.class);
            startActivity(intent);
        });

        return view;
    }
}
