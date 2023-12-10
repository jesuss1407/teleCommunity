package com.example.telecommunity.DelegadoGeneral;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.telecommunity.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;

public class EstadisticaGeneralFragment extends Fragment {

    private PieChart pieChart;
    private BarChart barChart;
    private HorizontalBarChart topDonorsChart;
    private FirebaseFirestore db;

    private int conteoActivos = 0;
    private int conteoBaneados = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estadistica_general, container, false);

        pieChart = view.findViewById(R.id.pieChart);
        barChart = view.findViewById(R.id.barChart);
        topDonorsChart = view.findViewById(R.id.topDonorsChart); // Asegúrate de tener este ID en tu layout XML
        db = FirebaseFirestore.getInstance();

        obtenerDatosAlumnos();
        obtenerDatosDonaciones();
        obtenerTopDonantes();

        return view;
    }


    private void obtenerDatosAlumnos() {
        db.collection("usuarios").whereEqualTo("estado", "activo").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                conteoActivos = task.getResult().size();
                db.collection("usuarios").whereEqualTo("estado", "baneado").get().addOnCompleteListener(taskBaneados -> {
                    if (taskBaneados.isSuccessful()) {
                        conteoBaneados = taskBaneados.getResult().size();
                        actualizarGrafico();
                    }
                });
            }
        });
    }

    private void obtenerDatosDonaciones() {
        db.collection("donacionconf").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Define el mapa para contar las donaciones por día
                Map<String, Integer> conteoDonacionesPorDia = new HashMap<>();

                // Formato de fecha para mostrar solo día y mes
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM", Locale.getDefault());

                // Calcula la fecha de 6 meses atrás
                Calendar sixMonthsAgo = Calendar.getInstance();
                sixMonthsAgo.add(Calendar.MONTH, -6);

                for (QueryDocumentSnapshot document : task.getResult()) {
                    Timestamp timestamp = document.getTimestamp("fecha");
                    if (timestamp != null) {
                        Date fechaDonacion = timestamp.toDate();
                        // Solo incluye la donación si es posterior a 6 meses atrás
                        if (fechaDonacion.after(sixMonthsAgo.getTime())) {
                            String fechaFormateada = sdf.format(fechaDonacion);
                            conteoDonacionesPorDia.put(fechaFormateada, conteoDonacionesPorDia.getOrDefault(fechaFormateada, 0) + 1);
                        }
                    }
                }

                // Prepara los datos para el gráfico de barras
                ArrayList<BarEntry> barEntries = new ArrayList<>();
                ArrayList<String> barEntryLabels = new ArrayList<>();
                int index = 0;
                for (Map.Entry<String, Integer> entry : conteoDonacionesPorDia.entrySet()) {
                    barEntries.add(new BarEntry(index, entry.getValue()));
                    barEntryLabels.add(entry.getKey());
                    index++;
                }

                BarDataSet barDataSet = new BarDataSet(barEntries, "");
                BarData barData = new BarData(barDataSet);
                barChart.setData(barData);

                // Configura el eje X para usar las etiquetas de día y mes
                XAxis xAxis = barChart.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(barEntryLabels));
                xAxis.setGranularity(1f);
                xAxis.setGranularityEnabled(true);
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setLabelRotationAngle(45); // Si las etiquetas se solapan, rota un poco

                barChart.getDescription().setEnabled(false);
                barChart.animateY(1000);
                barChart.invalidate();
            } else {
                // Manejo de errores
            }
        });
    }
    private void obtenerTopDonantes() {
        db.collection("donacionconf").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Map<String, Integer> donacionesPorCodigo = new HashMap<>();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    String codigo = document.getString("codigo");
                    donacionesPorCodigo.put(codigo, donacionesPorCodigo.getOrDefault(codigo, 0) + 1);
                }

                // Ordena y toma los top 5 donantes
                Map<String, Integer> topDonantes = donacionesPorCodigo.entrySet().stream()
                        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                        .limit(5)
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (e1, e2) -> e1,
                                LinkedHashMap::new));

                ArrayList<BarEntry> entries = new ArrayList<>();
                ArrayList<String> codes = new ArrayList<>();
                int index = 0;
                for (Map.Entry<String, Integer> entry : topDonantes.entrySet()) {
                    entries.add(new BarEntry(index, entry.getValue()));
                    codes.add(entry.getKey());
                    index++;
                }

                BarDataSet dataSet = new BarDataSet(entries, "Top 5 Donantes");
                dataSet.setColors(ColorTemplate.LIBERTY_COLORS);
                BarData data = new BarData(dataSet);

                topDonorsChart.setData(data);
                XAxis xAxis = topDonorsChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setValueFormatter(new IndexAxisValueFormatter(codes));
                xAxis.setLabelRotationAngle(45);
                xAxis.setDrawGridLines(false);

                topDonorsChart.getDescription().setEnabled(false);
                topDonorsChart.getAxisLeft().setDrawLabels(false);
                topDonorsChart.getAxisRight().setDrawLabels(false);
                topDonorsChart.getLegend().setEnabled(false);
                topDonorsChart.animateY(1000);
                topDonorsChart.invalidate();
            } else {
                // Manejo de errores
            }
        });
    }



    private void actualizarGrafico() {
        int totalAlumnos = conteoActivos + conteoBaneados;
        float porcentajeActivos = totalAlumnos > 0 ? ((float) conteoActivos / totalAlumnos) * 100 : 0;
        float porcentajeBaneados = totalAlumnos > 0 ? ((float) conteoBaneados / totalAlumnos) * 100 : 0;

        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(porcentajeActivos, "Activos"));
        pieEntries.add(new PieEntry(porcentajeBaneados, "Baneados"));

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");

        // Asigna colores personalizados
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.BLUE); // Azul para alumnos activos
        colors.add(Color.RED);  // Rojo para alumnos baneados
        pieDataSet.setColors(colors);

        pieDataSet.setValueTextSize(12f);
        pieDataSet.setValueFormatter(new PercentFormatter());

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }


    public class PercentFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            return String.format("%.1f%%", value);
        }
    }


}
