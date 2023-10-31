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

public class VerUsuario extends AppCompatActivity {

    TextView detailDesc, detailTitle, detailCodigo;
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
        detailDesc = findViewById(R.id.condicion);
        detailTitle = findViewById(R.id.nombreUsuario);
        //detailImage = findViewById(R.id.foto);
        detailCodigo = findViewById(R.id.correo);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            detailDesc.setText(bundle.getString("Desc"));
            detailCodigo.setText(bundle.getString("Image"));
            detailTitle.setText(bundle.getString("Title"));
        }



    }
}