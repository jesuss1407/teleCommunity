package com.example.telecommunity;

import static android.content.ContentValues.TAG;



import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;


public class ProfileFragment extends Fragment {
    private static final int PICK_IMAGE = 1;
    private TextView editTextNombres;
    private TextView editTextCorreo;
    private TextView editTextApellidos;
    private Spinner spinnerCondicion;
    private TextView editTextTelefono;
    private Button btnSave;
    private String documentId;

    private ImageView Foto;
    private AlertDialog loadingDialog;
    StorageReference reference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Button logout = view.findViewById(R.id.logout);
        editTextNombres = view.findViewById(R.id.editTextNombres);
        editTextApellidos = view.findViewById(R.id.editTextApellidos);
        editTextCorreo = view.findViewById(R.id.editTextCorreo);
        editTextTelefono = view.findViewById(R.id.editTextTelefono);
        btnSave = view.findViewById(R.id.btnSave);
        spinnerCondicion = view.findViewById(R.id.spinnerCondicion);
        Foto = view.findViewById(R.id.Foto);
        Button buttonFoto = view.findViewById(R.id.buttonFoto);

        editTextCorreo.setEnabled(false);

        reference = FirebaseStorage.getInstance().getReference();


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireActivity(),
                R.array.condiciones_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCondicion.setAdapter(adapter);

        ProgressBar progressBar = new ProgressBar(getActivity());
        progressBar.setIndeterminate(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(progressBar);
        loadingDialog = builder.create();
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loadingDialog.setCanceledOnTouchOutside(false);

        buttonFoto.setOnClickListener(v -> {

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE);
        });

        btnSave.setOnClickListener(v -> {
            if (validateForm()) {
                loadingDialog.show();

                String nombres = editTextNombres.getText().toString();
                String apellidos = editTextApellidos.getText().toString();
                String correo = editTextCorreo.getText().toString();
                String telefono = editTextTelefono.getText().toString();
                String condicion = spinnerCondicion.getSelectedItem().toString();

                DocumentReference docRef = db.collection("usuarios").document(documentId);

                docRef.update(
                        "nombre", nombres,
                        "apellido", apellidos,
                        "correo", correo,
                        "telefono", telefono,
                        "condicion", condicion
                ).addOnSuccessListener(aVoid -> {
                    loadingDialog.dismiss();
                    Toast.makeText(getActivity(), "Datos actualizados", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    Toast.makeText(getActivity(), "Error al actualizar", Toast.LENGTH_SHORT).show();
                });
            }
        });

        logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getActivity(), IniciarSesion.class));
        });

        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            db.collection("usuarios")
                    .whereEqualTo("correo", userEmail)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        for (DocumentSnapshot document : querySnapshot) {
                            documentId = document.getId();
                            editTextApellidos.setText(document.getString("apellido"));
                            editTextNombres.setText(document.getString("nombre"));
                            editTextCorreo.setText(document.getString("correo"));
                            editTextTelefono.setText(document.getString("telefono"));
                            String condicionAlmacenada = document.getString("condicion");
                            int spinnerPosition = adapter.getPosition(condicionAlmacenada);
                            spinnerCondicion.setSelection(spinnerPosition);

                            String fotoUrl = document.getString("foto");
                            if (fotoUrl != null && !fotoUrl.isEmpty()) {
                                loadProfileImageFromURL(fotoUrl);
                            } else {
                                Foto.setImageResource(R.drawable.blanco);
                            }
                        }
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getActivity(), "Error al cargar datos", Toast.LENGTH_SHORT).show();
                    });
        }

        return view;
    }

    private void loadProfileImageFromURL(String imageUrl) {
        new DownloadImageTask(Foto).execute(imageUrl);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == getActivity().RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            Foto.setImageURI(imageUri);
            uploadImageToFirebase(imageUri);

        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        loadingDialog.show(); // Mostrar el ícono de carga

        StorageReference imageRef = reference.child("images/" + documentId + "/foto.jpg");

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    updateDocumentImageUrl(imageUrl);
                }))
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss(); // Ocultar el ícono de carga
                    Toast.makeText(getActivity(), "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateDocumentImageUrl(String imageUrl) {
        DocumentReference docRef = db.collection("usuarios").document(documentId);
        docRef.update("foto", imageUrl)
                .addOnSuccessListener(aVoid -> {
                    loadingDialog.dismiss(); // Ocultar el ícono de carga
                    Toast.makeText(getActivity(), "Imagen subida con éxito", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss(); // Ocultar el ícono de carga
                    Toast.makeText(getActivity(), "Error al actualizar la imagen en el perfil", Toast.LENGTH_SHORT).show();
                });
    }

    private boolean validateForm() {
        boolean isValid = true;

        String nombres = editTextNombres.getText().toString().trim();
        String apellidos = editTextApellidos.getText().toString().trim();
        String telefono = editTextTelefono.getText().toString().trim();

        if (nombres.isEmpty()) {
            editTextNombres.setError("El nombre es obligatorio.");
            isValid = false;
        } else if (!nombres.matches("[a-zA-ZñÑáéíóúÁÉÍÓÚ ]+")) {
            editTextNombres.setError("El nombre no puede contener números.");
            isValid = false;
        } else {
            editTextNombres.setError(null);
        }

        if (apellidos.isEmpty()) {
            editTextApellidos.setError("El apellido es obligatorio.");
            isValid = false;
        } else if (!apellidos.matches("[a-zA-ZñÑáéíóúÁÉÍÓÚ ]+")) {
            editTextApellidos.setError("El apellido no puede contener números.");
            isValid = false;
        } else {
            editTextApellidos.setError(null);
        }

        if (telefono.isEmpty()) {
            editTextTelefono.setError("El teléfono es obligatorio.");
            isValid = false;
        } else if (!telefono.matches("\\d{9}")) {
            editTextTelefono.setError("El teléfono debe tener 9 dígitos y solo números.");
            isValid = false;
        } else {
            editTextTelefono.setError(null);
        }

        return isValid;
    }

}