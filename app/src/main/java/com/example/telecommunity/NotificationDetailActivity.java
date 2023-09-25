package com.example.telecommunity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class NotificationDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_detail);

        // Obtener datos de la notificación seleccionada (puedes usar Intent para pasar datos)
        // Por ejemplo, puedes pasar datos a través de Intent extras cuando inicies esta actividad.

        // Inicializar vistas de la actividad de detalle
        TextView detailTitle = findViewById(R.id.detailTitle);
        ImageView detailImage = findViewById(R.id.detailImage);
        TextView detailDesc = findViewById(R.id.detailDesc);

        // Rellenar vistas con los datos de la notificación
        String title = getIntent().getStringExtra("title");
        String desc = getIntent().getStringExtra("desc");
        int imageResource = getIntent().getIntExtra("imageResource", R.drawable.basquet);

        detailTitle.setText(title);
        detailDesc.setText(desc);
        detailImage.setImageResource(imageResource);
    }
}