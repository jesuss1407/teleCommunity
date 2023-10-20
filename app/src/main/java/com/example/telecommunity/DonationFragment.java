package com.example.telecommunity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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


public class DonationFragment extends Fragment {
    private static final int PICK_IMAGE = 1;
    private ImageView imageViewCaptura;
    private Button buttonSubirCaptura;
    private Button buttonEnviarCaptura;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donation, container, false);

        // Inicialización de las vistas
        imageViewCaptura = view.findViewById(R.id.imageViewCaptura);
        buttonSubirCaptura = view.findViewById(R.id.buttonSubirCaptura);
        buttonEnviarCaptura = view.findViewById(R.id.buttonEnviarCaptura);

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

                // Habilitar el botón para enviar la captura
                buttonEnviarCaptura.setEnabled(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}