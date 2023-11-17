package com.example.telecommunity.DelegadoGeneral;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.telecommunity.IniciarSesion;
import com.example.telecommunity.ProfileFragment;
import com.example.telecommunity.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.InputStream;

public class EditarUsuario extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private TextView editTextNombres;
    private TextView editTextCorreo;
    private TextView editTextApellidos;
    private Spinner spinnerCondicion;
    private TextView editTextTelefono;
    private Button btnSave;
    private String documentId, idUsuario;

    private Integer idUsuarioInt;

    private ImageView Foto;
    private AlertDialog loadingDialog;
    StorageReference reference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_usuario);


        editTextNombres = findViewById(R.id.editTextNombres);
        editTextApellidos = findViewById(R.id.editTextApellidos);
        editTextCorreo = findViewById(R.id.editTextCorreo);
        editTextTelefono = findViewById(R.id.editTextTelefono);
        btnSave = findViewById(R.id.btnSave);
        spinnerCondicion = findViewById(R.id.spinnerCondicion);
        Foto = findViewById(R.id.Foto);
        Button buttonFoto = findViewById(R.id.buttonFoto);

        editTextCorreo.setEnabled(false);

        reference = FirebaseStorage.getInstance().getReference();


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.condiciones_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCondicion.setAdapter(adapter);

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(progressBar);
        loadingDialog = builder.create();
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loadingDialog.setCanceledOnTouchOutside(false);


        //obtener usuario de la lista
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            idUsuario=bundle.getString("IdUser");

        }

        idUsuarioInt=Integer.parseInt(idUsuario);
        FirebaseFirestore db2 = FirebaseFirestore.getInstance();
        db2.collection("usuarios")
                .whereEqualTo("codigo", idUsuarioInt)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            editTextApellidos.setText(document.getString("apellido"));
                            editTextNombres.setText(document.getString("nombre"));
                            editTextCorreo.setText(document.getString("correo"));
                            //editTextTelefono.setText(document.getString("telefono"));
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
                    } else {
                        Log.d(TAG, "No coincide");
                    }
                });



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
                //String telefono = editTextTelefono.getText().toString();
                String condicion = spinnerCondicion.getSelectedItem().toString();

                DocumentReference docRef = db.collection("usuarios").document(idUsuario);

                docRef.update(
                        "nombre", nombres,
                        "apellido", apellidos,
                        "correo", correo,
                        //"telefono", telefono,
                        "condicion", condicion
                ).addOnSuccessListener(aVoid -> {
                    loadingDialog.dismiss();
                    Toast.makeText(this, "Datos actualizados", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                });
            }
        });



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

        if (requestCode == PICK_IMAGE && resultCode == this.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            Foto.setImageURI(imageUri);
            uploadImageToFirebase(imageUri);

        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        loadingDialog.show(); // Mostrar el ícono de carga

        StorageReference imageRef = reference.child("images/" + idUsuario + "/foto.jpg");

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    updateDocumentImageUrl(imageUrl);
                }))
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss(); // Ocultar el ícono de carga
                    Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateDocumentImageUrl(String imageUrl) {
        DocumentReference docRef = db.collection("usuarios").document(idUsuario);
        docRef.update("foto", imageUrl)
                .addOnSuccessListener(aVoid -> {
                    loadingDialog.dismiss(); // Ocultar el ícono de carga
                    Toast.makeText(this, "Imagen subida con éxito", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss(); // Ocultar el ícono de carga
                    Toast.makeText(this, "Error al actualizar la imagen en el perfil", Toast.LENGTH_SHORT).show();
                });
    }

    private boolean validateForm() {
        boolean isValid = true;

        String nombres = editTextNombres.getText().toString().trim();
        String apellidos = editTextApellidos.getText().toString().trim();
        //String telefono = editTextTelefono.getText().toString().trim();

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

        //if (telefono.isEmpty()) {
            //editTextTelefono.setError("El teléfono es obligatorio.");
           // isValid = false;
        //} else if (!telefono.matches("\\d{9}")) {
            //editTextTelefono.setError("El teléfono debe tener 9 dígitos y solo números.");
            //isValid = false;
        //} else {
            //editTextTelefono.setError(null);
        //}

        return isValid;
    }



}