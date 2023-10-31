package com.example.telecommunity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
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

    FirebaseStorage storage;
    StorageReference reference;
    FirebaseFirestore db = FirebaseFirestore.getInstance(); // Añadir Firestore
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser(); // Obtén el usuario actual

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donation, container, false);

        // Inicialización de las vistas
        imageViewCaptura = view.findViewById(R.id.imageViewCaptura);
        buttonSubirCaptura = view.findViewById(R.id.buttonSubirCaptura);
        buttonEnviarCaptura = view.findViewById(R.id.buttonEnviarCaptura);

        storage = FirebaseStorage.getInstance();
        reference = storage.getReference();

        // Configurar el botón para subir la captura
        buttonSubirCaptura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE);
            }
        });

        // Configurar el botón para enviar la captura
        buttonEnviarCaptura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = ((BitmapDrawable) imageViewCaptura.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                String uniqueID = UUID.randomUUID().toString();
                StorageReference imageRef = reference.child("images/" + uniqueID + ".jpg");

                UploadTask uploadTask = imageRef.putBytes(data);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageURL = uri.toString();
                                        if (currentUser != null) {
                                            String userEmail = currentUser.getEmail();
                                            db.collection("usuarios")
                                                    .whereEqualTo("correo", userEmail)
                                                    .get()
                                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onSuccess(QuerySnapshot querySnapshot) {
                                                            if (!querySnapshot.isEmpty()) {
                                                                String userCode = String.valueOf(querySnapshot.getDocuments().get(0).getLong("codigo"));
                                                                Timestamp timestamp = new Timestamp(new java.util.Date()); // Usar Timestamp de Firestore

                                                                Map<String, Object> newDonation = new HashMap<>();
                                                                newDonation.put("url", imageURL);
                                                                newDonation.put("codigo", userCode);
                                                                newDonation.put("timestamp", timestamp); // Guardar como Timestamp

                                                                db.collection("donacion").add(newDonation);
                                                            }
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getActivity(), "Error al buscar el usuario", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

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
}
