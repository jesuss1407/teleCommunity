package com.example.telecommunity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import android.util.Log;
import android.widget.Toast;

import com.example.telecommunity.databinding.ActivityRegistroUsuarioBinding;
import com.example.telecommunity.entity.UsuariosDto;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import androidx.annotation.NonNull;
public class RegistroUsuario extends AppCompatActivity {
    FirebaseFirestore db;
    ActivityRegistroUsuarioBinding binding;
    ListenerRegistration snapshotListener;

    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);

        binding = ActivityRegistroUsuarioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();


        //
        auth = FirebaseAuth.getInstance();

        //redireccionar a logueo
        TextView iniciarme = findViewById(R.id.loginRedirectText);
        iniciarme.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent intent = new Intent(RegistroUsuario.this, IniciarSesion.class);
                startActivity(intent);
            }
        });

        //registro

        binding.crearBtn.setOnClickListener(view -> {

            String nombre = binding.nombre.getText().toString();
            String apellido = binding.apellido.getText().toString();
            String codigoStr = binding.codigo.getText().toString();
            String correo = binding.correo.getText().toString();
            String contrasena = binding.contrasena.getText().toString();
            String contrasena2 = binding.contrasena2.getText().toString();
            String condicion = binding.condicion.getText().toString();
            String rol = "Usuario";
            String estado = "1";
            String foto = "link";
            if (nombre.isEmpty() || apellido.isEmpty() ||codigoStr.isEmpty() ||correo.isEmpty() ||contrasena.isEmpty() ||contrasena2.isEmpty() ||condicion.isEmpty()){
                Toast.makeText(RegistroUsuario.this, "Llene todos los campos ", Toast.LENGTH_SHORT).show();
            } else {
                if (contrasena.equals(contrasena2)) {

                    //registrarlo con email y contrasena en firebase auth
                    auth.createUserWithEmailAndPassword(correo, contrasena).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            //registro en firestore database
                            UsuariosDto usuario = new UsuariosDto();
                            usuario.setNombre(nombre);
                            usuario.setApellido(apellido);
                            usuario.setCodigo(Integer.parseInt(codigoStr));
                            usuario.setCorreo(correo);
                            usuario.setContrasena(contrasena);
                            usuario.setCondicion(condicion);
                            usuario.setRol(rol);
                            usuario.setEstado(Integer.parseInt(estado));
                            usuario.setFoto(foto);

                            db.collection("usuarios")
                                    .document(codigoStr)
                                    .set(usuario)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegistroUsuario.this, IniciarSesion.class);
                                        startActivity(intent);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Ocurrió un error, intente nuevamente ", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(RegistroUsuario.this, "Registro fallido " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Toast.makeText(RegistroUsuario.this, "Las contraseñas deben ser iguales", Toast.LENGTH_SHORT).show();
                }
            }



        });


    }
}