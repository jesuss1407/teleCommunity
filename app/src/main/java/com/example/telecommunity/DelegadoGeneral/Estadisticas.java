package com.example.telecommunity.DelegadoGeneral;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.telecommunity.Notificaciones;
import com.example.telecommunity.R;
import com.example.telecommunity.RealizarDonacion;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.charts.PieChart;

import java.util.ArrayList;



public class Estadisticas extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, AdmActividades.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_estadisticas);

        //Navbar

        // Encontrar la BottomNavigationView y setear el item seleccionado
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.navigation_estadistica);

        // Configurar el Listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if (menuItem.getItemId() == R.id. navigation_admdonacion) {
                    // Ir a la actividad Donacion
                    startActivity(new Intent(getApplicationContext(), AdmDonacion.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                else if (menuItem.getItemId() == R.id.navigation_actividades) {
                    // Ir a la actividad AdmActividades
                    startActivity(new Intent(getApplicationContext(), AdmActividades.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                else if (menuItem.getItemId() == R.id.navigation_alumnos) {
                    // Ir a la actividad ver usuario
                    startActivity(new Intent(getApplicationContext(), AdmUsuarios.class));
                    overridePendingTransition(0, 0);
                    return true;
                }

                return false;
            }
        });
        BarChart barChart = findViewById(R.id.barChart);

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
        PieChart pieChart = findViewById(R.id.pieChart);

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

    }


}
