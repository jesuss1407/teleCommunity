package com.example.telecommunity.DelegadoGeneral;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.telecommunity.R;


public class AlumnosGeneralFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alumnos_general, container, false);

        ListView listView = view.findViewById(R.id.opcionesActividades);

        String[] actividadesOptions = {"Alumnos activos", "Alumnos baneados", "Solicitudes de activacion"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.list_item_actividad, R.id.tvActividadName, actividadesOptions);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new AlumnosActivosFragment();
                    break;
                case 1:
                    fragment = new AlumnosBaneadosFragment();
                    break;
                case 2:
                    fragment = new AlumnosSolicitudActivacionFragment();
                    break;
            }
            if (fragment != null) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });



        return view;
    }
}