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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.telecommunity.DelegadoGeneral.AdmActividades;
import com.example.telecommunity.DelegadoGeneral.BaseGeneralActivity;
import com.example.telecommunity.adapter.GeneralUsuariosAdapter;
import com.example.telecommunity.databinding.ActivityIniciarSesionBinding;
import com.example.telecommunity.entity.UsuariosDto;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;




//forgot
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
//import com.developer.gbuttons.GoogleSignInButton;
//import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

//

public class IniciarSesion extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ActivityIniciarSesionBinding binding;
    private FirebaseFirestore db;
    TextView forgotPassword;
    //forgot pass
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIniciarSesionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        forgotPassword = findViewById(R.id.forgot_password);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            redirectUserBasedOnRole(user.getEmail());
        } else {
            setupLoginButton();
            setupSignUpText();
        }



        forgotPassword.setOnClickListener(v -> {
            showForgotPasswordDialog();
        });

    }

    private void setupLoginButton() {
        binding.loginButton.setOnClickListener(v -> {
            String email = binding.correo.getText().toString();
            String password = binding.contrasena.getText().toString();
            if (!email.isEmpty() && !password.isEmpty()) {
                performLogin(email, password);
            } else {
                Toast.makeText(IniciarSesion.this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        redirectUserBasedOnRole(email);
                    } else {
                        Toast.makeText(IniciarSesion.this, "Error en el inicio de sesión.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void redirectUserBasedOnRole(String email) {
        Query query = db.collection("usuarios").whereEqualTo("correo", email);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (!querySnapshot.isEmpty()) {
                    // El documento existe, puedes obtener los datos
                    DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                    Map<String, Object> data = document.getData();
                    String rol = (String) data.get("rol");
                    String estadoStr = (String) data.get("estado");

                    SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("tipoUsuario", rol);
                    editor.apply();

                    if("activo".equals(estadoStr)){
                        switch (rol) {
                            case "Delegado general":
                                startActivity(new Intent(IniciarSesion.this, BaseGeneralActivity.class));
                                break;
                            case "Delegado de actividad":
                                // Presumiendo que BaseActivity es para "Delegado de actividad"
                                startActivity(new Intent(IniciarSesion.this, BaseActivity.class));
                                break;
                            default:
                                // Para otros roles o si no se especifica
                                startActivity(new Intent(IniciarSesion.this, BaseActivity.class));
                                break;
                        }
                        Toast.makeText(IniciarSesion.this, "Inicio de sesión exitoso.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(IniciarSesion.this, "La cuenta no se encuentra habilitada, comuníquese con el administrador.", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(this, IniciarSesion.class));
                    }
                } else {
                    // El documento no existe
                    Toast.makeText(IniciarSesion.this, "Usuario no encontrado en la base de datos.", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Error al obtener el documento
                Toast.makeText(IniciarSesion.this, "Error al obtener datos del usuario.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setupSignUpText() {
        binding.signupText.setOnClickListener(v -> {
            startActivity(new Intent(IniciarSesion.this, RegistroUsuario.class));
        });
    }

    // Agregar el siguiente método en tu actividad IniciarSesion
    private void showForgotPasswordDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_forgot_password, null);
        EditText emailEditText = view.findViewById(R.id.editTextEmail);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view)
                .setTitle("Recuperar contraseña")
                .setPositiveButton("Enviar", (dialog, which) -> {
                    String email = emailEditText.getText().toString().trim();
                    if (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        sendPasswordResetEmail(email);
                    } else {
                        Toast.makeText(IniciarSesion.this, "Ingrese un correo electrónico válido", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void sendPasswordResetEmail(String email) {
        auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(IniciarSesion.this, "Se ha enviado un correo electrónico para restablecer la contraseña.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(IniciarSesion.this, "Error al enviar el correo electrónico. Verifique la dirección de correo electrónico.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
