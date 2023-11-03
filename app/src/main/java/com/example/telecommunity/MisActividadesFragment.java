package com.example.telecommunity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.telecommunity.adapter.MisActividadesAdapter;
import com.example.telecommunity.entity.ActividadDto;

import java.util.ArrayList;
import java.util.List;


public class MisActividadesFragment extends Fragment {


    private RecyclerView recyclerView;
    private MisActividadesAdapter actividadAdapter;
    private List<ActividadDto> actividadList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mis_actividades, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Inicializar la lista de actividades
        actividadList = new ArrayList<>();

        // Agregar actividades de ejemplo
        actividadList.add(new ActividadDto("1", 101, "Delegado Uno", "Actividad Uno", "Descripción de la actividad uno", "link_de_imagen", "activo"));
        actividadList.add(new ActividadDto("2", 102, "Delegado Dos", "Actividad Dos", "Descripción de la actividad dos", "link_de_imagen", "activo"));
        // ... Añadir más ítems según necesites

        // Inicializar el adaptador con la lista de actividades
        actividadAdapter = new MisActividadesAdapter(getActivity(), actividadList);

        // Configurar el RecyclerView con el adaptador
        recyclerView.setAdapter(actividadAdapter);

        return view;
    }
}