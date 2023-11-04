package com.example.telecommunity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class DonationFragment extends Fragment {

    private static final int PICK_IMAGE = 1;
    private ImageView imageViewCaptura;
    private Button buttonSubirCaptura;
    private Button buttonEnviarCaptura;
    private static final String NOTIFICATION_TITLE = "Captura de donación subida correctamente";
    private static final String NOTIFICATION_BODY = "Tu captura se ha subido correctamente. Cuando el delegado general revise tu donación, se te enviará un acuse de recibo y, en caso seas egresado, se te enviará los detalles para recoger tu kit teleco.";
    private ProgressDialog progressDialog;

    FirebaseStorage storage;
    StorageReference reference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donation, container, false);

        imageViewCaptura = view.findViewById(R.id.imageViewCaptura);
        buttonSubirCaptura = view.findViewById(R.id.buttonSubirCaptura);
        buttonEnviarCaptura = view.findViewById(R.id.buttonEnviarCaptura);

        storage = FirebaseStorage.getInstance();
        reference = storage.getReference();

        // Crear un ProgressBar para mostrarlo en el AlertDialog
        ProgressBar progressBar = new ProgressBar(getActivity());
        progressBar.setIndeterminate(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(progressBar);
        AlertDialog loadingDialog = builder.create();
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT)); // Hacer el fondo transparente
        loadingDialog.setCanceledOnTouchOutside(false); // Para que no se pueda cancelar tocando fuera

        buttonSubirCaptura.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE);
        });

        buttonEnviarCaptura.setOnClickListener(v -> new AlertDialog.Builder(getActivity())
                .setTitle("Confirmar envío")
                .setMessage("¿Estás seguro de que quieres subir esta imagen?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    buttonEnviarCaptura.setEnabled(false);
                    buttonSubirCaptura.setEnabled(false);
                    loadingDialog.show();

                    Bitmap bitmap = ((BitmapDrawable) imageViewCaptura.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    String uniqueID = UUID.randomUUID().toString();
                    StorageReference imageRef = reference.child("images/" + uniqueID + ".jpg");

                    UploadTask uploadTask = imageRef.putBytes(data);
                    uploadTask.addOnSuccessListener(taskSnapshot -> {
                                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                    String imageURL = uri.toString();
                                    if (currentUser != null) {
                                        String userEmail = currentUser.getEmail();
                                        db.collection("usuarios")
                                                .whereEqualTo("correo", userEmail)
                                                .get()
                                                .addOnSuccessListener(querySnapshot -> {
                                                    if (!querySnapshot.isEmpty()) {
                                                        String userCode = String.valueOf(querySnapshot.getDocuments().get(0).getLong("codigo"));
                                                        Timestamp timestamp = new Timestamp(new java.util.Date());

                                                        Map<String, Object> newDonation = new HashMap<>();
                                                        newDonation.put("url", imageURL);
                                                        newDonation.put("codigo", userCode);
                                                        newDonation.put("timestamp", timestamp);

                                                        db.collection("donacion").add(newDonation);

                                                        Map<String, Object> newNotification = new HashMap<>();
                                                        newNotification.put("titulo", NOTIFICATION_TITLE);
                                                        newNotification.put("cuerpo", NOTIFICATION_BODY);
                                                        newNotification.put("codigo", userCode);
                                                        newNotification.put("timestamp", timestamp);
                                                        newNotification.put("tipo","donacion");

                                                        db.collection("notificaciones").add(newNotification);

                                                        loadingDialog.dismiss(); // Cierra el dialogo de carga
                                                        buttonEnviarCaptura.setEnabled(true);
                                                        buttonSubirCaptura.setEnabled(true);

                                                        new AlertDialog.Builder(getActivity())
                                                                .setTitle("Captura Enviada")
                                                                .setMessage("Captura subida correctamente")
                                                                .setPositiveButton(android.R.string.ok, (dialog1, which1) -> {})
                                                                .show();

                                                        showNotification();
                                                        imageViewCaptura.setImageResource(R.drawable.regalito); // Replace with your default image
                                                    }
                                                })
                                                .addOnFailureListener(e -> {
                                                    loadingDialog.dismiss(); // Cierra el dialogo de carga en caso de fallo
                                                    Toast.makeText(getActivity(), "Error al buscar el usuario", Toast.LENGTH_SHORT).show();
                                                });
                                    }
                                });
                            })
                            .addOnFailureListener(e -> {
                                loadingDialog.dismiss(); // Cierra el dialogo de carga en caso de fallo
                                buttonEnviarCaptura.setEnabled(true);
                                buttonSubirCaptura.setEnabled(true);
                                Toast.makeText(getActivity(), "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                            });

                })
                .setNegativeButton(android.R.string.no, null)
                .show());

        return view;
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == getActivity().RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));
                imageViewCaptura.setImageBitmap(bitmap);
                buttonEnviarCaptura.setEnabled(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showNotification() {
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel("notificacion", "TeleCommunity", NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "notificacion")
                .setSmallIcon(R.drawable.logo_black) // Reemplaza con tu icono
                .setContentTitle("Captura enviada con éxito")
                .setContentText("Imagen subida exitosamente")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        notificationManager.notify(1, builder.build());
    }
}