package com.example.telecommunity.DelegadoGeneral;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.telecommunity.IniciarSesion;
import com.example.telecommunity.PantallaPrincipal;
import com.example.telecommunity.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

public class VerUsuario extends AppCompatActivity {

    TextView detailCondicion, detailNombre, detailCorreo;
    ImageView detailImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_usuario);


        //ir a editar usuario
        Button iniciar = findViewById(R.id.editarButton);
        iniciar.setOnClickListener(view -> {
            Intent intent = new Intent(VerUsuario.this, EditarUsuario.class);
            startActivity(intent);
        });
        //ir a banear usuario
        Button banear = findViewById(R.id.banearButton);
        banear.setOnClickListener(view -> {
            Intent intent = new Intent(VerUsuario.this, BanearUsuario.class);
            startActivity(intent);
        });

        //mandar info del usuario seleccionado
        detailCondicion = findViewById(R.id.condicion);
        detailNombre = findViewById(R.id.nombreUsuario);
        detailImage = findViewById(R.id.foto);
        detailCorreo = findViewById(R.id.correo);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            detailCondicion.setText(bundle.getString("Condicion"));
            detailCorreo.setText(bundle.getString("Correo"));
            detailNombre.setText(bundle.getString("Nombre"));

            String imageUrl = bundle.getString("Image");
            Picasso.get().load(imageUrl).into(detailImage);
        }



    }
}