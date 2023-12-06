package com.example.telecommunity.DelegadoGeneral;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.telecommunity.R;
import com.example.telecommunity.adapter.GeneralDonacionAdapter;
import com.example.telecommunity.entity.Donacion;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class DonacionesGeneralFragment extends Fragment {

    private RecyclerView recyclerView;
    private GeneralDonacionAdapter adapter;
    private FirebaseFirestore db;
    private SearchView searchView;

    private Button selectDateButton;
    private TextView dateRangeTextView;
    private Calendar startDateCalendar = Calendar.getInstance();
    private Calendar endDateCalendar = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_donaciones_general, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewDonaciones);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        db = FirebaseFirestore.getInstance();
        searchView = view.findViewById(R.id.searchViewDonaciones);

        selectDateButton = view.findViewById(R.id.selectDateButton);
        dateRangeTextView = view.findViewById(R.id.dateRangeTextView);

        setupDatePickers();
        obtenerDonacionesDesdeFirestore();
        setupSearchView();
    }

    private void setupDatePickers() {
        startDateCalendar.clear();
        endDateCalendar.clear();

        startDateCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startDateCalendar.set(Calendar.YEAR, 2023);
        endDateCalendar.set(Calendar.YEAR, 2023);

        DatePickerDialog.OnDateSetListener endDateSetListener = (view, year, month, dayOfMonth) -> {
            endDateCalendar.set(year, month, dayOfMonth);
            endDateCalendar.set(Calendar.HOUR_OF_DAY, 23);
            if (endDateCalendar.isSet(Calendar.HOUR_OF_DAY)) {
                filtrarPorFecha();
            }
            updateDateRangeTextView();

        };
        DatePickerDialog.OnDateSetListener startDateSetListener = (view, year, month, dayOfMonth) -> {
            startDateCalendar.set(year, month, dayOfMonth);
            updateDateRangeTextView();
            if (endDateCalendar.isSet(Calendar.HOUR_OF_DAY)) {
                filtrarPorFecha();
            }
        };

        selectDateButton.setOnClickListener(v -> {
            showDatePickerDialog(endDateSetListener, endDateCalendar);
            showDatePickerDialog(startDateSetListener, startDateCalendar);

        });
    }

    private void showDatePickerDialog(DatePickerDialog.OnDateSetListener dateSetListener, Calendar calendar) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    private void updateDateRangeTextView() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String fechaInicio = dateFormat.format(startDateCalendar.getTime());
        String fechaFin = dateFormat.format(endDateCalendar.getTime());

        String fechaSeleccionada = "Rango de fechas seleccionado: " + fechaInicio + " - " + fechaFin;
        dateRangeTextView.setText(fechaSeleccionada);
        dateRangeTextView.setVisibility(View.VISIBLE);
    }

    private void filtrarPorFecha() {
        long startDate = startDateCalendar.getTimeInMillis();
        long endDate = endDateCalendar.getTimeInMillis();

        List<Donacion> donacionesFiltradas = new ArrayList<>();

        for (Donacion donacion : adapter.getDonacionesListFull()) {
            long timestamp = donacion.getTimestamp().toDate().getTime();
            if (timestamp >= startDate && timestamp <= endDate) {
                donacionesFiltradas.add(donacion);
            }
        }

        Log.d("Filtrado", "Número de donaciones filtradas: " + donacionesFiltradas.size());

        adapter.filterByDateRange(donacionesFiltradas);
    }





    private long obtenerFechaSeleccionada(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTimeInMillis();
    }

    private void obtenerDonacionesDesdeFirestore() {
        db.collection("donacion")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Donacion> donacionesList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Donacion donacion = document.toObject(Donacion.class);
                            donacionesList.add(donacion);
                        }

                        adapter = new GeneralDonacionAdapter(requireContext(), donacionesList);
                        recyclerView.setAdapter(adapter);


                    } else {

                    }
                });
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }
}


