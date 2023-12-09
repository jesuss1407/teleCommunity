package com.example.telecommunity.DelegadoGeneral;



import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class EventoDetalleFragment extends Fragment {

    private TextView textViewTituloEvento;
    private TextView textViewFechaEvento;
    private ImageView imageViewDonacion;
    private Context context;
    private Button deleteButton;
    private Button confirmButton;
    private EditText monto;
    private AlertDialog loadingDialog;

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
            builder.setPositiveButton("Confirmar", (dialog, which) -> confirmarDonacion());
            builder.setNegativeButton("Cancelar", null);
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
        return amount.matches(regex);
    }


    private void confirmarDonacion() {
        Donacion donacion = obtenerDonacionDesdeBundle();
        if (donacion != null) {
            String codigoUsuario = donacion.getCodigo();
            String montoDonacion = monto.getText().toString();

            Map<String, Object> confirmacion = new HashMap<>();
            confirmacion.put("fecha", FieldValue.serverTimestamp());
            confirmacion.put("monto", montoDonacion);

            FirebaseFirestore.getInstance().collection("donacionconf")
                    .document(codigoUsuario)
                    .set(confirmacion)
                    .addOnSuccessListener(aVoid -> {
                        eliminarDonacion();
                        Toast.makeText(getActivity(), "Donación confirmada", Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    })
                    .addOnFailureListener(e -> {
                        loadingDialog.dismiss();
                    });
        }
    }



    private void mostrarConfirmacionEliminacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Confirmar eliminación");
        builder.setMessage("¿Estás seguro de querer eliminar esta donación?");
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

                            String condicion = document.getString("condicion");
                            String nombre = document.getString("nombre");
                            String apellido = document.getString("apellido");
                            String correo= document.getString("correo");


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
