package com.example.telecommunity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.telecommunity.adapter.PublicacionAdapter;
import com.example.telecommunity.entity.Publicaciondto;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PublicacionAdapter publicacionAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar el layout para este fragmento
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Publicaciondto> publicacionList = new ArrayList<>();
        publicacionList.add(new Publicaciondto(R.drawable.gato, "Marcelo Rojas", "Basquet Damas","25/09/23","13:30",2,"Pabellón V","Únanse a basquet damas, pronto empezaremos con las prácticas en la cancha de minas, nos falta barra tambien!!!",0));
        publicacionList.add(new Publicaciondto(R.drawable.gato, "Marcelo Rojas", "Futsal Damas","02/09/23","08:27",3,"Pabellón V","En futsal damas ya tenemos el equipo formado, pero igual estamos buscando practicar las barras, los esperamos",R.drawable.futsal_damas));
        publicacionList.add(new Publicaciondto(R.drawable.gato, "Marcelo Rojas", "Ajedrez","18/08/23","19:55",4,"Pabellón V","No hay ni un solo teleco que juegue ajedrez, por favor, necesitamos al menos a una persona que vaya camotito",0));

        publicacionAdapter = new PublicacionAdapter(publicacionList, getContext());
        recyclerView.setAdapter(publicacionAdapter);

        return view;
    }
}