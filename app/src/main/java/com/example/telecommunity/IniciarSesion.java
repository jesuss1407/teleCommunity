package com.example.telecommunity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class IniciarSesion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);


        //iniciar
        Button iniciar = findViewById(R.id.loginButton);
        iniciar.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent intent = new Intent(IniciarSesion.this, PantallaPrincipal.class);
                startActivity(intent);
            }
        });

        //redireccionar a registro
        TextView registrarme = findViewById(R.id.signupText);
        registrarme.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent intent = new Intent(IniciarSesion.this, RegistroUsuario.class);
                startActivity(intent);
            }
        });

    }
}