package com.example.telecommunity.DelegadoGeneral;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import com.example.telecommunity.IniciarSesion;
import com.example.telecommunity.PantallaPrincipal;
import com.example.telecommunity.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class VerUsuario extends AppCompatActivity {

    TextView detailCondicion, detailNombre, detailCorreo;
    ImageView detailImage;
    private String userCode;
    private String state, userMail, userName;
    private ExecutorService emailExecutor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_usuario);


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

            userCode = bundle.getString("UserCode");
            String imageUrl = bundle.getString("Image");
            Picasso.get().load(imageUrl).into(detailImage);
        }


        //ir a editar usuario
        Button iniciar = findViewById(R.id.editarButton);
        iniciar.setOnClickListener(view -> {
            Intent intent = new Intent(VerUsuario.this, EditarUsuario.class);
            startActivity(intent);
        });


        Button banear = findViewById(R.id.banearButton);
        //se valida el estado actual del usuario
        FirebaseFirestore dbAct = FirebaseFirestore.getInstance();
        dbAct.collection("usuarios").document(userCode).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Obtiene el valor del campo "tipo"
                        state = documentSnapshot.getString("estado");
                        userMail = documentSnapshot.getString("correo");
                        userName= documentSnapshot.getString("nombre") + " "+ documentSnapshot.getString("apellido") ;

                        if ("activo".equals(state)) {
                            banear.setText("Banear usuario");

                        } else if ("baneado".equals(state)) {
                            // Crea un diálogo de alerta para confirmar la acción.
                            banear.setText("Desbanear usuario");

                        }else if ("pendiente".equals(state)) {
                            // Crea un diálogo de alerta para confirmar la acción.
                            banear.setText("Aceptar solicitud");
                            banear.setBackgroundColor(getResources().getColor(R.color.purple));

                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Maneja los errores si la lectura del documento falla
                });

        //ir a banear usuario
        banear.setOnClickListener(v -> {
            //se valida el estado actual del usuario
            if ("activo".equals(state)) {
                // Crea un diálogo de alerta para confirmar la acción.
                new AlertDialog.Builder(VerUsuario.this)
                        .setTitle("Confirmar acción")
                        .setMessage("¿Estás seguro de que deseas banear a este usuario?")
                        .setPositiveButton("Sí", (dialog, which) -> {
                            // Usuario ha confirmado la acción.
                            banearUsuario(); // Función para cambiar el estado.
                        })
                        .setNegativeButton("Cancelar", (dialog, which) -> {
                            // Usuario ha cancelado la acción.
                            dialog.dismiss(); // Cierra el diálogo.
                        })
                        .show();
            } else if ("baneado".equals(state)) {
                // Crea un diálogo de alerta para confirmar la acción.
                new AlertDialog.Builder(VerUsuario.this)
                        .setTitle("Confirmar acción")
                        .setMessage("¿Estás seguro de que deseas desbanear a este usuario?")
                        .setPositiveButton("Sí", (dialog, which) -> {
                            // Usuario ha confirmado la acción.
                            desbanearUsuario(); // Función para cambiar el estado.
                        })
                        .setNegativeButton("Cancelar", (dialog, which) -> {
                            // Usuario ha cancelado la acción.
                            dialog.dismiss(); // Cierra el diálogo.
                        })
                        .show();
            }
            else if ("pendiente".equals(state)) {
                // Crea un diálogo de alerta para confirmar la acción.
                new AlertDialog.Builder(VerUsuario.this)
                        .setTitle("Confirmar acción")
                        .setMessage("¿Estás seguro de que deseas ACTIVAR a este usuario?")
                        .setPositiveButton("Sí", (dialog, which) -> {
                            // Usuario ha confirmado la acción.
                            activarUsuario(); // Función para cambiar el estado.

                            try {
                                enviarEmailAprobacion(userMail, userName);
                            } catch (MessagingException e) {
                                throw new RuntimeException(e);
                            }

                        })
                        .setNegativeButton("Cancelar", (dialog, which) -> {
                            // Usuario ha cancelado la acción.
                            dialog.dismiss(); // Cierra el diálogo.
                        })
                        .show();
            }
        });

    }

    private void banearUsuario() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference actividadRef = db.collection("usuarios").document(userCode);

//                              Actualiza el campo "estado" a "Finalizado".
        actividadRef.update("estado", "baneado")
                .addOnSuccessListener(aVoid -> {
                    // El estado se actualizó con éxito en Firestore.
                    Toast.makeText(VerUsuario.this, "Usuario baneado con éxito", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(VerUsuario.this, BaseGeneralActivity.class);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // Para asegurar que AdmActividades sea la única actividad en la pila
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Ocurrió un error al actualizar el estado en Firestore.
                    // Puedes mostrar un mensaje de error o realizar acciones de manejo de errores.
                });



    }

    private void desbanearUsuario() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference actividadRef = db.collection("usuarios").document(userCode);

//                              Actualiza el campo "estado" a "Finalizado".
        actividadRef.update("estado", "activo")
                .addOnSuccessListener(aVoid -> {
                    // El estado se actualizó con éxito en Firestore.
                    Toast.makeText(VerUsuario.this, "Usuario desbaneado con éxito", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(VerUsuario.this, BaseGeneralActivity.class);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // Para asegurar que AdmActividades sea la única actividad en la pila
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Ocurrió un error al actualizar el estado en Firestore.
                    // Puedes mostrar un mensaje de error o realizar acciones de manejo de errores.
                });




    }



    private void activarUsuario() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference actividadRef = db.collection("usuarios").document(userCode);

//                              Actualiza el campo "estado" a "Finalizado".
        actividadRef.update("estado", "activo")
                .addOnSuccessListener(aVoid -> {
                    // El estado se actualizó con éxito en Firestore.
                    Toast.makeText(VerUsuario.this, "Usuario activado con éxito", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(VerUsuario.this, BaseGeneralActivity.class);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // Para asegurar que AdmActividades sea la única actividad en la pila
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Ocurrió un error al actualizar el estado en Firestore.
                    // Puedes mostrar un mensaje de error o realizar acciones de manejo de errores.
                });




    }


    public void enviarEmailAprobacion(String destinatario, String nombre) throws MessagingException {

        emailExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // Aquí va tu código existente para enviar el email
                    // Configuración de las propiedades SMTP
                    Properties prop = new Properties();
                    prop.put("mail.smtp.host", "smtp.gmail.com");
                    prop.put("mail.smtp.port", "587");
                    prop.put("mail.smtp.auth", "true");
                    prop.put("mail.smtp.starttls.enable", "true"); //TLS

                    // Autenticación
                    final String username = "apptelecommunity@gmail.com"; // Cambiar por tu email
                    final String password = "ayka dtem fonq ydeu"; // Cambiar por tu contraseña

                    Session session = Session.getInstance(prop, new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });

                    // Creación y envío del mensaje
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress("apptelecommunity@gmail.com"));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
                    message.setSubject("Aprobación de Registro");

                    // Contenido HTML con estilos en línea
                    String htmlContent = "<div style='background-color:#f8f8f8; padding:20px; text-align:center;'>"
                            + "<h1 style='color:#333;'>¡Solicitud Aprobada!</h1>"
                            + "<p style='color:#555;'>Estimado usuario <em>" + nombre + "</em>, </p>"
                            + "<p style='color:#555;'>Su solicitud de registro ha sido <strong>aprobada</strong>.</p>"
                            + "<p style='color:#555;'>¡Bienvenido a la comunidad!</p>"
                            + "</div>";

                    message.setContent(htmlContent, "text/html; charset=utf-8");

                    Transport.send(message);
                    System.out.println("Email enviado con éxito");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }
}