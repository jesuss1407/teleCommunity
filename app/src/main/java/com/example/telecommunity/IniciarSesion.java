package com.example.telecommunity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.example.telecommunity.DelegadoGeneral.AdmActividades;
import com.example.telecommunity.DelegadoGeneral.BaseGeneralActivity;
import com.example.telecommunity.databinding.ActivityIniciarSesionBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

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

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Define la referencia al documento que deseas leer
        DocumentReference docRef = db.collection("usuarios").document("id_del_documento");


        //redireccionar a registro
        TextView registrarme = findViewById(R.id.signupText);
        registrarme.setOnClickListener(view -> {
            Intent intent = new Intent(IniciarSesion.this, RegistroUsuario.class);
            startActivity(intent);
        });

        //redireccionar a mi perfil
        TextView perfil = findViewById(R.id.loginText);
        perfil.setOnClickListener(view -> {
            Intent intent = new Intent(IniciarSesion.this,BaseActivity.class);
            startActivity(intent);
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
                    // ...

                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(IniciarSesion.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Inicio de sesión exitoso
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        //redirigir a la vista segun rol
                                        String email = user.getEmail();
                                        Query query = db.collection("usuarios").whereEqualTo("correo", email);
                                        query.get().addOnCompleteListener(task2 -> {
                                            if (task2.isSuccessful()) {
                                                QuerySnapshot querySnapshot = task2.getResult();
                                                if (!querySnapshot.isEmpty()) {
                                                    // El documento existe, puedes obtener los datos
                                                    DocumentSnapshot document = querySnapshot.getDocuments().get(0);

                                                    // Accede a los datos del documento
                                                    Map<String, Object> data = document.getData();
                                                    // Ahora puedes acceder a los campos de datos del documento
                                                    String rol = (String) data.get("rol");

                                                    // Guarda el tipo de usuario en las preferencias compartidas
                                                    SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    editor.putString("tipoUsuario", rol);
                                                    editor.apply();

                                                    // ...

                                                    //int estado = document.getLong("estado").intValue();
                                                    String estadoStr = (String) data.get("estado");
                                                    if(estadoStr.equals("activo")){
                                                        if("Delegado general".equals(rol)){
                                                            startActivity(new Intent(IniciarSesion.this, BaseGeneralActivity.class));
                                                        } else if ("Delegado de actividad".equals(rol)) {
                                                            startActivity(new Intent(IniciarSesion.this, BaseActivity.class));
                                                            Toast.makeText(IniciarSesion.this, "Inicio de sesión exitoso.", Toast.LENGTH_SHORT).show();

                                                        } else {
                                                            startActivity(new Intent(IniciarSesion.this, BaseActivity.class));
                                                            Toast.makeText(IniciarSesion.this, "Inicio de sesión exitoso.", Toast.LENGTH_SHORT).show();

                                                        }
                                                    } else {
                                                        Toast.makeText(IniciarSesion.this, "La cuenta no se encuentra habilitada, comuníquese con el administrador.", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    // El documento no existe
                                                    // Puedes manejar este caso
                                                }
                                            } else {
                                                // Hubo un error al obtener el documento
                                                FirebaseFirestoreException exception = (FirebaseFirestoreException) task.getException();
                                                if (exception != null) {
                                                    // Maneja el error
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(IniciarSesion.this, "Error en el inicio de sesión.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

// ...

                } else {
                    Toast.makeText(IniciarSesion.this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }




}