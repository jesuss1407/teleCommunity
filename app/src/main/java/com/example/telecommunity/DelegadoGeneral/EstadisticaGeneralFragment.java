package com.example.telecommunity.DelegadoGeneral;

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
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;


public class EstadisticaGeneralFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_estadistica_general, container, false);

        BarChart barChart = view.findViewById(R.id.barChart);

        // Crea un conjunto de datos de barras y agrega datos de ejemplo
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(1, 20f)); // Valor 20 en la posición 1
        entries.add(new BarEntry(2, 35f)); // Valor 35 en la posición 2
        entries.add(new BarEntry(3, 15f)); // Valor 15 en la posición 3
        entries.add(new BarEntry(4, 50f)); // Valor 50 en la posición 4

        BarDataSet dataSet = new BarDataSet(entries, "Datos de Ejemplo");

        // Personaliza la apariencia del conjunto de datos (color, borde, etc.)
        dataSet.setColor(getResources().getColor(R.color.colorAccent)); // Color de las barras

        /// Configura el espacio entre las barras
        dataSet.setDrawValues(false); // Para ocultar los valores en las barras

        // Crea un objeto BarData y establece el conjunto de datos
        BarData barData = new BarData(dataSet);

        // Personaliza el eje X (etiquetas, posición de la barra, etc.)
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Etiquetas en la parte inferior
        xAxis.setGranularity(1f); // Espacio entre las etiquetas

        // Personaliza el eje Y
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setGranularity(1f); // Espacio entre las divisiones en el eje Y

        // Asigna los datos al gráfico
        barChart.setData(barData);

        // Actualiza el gráfico
        barChart.invalidate();

        // Encuentra el PieChart en tu XML
        PieChart pieChart = view.findViewById(R.id.pieChart);

        // Crea un conjunto de datos para el gráfico de pastel y agrega datos de ejemplo
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(30f, "Etiqueta 1"));
        pieEntries.add(new PieEntry(45f, "Etiqueta 2"));
        pieEntries.add(new PieEntry(25f, "Etiqueta 3"));

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Datos de Ejemplo");

        // Personaliza la apariencia del conjunto de datos (colores, borde, etc.)
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS); // Colores predeterminados

        // Crea un objeto PieData y establece el conjunto de datos
        PieData pieData = new PieData(pieDataSet);

        // Asigna los datos al gráfico de pastel
        pieChart.setData(pieData);

        // Personaliza el centro del gráfico de pastel (opcional)
        pieChart.setCenterText("Estadísticas");
        pieChart.setCenterTextSize(18f);

        // Actualiza el gráfico de pastel
        pieChart.invalidate();

        return view;
    }
}