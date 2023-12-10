package com.example.telecommunity.DelegadoGeneral;



import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.telecommunity.R;
import com.example.telecommunity.entity.Donacion;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class EventoDetalleFragment extends Fragment {

    private TextView textViewTituloEvento;
    private TextView textViewFechaEvento;
    private ImageView imageViewDonacion;
    private Context context;
    private Button deleteButton;
    private Button confirmButton;
    private EditText monto;
    private AlertDialog loadingDialog;

    private String correo;
    private String condicion;
    private String codigo;
    private String nombre;
    private ExecutorService emailExecutor = Executors.newSingleThreadExecutor();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public EventoDetalleFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        ProgressBar progressBar = new ProgressBar(getActivity());
        progressBar.setIndeterminate(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(progressBar);
        loadingDialog = builder.create();
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.setCanceledOnTouchOutside(false);


        return inflater.inflate(R.layout.fragment_evento_detalle, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        monto=view.findViewById(R.id.monto);
        textViewTituloEvento = view.findViewById(R.id.textViewTituloEvento);
        textViewFechaEvento = view.findViewById(R.id.textViewFechaEvento);
        imageViewDonacion=view.findViewById(R.id.imageViewDonacion);
        deleteButton = view.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(v -> mostrarConfirmacionEliminacion());

        confirmButton = view.findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(v -> mostrarConfirmacionDonacion());



        Bundle bundle = getArguments();
        if (bundle != null) {
            Donacion donacion = (Donacion) bundle.getSerializable("donacion");
            if (donacion != null) {
                mostrarDetalleEvento(donacion);
            }
        }
    }

    private void mostrarConfirmacionDonacion() {
        String montoIngresado = monto.getText().toString();

        if (isValidAmount(montoIngresado)) {
            loadingDialog.show();
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Confirmar donación");
            builder.setMessage("¿Confirmar monto de: " + montoIngresado + "?");
            builder.setPositiveButton("Confirmar", (dialog, which) -> {
                try {
                    confirmarDonacion();
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            });
            builder.setNegativeButton("Cancelar", (dialog,which) -> loadingDialog.dismiss());
            builder.show();
        } else {
            monto.setError("Monto inválido");
        }
    }

    private boolean isValidAmount(String amount) {
        if (amount.isEmpty()) {
            return false;
        }


        String regex = "^(\\d+(\\.\\d{1,2})?)$";

        if (amount.matches(regex)){
            if (Double.parseDouble(amount)>=1){
                return amount.matches(regex);
            }
        }
        return false;
    }


    private void confirmarDonacion() throws MessagingException {
        Donacion donacion = obtenerDonacionDesdeBundle();
        if (donacion != null) {
            String codigoUsuario = donacion.getCodigo();
            String montoDonacion = monto.getText().toString();
            String condicionDonante = condicion.toLowerCase();

            if (isValidAmount(montoDonacion)) {

                if (Double.parseDouble(montoDonacion) >= 100 && condicionDonante.equals("egresado")) {
                    mostrarSeleccionFechaHora();
                } else {
                    enviarCorreoAlumno(correo,nombre,montoDonacion);
                    confirmarDonacionFirebase(codigoUsuario, montoDonacion);
                }
            } else {
                monto.setError("Monto inválido");
            }
        }
    }

    private void mostrarSeleccionFechaHora() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, yearSelected, monthOfYear, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(yearSelected, monthOfYear, dayOfMonth);

            // Verificar si la fecha seleccionada es hoy o en el futuro
            if (selectedDate.after(calendar) || selectedDate.equals(calendar)) {
                mostrarSeleccionHora(yearSelected, monthOfYear, dayOfMonth);
            } else {
                loadingDialog.dismiss();
                Toast.makeText(getActivity(), "Selecciona una fecha válida", Toast.LENGTH_SHORT).show();
            }
        }, year, month, day);
        datePickerDialog.setOnCancelListener(dialogInterface -> {
            loadingDialog.dismiss();
            // Aquí puedes realizar cualquier acción adicional al cancelar el diálogo de selección de fecha
        });
        datePickerDialog.show();
    }

    private void mostrarSeleccionHora(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        int seconds=0;

        // Sumar una hora a la hora actual
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        int minHour = calendar.get(Calendar.HOUR_OF_DAY);

        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), (view, hourOfDay, minuteOfDay) -> {
            Calendar selectedDateTime = Calendar.getInstance();
            selectedDateTime.set(year, month, day, hourOfDay, minuteOfDay,seconds);

            // Verificar si la hora seleccionada es al menos una hora después de la actual
            if (selectedDateTime.after(calendar) || (hourOfDay == minHour && minuteOfDay >= minute)) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                String formattedDate = formatter.format(selectedDateTime.getTime());
                Date date = selectedDateTime.getTime();

                com.google.firebase.Timestamp timestamp = new com.google.firebase.Timestamp(date);

                actualizarFechaFirebase(timestamp, codigo, monto.getText().toString());
            } else {
                loadingDialog.dismiss();
                Toast.makeText(getActivity(), "Selecciona una hora válida", Toast.LENGTH_SHORT).show();
            }
        }, hour, minute, false);

        timePickerDialog.setOnCancelListener(dialogInterface -> {
            loadingDialog.dismiss();
            // Aquí puedes realizar cualquier acción adicional al cancelar el diálogo de selección de fecha
        });
        timePickerDialog.show();
    }


    private void actualizarFechaFirebase(com.google.firebase.Timestamp timestamp, String codigoUsuario, String montoDonacion) {
        // Subir la fecha a Firebase y enviar el correo
        Map<String, Object> confirmacion = new HashMap<>();
        confirmacion.put("fecha", timestamp);
        confirmacion.put("monto", montoDonacion);
        confirmacion.put("codigo",codigoUsuario);

        FirebaseFirestore.getInstance().collection("donacionconf")
                .document()
                .set(confirmacion)
                .addOnSuccessListener(aVoid -> {
                    enviarNotificacion();
                    eliminarDonacion();
                    Toast.makeText(getActivity(), "Donación confirmada", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                })
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                });

        // Envío del correo de aprobación
        try {
            enviarCorreoEgresado(correo, nombre,montoDonacion,timestamp.toDate().toLocaleString());
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }



    private void enviarCorreoEgresado(String destinatario, String nombre, String monto, String fecha) throws MessagingException {
        emailExecutor.execute(() -> {
            try {
                Properties prop = new Properties();
                prop.put("mail.smtp.host", "smtp.gmail.com");
                prop.put("mail.smtp.port", "587");
                prop.put("mail.smtp.auth", "true");
                prop.put("mail.smtp.starttls.enable", "true");

                final String username = "apptelecommunity@gmail.com"; // Cambiar por tu email
                final String password = "ayka dtem fonq ydeu"; // Cambiar por tu contraseña

                Session session = Session.getInstance(prop, new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("apptelecommunity@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
                message.setSubject("¡Gracias por tu donación!");

                String logoUrl = "https://firebasestorage.googleapis.com/v0/b/telecommunity-cbff5.appspot.com/o/logo_black.png?alt=media&token=cf736928-c933-4096-b0fe-ffa7b4de8ddc";

                String appName = "Telecommunity"; // Nombre de la aplicación

                // URL de la imagen del mapa de Google
                String googleMapImageURL = "https://maps.googleapis.com/maps/api/staticmap?center=-12.073091040556687%2C+-77.08189512658883&zoom=19&scale=2&size=600x300&maptype=roadmap&format=png&key=AIzaSyCgcBdCoM_Kt72yIre8Kwg2-LYDdf8uuzI&markers=size:mid%7Ccolor:0xff0033%7C-12.073091040556687%2C%20-77.08189512658883";

                // Enlace de redirección al hacer clic en la imagen
                String googleMapLink = "https://maps.app.goo.gl/obDY4qG6rr2u9xCJ8";



                String htmlContent = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//ES\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" +
                        "<html xmlns=\"http://www.w3.org/1999/xhtml\">" +
                        "<head>" +
                        "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />" +
                        "<title>Gracias por tu donación</title>" +
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
                        "<p>Su donación de: <strong>S/. " + monto + "</strong> ha sido verificada.</p>" +
                        "<p>Ya que tienes la condición de <strong>egresado</strong>, puedes recoger tu kit teleco el día: <strong>" + fecha + "</strong>.</p>" +
                        "<p>Para ver la ubicación de recojo, haz click en la imagen:</p>" +
                        "<a href=\"" + googleMapLink + "\"><img src=\"" + googleMapImageURL + "\" alt=\"Google map of -12.073091040556687, -77.08189512658883\" width=\"600\" height=\"300\" style=\"border:0;\" /></a>" +
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
        });
    }

    private void enviarCorreoAlumno(String destinatario, String nombre, String monto) throws MessagingException {
        emailExecutor.execute(() -> {
            try {
                Properties prop = new Properties();
                prop.put("mail.smtp.host", "smtp.gmail.com");
                prop.put("mail.smtp.port", "587");
                prop.put("mail.smtp.auth", "true");
                prop.put("mail.smtp.starttls.enable", "true");

                final String username = "apptelecommunity@gmail.com"; // Cambiar por tu email
                final String password = "ayka dtem fonq ydeu"; // Cambiar por tu contraseña

                Session session = Session.getInstance(prop, new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("apptelecommunity@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
                message.setSubject("¡Gracias por tu donación!");

                String logoUrl = "https://firebasestorage.googleapis.com/v0/b/telecommunity-cbff5.appspot.com/o/logo_black.png?alt=media&token=cf736928-c933-4096-b0fe-ffa7b4de8ddc";

                String appName = "Telecommunity"; // Nombre de la aplicación

                String htmlContent = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//ES\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" +
                        "<html xmlns=\"http://www.w3.org/1999/xhtml\">" +
                        "<head>" +
                        "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />" +
                        "<title>Gracias por tu donación</title>" +
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
                        "<p>Su donación de: <strong>S/. " + monto + "</strong> ha sido verificada.</p>" +
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }





    private void confirmarDonacionFirebase(String codigoUsuario, String montoDonacion) {
        Map<String, Object> confirmacion = new HashMap<>();
        confirmacion.put("fecha", FieldValue.serverTimestamp());
        confirmacion.put("monto", montoDonacion);
        confirmacion.put("codigo",codigoUsuario);

        FirebaseFirestore.getInstance().collection("donacionconf")
                .document()
                .set(confirmacion)
                .addOnSuccessListener(aVoid -> {
                    eliminarDonacion();
                    enviarNotificacion();
                    Toast.makeText(getActivity(), "Donación confirmada", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                })
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                });



    }


    private void enviarNotificacion(){
        Map<String, Object> confirmacion = new HashMap<>();
        confirmacion.put("codigo",codigo);
        confirmacion.put("cuerpo","¡Muchas gracias por tu donación! .Si eres egresad@, por favor, revisa los detalles de recojo de kit en tu correo electrónico ✉️.");
        confirmacion.put("timestamp", FieldValue.serverTimestamp());
        confirmacion.put("tipo", "donacion");
        confirmacion.put("titulo", "¡Donación confirmada!");
        FirebaseFirestore.getInstance().collection("notificaciones")
                .document()
                .set(confirmacion)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Envio");
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "No envio");
                });
    }





    private void mostrarConfirmacionEliminacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Confirmar eliminación");
        builder.setMessage("¿Estás seguro de eliminar esta donación?");
        builder.setPositiveButton("Eliminar", (dialog, which) -> eliminarDonacion());
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }
    private void eliminarDonacion() {
        Donacion donacion = obtenerDonacionDesdeBundle();
        if (donacion != null) {
            String imageUrl = donacion.getUrl();

            FirebaseFirestore.getInstance().collection("donacion")
                    .whereEqualTo("url", imageUrl)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                document.getReference().delete();
                            }
                            eliminarImagenFirebase(imageUrl);
                        }
                    });
        }
    }

    private void eliminarImagenFirebase(String imageUrl) {

        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
        storageRef.delete()
                .addOnSuccessListener(aVoid -> {
                    getActivity().onBackPressed();
                })
                .addOnFailureListener(exception -> {

                });
    }

    private Donacion obtenerDonacionDesdeBundle() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            return (Donacion) bundle.getSerializable("donacion");
        }
        return null;
    }


    public void mostrarDetalleEvento(Donacion donacion) {
        Date date = donacion.getTimestamp().toDate();

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String formattedDate = formatter.format(date);
        textViewFechaEvento.setText("Fecha: "+formattedDate);


        String imageUrl = donacion.getUrl();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(requireContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.blanco)
                    .error(R.drawable.blanco)
                    .into(imageViewDonacion);
        }

        FirebaseFirestore db2 = FirebaseFirestore.getInstance();
        db2.collection("usuarios")
                .whereEqualTo("codigo", Integer.parseInt(donacion.getCodigo()))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        for (DocumentSnapshot document : task.getResult()) {

                             condicion = document.getString("condicion");
                             nombre = document.getString("nombre");
                            String apellido = document.getString("apellido");
                             correo= document.getString("correo");
                             codigo=donacion.getCodigo();


                            TextView textViewCondicion = requireView().findViewById(R.id.textViewCondicion);
                            textViewCondicion.setText("Condición: "+condicion);

                            TextView textViewNombreApellido = requireView().findViewById(R.id.textViewNombreApellido);
                            String nombreApellido = String.format("%s, %s", apellido, nombre);
                            textViewNombreApellido.setText("Nombres: "+nombreApellido);

                        }
                    } else {
                        Log.d(TAG, "No coincide");
                    }
                });

    }
}
