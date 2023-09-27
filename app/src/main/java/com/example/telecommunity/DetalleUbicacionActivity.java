package com.example.telecommunity;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.telecommunity.entity.Evento;

public class DetalleUbicacionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_ubicacion);

        // Recupera el objeto Evento de la actividad anterior
        Evento evento = (Evento) getIntent().getSerializableExtra("evento");

        // Accede a los atributos del objeto Evento
        String titulo = evento.getTitulo();
        String descripcion = evento.getDescripcion();
        String hora = evento.getHora();
        String creador = evento.getCreador();

        // Muestra la información en la interfaz de usuario
        TextView tituloTextView = findViewById(R.id.tituloTextView);
        TextView descripcionTextView = findViewById(R.id.descripcionTextView);
        TextView horaTextView = findViewById(R.id.horaTextView);
        TextView creadorTextView = findViewById(R.id.creadorTextView);

        tituloTextView.setText(titulo);
        descripcionTextView.setText(descripcion);
        horaTextView.setText("Hora: " + hora);
        creadorTextView.setText("Creado por: " + creador);
    }
}
