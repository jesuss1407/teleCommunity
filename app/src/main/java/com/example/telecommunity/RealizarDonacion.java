package com.example.telecommunity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RealizarDonacion extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private ImageView imageViewCaptura;
    private Button buttonSubirCaptura;
    private Button buttonEnviarCaptura;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donacion);

        // Inicialización de las vistas
        imageViewCaptura = findViewById(R.id.imageViewCaptura);
        buttonSubirCaptura = findViewById(R.id.buttonSubirCaptura);
        buttonEnviarCaptura = findViewById(R.id.buttonEnviarCaptura);

        // Configurar el botón para subir la captura
        buttonSubirCaptura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE);
            }
        });

        //Navbar

        // Encontrar la BottomNavigationView y setear el item seleccionado
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.navigation_donacion);

// Configurar el Listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.navigation_mi_perfil) {
                    // Ir a la actividad RealizarDonacion
                    startActivity(new Intent(getApplicationContext(), MiPerfil.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                else if (menuItem.getItemId() == R.id.navigation_notificacion) {

                    startActivity(new Intent(getApplicationContext(), Notificaciones.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                // Agrega más casos 'if' para otros items si es necesario
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                imageViewCaptura.setImageBitmap(bitmap);

                // Habilitar el botón para enviar la captura
                buttonEnviarCaptura.setEnabled(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }






}
