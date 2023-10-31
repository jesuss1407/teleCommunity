package com.example.telecommunity.DelegadoGeneral;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.telecommunity.R;
import com.example.telecommunity.adapter.GeneralUsuariosAdapter;
import com.example.telecommunity.entity.GeneralUsuariosdto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlumnosActivosFragment extends Fragment {

    private RecyclerView recyclerView;
    private GeneralUsuariosAdapter adapter;

    public AlumnosActivosFragment() {
        // Constructor público vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout para este fragmento
        View view = inflater.inflate(R.layout.fragment_alumnos_activos, container, false);

        // Hardcoded data
        List<GeneralUsuariosdto> actividadList = Arrays.asList(
                new GeneralUsuariosdto(20171234, "Maria Perez Gonzalez", "Estudiante", 1, "some_link"),
                new GeneralUsuariosdto(20219876, "Juan Carlos Rodriguez", "Egresado", 2, "some_link"),
                new GeneralUsuariosdto(20175678, "Ana Isabel Torres", "Estudiante", 2, "some_link"),
                new GeneralUsuariosdto(20214321, "Carlos Lopez Diaz", "Egresado", 2, "some_link"),
                new GeneralUsuariosdto(20188901, "Sofia Ramirez", "Estudiante", 2, "some_link"),
                new GeneralUsuariosdto(20179012, "Diego Santos", "Estudiante", 1, "some_link"),
                new GeneralUsuariosdto(20220123, "Laura Alvarado Reyes", "Egresado", 3, "some_link"),
                new GeneralUsuariosdto(20182345, "Pedro Vargas", "Estudiante", 2, "some_link"),
                new GeneralUsuariosdto(20193456, "Isabel Marquez Gutierrez", "Egresado", 2, "some_link"),
                new GeneralUsuariosdto(20223344, "Jose Antonio Castro", "Estudiante", 1, "some_link"),
                new GeneralUsuariosdto(20173211, "Carmen Morales", "Egresado", 3, "some_link"),
                new GeneralUsuariosdto(20186788, "Miguel Rios", "Estudiante", 2, "some_link"),
                new GeneralUsuariosdto(20207766, "Sara Mendoza", "Egresado", 2, "some_link"),
                new GeneralUsuariosdto(20178899, "Pablo Molina", "Egresado", 3, "some_link"),
                new GeneralUsuariosdto(20220111, "Daniela Ortiz", "Egresado", 3, "some_link"),
                new GeneralUsuariosdto(20181133, "Eduardo Gutierrez", "Egresado", 3, "some_link"),
                new GeneralUsuariosdto(20195566, "Clara Rodriguez", "Estudiante", 2, "some_link"),
                new GeneralUsuariosdto(20216655, "Ricardo Perez", "Egresado", 3, "some_link"),
                new GeneralUsuariosdto(20192233, "Luisa Hernandez", "Estudiante", 1, "some_link"),
                new GeneralUsuariosdto(20229988, "Hernan Suarez", "Estudiante", 1, "some_link"),
                new GeneralUsuariosdto(20174455, "Victoria Alvarado", "Estudiante", 2, "some_link"),
                new GeneralUsuariosdto(20215544, "Oscar Luna", "Egresado", 2, "some_link"),
                new GeneralUsuariosdto(20182211, "Lucia Vargas", "Estudiante", 2, "some_link"),
                new GeneralUsuariosdto(20203322, "Julia Castillo", "Estudiante", 2, "some_link"),
                new GeneralUsuariosdto(20178877, "Fernando Ruiz", "Egresado", 2, "some_link"),
                new GeneralUsuariosdto(20215544, "Manuel Torres", "Estudiante", 1, "some_link"),
                new GeneralUsuariosdto(20177666, "Cristina Serrano", "Estudiante", 1, "some_link"),
                new GeneralUsuariosdto(20174333, "Antonio Miguel Garcia", "Estudiante", 1, "some_link"),
                new GeneralUsuariosdto(20215566, "Andrea Herrera", "Egresado", 3, "some_link"),
                new GeneralUsuariosdto(20194433, "Gabriel Lopez Alvarez", "Estudiante", 2, "some_link")
        );

        List<GeneralUsuariosdto> filteredList = new ArrayList<>();
        for (GeneralUsuariosdto state : actividadList) {
            if (state.getEstado() == 1) {
                filteredList.add(state);
            }
        }

        // Pasar la lista filtrada al adaptador
        recyclerView = view.findViewById(R.id.listarActividades);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GeneralUsuariosAdapter(getContext(), filteredList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}