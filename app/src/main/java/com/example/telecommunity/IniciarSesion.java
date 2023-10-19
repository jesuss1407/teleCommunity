package com.example.telecommunity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.example.telecommunity.databinding.ActivityIniciarSesionBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

//


public class IniciarSesion extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    ActivityIniciarSesionBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);



        //redireccionar a registro
        TextView registrarme = findViewById(R.id.signupText);
        registrarme.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent intent = new Intent(IniciarSesion.this, RegistroUsuario.class);
                startActivity(intent);
            }
        });

        //redireccionar a mi perfil
        TextView perfil = findViewById(R.id.loginText);
        perfil.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent intent = new Intent(IniciarSesion.this,MiPerfil.class);
                startActivity(intent);
            }
        });



        //iniciar sesion

        mAuth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.correo);
        passwordEditText = findViewById(R.id.contrasena);
        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (!email.isEmpty() && !password.isEmpty()) {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(IniciarSesion.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Inicio de sesión exitoso
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Toast.makeText(IniciarSesion.this, "Inicio de sesión exitoso.", Toast.LENGTH_SHORT).show();
                                        // Redirigir a la actividad principal
                                        startActivity(new Intent(IniciarSesion.this, MiPerfil.class));
                                    } else {
                                        Toast.makeText(IniciarSesion.this, "Error en el inicio de sesión.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(IniciarSesion.this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}