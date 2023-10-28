package com.example.telecommunity.DelegadoGeneral;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.telecommunity.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

public class VerActividad extends AppCompatActivity {

    TextView detailApoyos, detailTitle, detailEventos,detailDescripcion,detailDelegado;
    ImageView detailImagen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_actividad);


        //mandar info del usuario seleccionado
        //detailEventos= findViewById(R.id.numeventos);
        detailTitle = findViewById(R.id.nombre);
        detailDescripcion = findViewById(R.id.recDesc);
        detailDelegado = findViewById(R.id.dele);
        detailImagen = findViewById(R.id.recImage);
        //detailApoyos = findViewById(R.id.numapoyos);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            String imageUrl = bundle.getString("Image");
            Picasso.get().load(imageUrl).into(detailImagen);
            //detailEventos.setText(bundle.getString("Numeventos"));
            //detailApoyos.setText(bundle.getString("Numapoyos"));
            detailTitle.setText(bundle.getString("Title"));
            detailDescripcion.setText(bundle.getString("Descripcion"));
            detailDelegado.setText(bundle.getString("Delegado")+ " - "+bundle.getString("DelegadoCode") );

        }

        //Navbar

        // Encontrar la BottomNavigationView y setear el item seleccionado
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.navigation_actividades);

        // Configurar el Listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if (menuItem.getItemId() == R.id.navigation_estadistica) {
                    // Ir a la actividad Estadisticas
                    startActivity(new Intent(getApplicationContext(), Estadisticas.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                else if (menuItem.getItemId() == R.id.navigation_admdonacion) {
                    // Ir a la actividad AdministrarDonaión
                    startActivity(new Intent(getApplicationContext(), AdmDonacion.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                else if (menuItem.getItemId() == R.id.navigation_alumnos) {
                    // Ir a la actividad AdministrarDonaión
                    startActivity(new Intent(getApplicationContext(), AdmUsuarios.class));
                    overridePendingTransition(0, 0);
                    return true;
                }

                return false;
            }
        });

    }
}