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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class VerUsuario extends AppCompatActivity {

    TextView detailCondicion, detailNombre, detailCorreo;
    ImageView detailImage;
    private String userCode;
    private String state, userMail, userName,userLogueado,urol;
    private ExecutorService emailExecutor = Executors.newSingleThreadExecutor();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_usuario);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // El usuario está logueado
            String userId = currentUser.getUid(); // Obtiene el UID del usuario
            userLogueado = currentUser.getEmail(); // Obtiene el correo electrónico del usuario
            // ... puedes obtener más información si la necesitas
        } else {
            // No hay ningún usuario logueado
        }

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
            intent.putExtra("IdUser", userCode);
            startActivity(intent);
        });


        Button banear = findViewById(R.id.banearButton);
        Button convertirDelegado = findViewById(R.id.editarButton2);
        //se valida el estado actual del usuario
        FirebaseFirestore dbAct = FirebaseFirestore.getInstance();
        dbAct.collection("usuarios").document(userCode).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Obtiene el valor del campo "tipo"
                        state = documentSnapshot.getString("estado");
                        userMail = documentSnapshot.getString("correo");
                        urol = documentSnapshot.getString("rol");
                        userName= documentSnapshot.getString("nombre") + " "+ documentSnapshot.getString("apellido") ;

                        if ("activo".equals(state)) {
                            banear.setText("Banear usuario");
                            if("jesus@pucp.edu.pe".equals(userLogueado)&& !"jesus@pucp.edu.pe".equals(userMail)){
                                convertirDelegado.setVisibility(View.VISIBLE);
                                if("Delegado general".equals(urol)){
                                    convertirDelegado.setText("Degradar a usuario");
                                }
                            }else {
                                convertirDelegado.setVisibility(View.GONE);
                            }

                        } else if ("baneado".equals(state)) {
                            // Crea un diálogo de alerta para confirmar la acción.
                            banear.setText("Desbanear usuario");
                            convertirDelegado.setVisibility(View.GONE);
                        }else if ("pendiente".equals(state)) {
                            // Crea un diálogo de alerta para confirmar la acción.
                            banear.setText("Aceptar solicitud");
                            banear.setBackgroundColor(getResources().getColor(R.color.purple));
                            convertirDelegado.setVisibility(View.GONE);
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
                            try {
                                enviarEmailBaneado(userMail, userName);
                            } catch (MessagingException e) {
                                throw new RuntimeException(e);
                            }
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

                            try {
                                enviarEmailDesbaneado(userMail, userName);
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


        //convertir en delegado general
        convertirDelegado.setOnClickListener(v -> {
            //se valida el estado actual del usuario
            if ("Delegado de actividad".equals(urol) ||"Usuario".equals(urol) ) {
                // Crea un diálogo de alerta para confirmar la acción.
                new AlertDialog.Builder(VerUsuario.this)
                        .setTitle("Confirmar acción")
                        .setMessage("¿Estás seguro de que deseas convertir en Delegado General a este usuario?")
                        .setPositiveButton("Sí", (dialog, which) -> {
                            // Usuario ha confirmado la acción.
                            convertirDelegadoUsuario(); // Función para cambiar el estado.
                            try {
                                enviarEmailDelegado(userMail, userName);
                            } catch (MessagingException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .setNegativeButton("Cancelar", (dialog, which) -> {
                            // Usuario ha cancelado la acción.
                            dialog.dismiss(); // Cierra el diálogo.
                        })
                        .show();
            }else if ("Delegado general".equals(urol)) {
                // Crea un diálogo de alerta para confirmar la acción.
                new AlertDialog.Builder(VerUsuario.this)
                        .setTitle("Confirmar acción")
                        .setMessage("¿Estás seguro de que deseas DEGRADAR a este usuario?")
                        .setPositiveButton("Sí", (dialog, which) -> {
                            // Usuario ha confirmado la acción.
                            desconvertirDelegadoUsuario(); // Función para cambiar el estado.

                            try {
                                enviarEmailDelegadoDegradado(userMail, userName);
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
    private void convertirDelegadoUsuario() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference actividadRef = db.collection("usuarios").document(userCode);

//                              Actualiza el campo "estado" a "Finalizado".
        actividadRef.update("rol", "Delegado general")
                .addOnSuccessListener(aVoid -> {
                    // El estado se actualizó con éxito en Firestore.
                    Toast.makeText(VerUsuario.this, "El usuario seleccionado ahora es Delegado General", Toast.LENGTH_SHORT).show();

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

    private void desconvertirDelegadoUsuario() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference actividadRef = db.collection("usuarios").document(userCode);

//                              Actualiza el campo "estado" a "Finalizado".
        actividadRef.update("rol", "Usuario")
                .addOnSuccessListener(aVoid -> {
                    // El estado se actualizó con éxito en Firestore.
                    Toast.makeText(VerUsuario.this, "El usuario seleccionado ahora es Usuario común", Toast.LENGTH_SHORT).show();

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
                    message.setSubject("¡Bienvenido a la comunidad");
                    String logoUrl = "https://firebasestorage.googleapis.com/v0/b/telecommunity-cbff5.appspot.com/o/logo_black.png?alt=media&token=cf736928-c933-4096-b0fe-ffa7b4de8ddc";

                    String appName = "Telecommunity"; // Nombre de la aplicación



                    String htmlContent = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//ES\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" +
                            "<html xmlns=\"http://www.w3.org/1999/xhtml\">" +
                            "<head>" +
                            "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />" +
                            "<title>Bienvenido a la comunidad</title>" +
                            "</head>" +
                            "<body>" +
                            "<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">" +
                            "<tr>" +
                            "<td bgcolor=\"#00C293\">" +
                            "<table width=\"650\" border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\">" +
                            "<tr>" +
                            "<td style=\"line-height:0px; padding:20px; background-color:#00C293\">" +
                            "<table border=\"0\">" +
                            "<tr>" +
                            "<td style=\"padding-right: 10px; vertical-align: middle;\"><img src=\"" + logoUrl + "\" alt=\"Telecommunity Logo\" width=\"146px\" border=\"0\" /></td>" +
                            "<td style=\"color: #000000; font-family: Arial, Helvetica, sans-serif; font-size: 22px; font-weight: bold; vertical-align: middle; padding-top: 5px;\">" + appName + "</td>" +
                            "</tr>" +
                            "</table>" +
                            "</td>" +
                            "</tr>" +
                            "<tr>" +
                            "<td bgcolor=\"#FFFFFF\" style=\"color: #042354; font-family: Arial, Helvetica, sans-serif; font-size: 15px; line-height: 24px; padding: 25px 38px 15px 38px; text-align: justify;\">" +
                            "<p><strong>Estimado/a " + nombre + ":</strong></p>" +
                            "<p>Su solicitud de registro ha sido <strong>aprobada</strong>, ya puede iniciar sesión.</p>" +
                            "<p>Atentamente,</p>" +
                            "<p>El equipo de " + appName + "</p>" +
                            "</td>" +
                            "</tr>" +
                            "</table>" +
                            "</td>" +
                            "</tr>" +
                            "</table>" +
                            "</body>" +
                            "</html>";

                    message.setContent(htmlContent, "text/html; charset=utf-8");

                    Transport.send(message);
                    System.out.println("Email enviado con éxito");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public void enviarEmailDelegado(String destinatario, String nombre) throws MessagingException {

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
                    message.setSubject("Bienvenido al equipo de Delegados Generales");

                    // Contenido HTML con estilos en línea
                    String htmlContent = "<div style='background-color:#f8f8f8; padding:20px; text-align:center;'>"
                            + "<h1 style='color:#333;'>¡Ahora eres Delegado General!</h1>"
                            + "<p style='color:#555;'>Estimado usuario <em>" + nombre + "</em>, </p>"
                            + "<p style='color:#555;'>Su cuenta de teleCommunity ahora tiene privilegios de <strong>Delegado General</strong>.</p>"
                            + "<p style='color:#555;'>Si desea mas detalles, por favor responda este correo</p>"
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

    public void enviarEmailDelegadoDegradado(String destinatario, String nombre) throws MessagingException {

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
                    message.setSubject("Ya no formas parte de los Delegados Generales");

                    // Contenido HTML con estilos en línea
                    String htmlContent = "<div style='background-color:#f8f8f8; padding:20px; text-align:center;'>"
                            + "<h1 style='color:#333;'>¡Lo sentimos!</h1>"
                            + "<p style='color:#555;'>Estimado usuario <em>" + nombre + "</em>, </p>"
                            + "<p style='color:#555;'>Su cuenta de teleCommunity ahora tiene privilegios de <strong>Usuario común</strong>.</p>"
                            + "<p style='color:#555;'>Si desea más detalles, por favor responda este correo</p>"
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

    public void enviarEmailBaneado(String destinatario, String nombre) throws MessagingException {

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
                    message.setSubject("Cuenta suspendida");

                    // Contenido HTML con estilos en línea
                    String htmlContent = "<div style='background-color:#f8f8f8; padding:20px; text-align:center;'>"
                            + "<h1 style='color:#333;'>¡Su cuenta ha sido suspendida!</h1>"
                            + "<p style='color:#555;'>Estimado usuario <em>" + nombre + "</em>, </p>"
                            + "<p style='color:#555;'>Su cuenta de teleCommunity ha sido <strong>suspendida</strong>.</p>"
                            + "<p style='color:#555;'>Si esta en desacuerdo, por favor responda este correo</p>"
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

    public void enviarEmailDesbaneado(String destinatario, String nombre) throws MessagingException {

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
                    message.setSubject("Cuenta reactivada");

                    // Contenido HTML con estilos en línea
                    String htmlContent = "<div style='background-color:#f8f8f8; padding:20px; text-align:center;'>"
                            + "<h1 style='color:#333;'>¡Solicitud Aprobada!</h1>"
                            + "<p style='color:#555;'>Estimado usuario <em>" + nombre + "</em>, </p>"
                            + "<p style='color:#555;'>Su solicitud de desbaneo ha sido <strong>aprobada</strong>.</p>"
                            + "<p style='color:#555;'>¡Bienvenido de regreso a la comunidad!</p>"
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