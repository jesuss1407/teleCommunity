package com.example.telecommunity.DelegadoGeneral;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.telecommunity.IniciarSesion;
import com.example.telecommunity.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class PerfilGeneralFragment extends Fragment {

    private TextView editTextNombres;
    private TextView editTextCorreo;
    private Spinner spinnerCondicion;

    private TextView editTextTelefono;
    private Button btnSave;
    private String documentId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //logout
        Button logout = view.findViewById(R.id.logout);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        editTextNombres = view.findViewById(R.id.editTextNombres);
        editTextCorreo = view.findViewById(R.id.editTextCorreo);
        editTextTelefono = view.findViewById(R.id.editTextTelefono);
        btnSave = view.findViewById(R.id.btnSave);

        spinnerCondicion = view.findViewById(R.id.spinnerCondicion);

        // Crea un ArrayAdapter con las opciones para el Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireActivity(),
                R.array.condiciones_array, android.R.layout.simple_spinner_item);

        // Especifica el layout a usar cuando aparece la lista de opciones
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Aplica el adaptador al Spinner
        spinnerCondicion.setAdapter(adapter);




        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios")
                .whereEqualTo("correo", FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                documentId = document.getId();
                                String nombres=document.getString("nombre")+" "+document.getString("apellido");
                                editTextNombres.setText(nombres);
                                editTextCorreo.setText(document.getString("correo"));
                                editTextTelefono.setText(document.getString("telefono"));
                                String condicionAlmacenada = document.getString("condicion") ; // Supongamos que este valor lo obtienes de Firestore o de otra fuente
                                int spinnerPosition = adapter.getPosition(condicionAlmacenada);
                                spinnerCondicion.setSelection(spinnerPosition);

                            }
                        } else {
                            Log.d(TAG, "No coincide");
                        }
                    }
                });


        // Botón para guardar los cambios
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Recoge los datos de los EditText y del Spinner
                String nombres = editTextNombres.getText().toString();
                String correo = editTextCorreo.getText().toString();
                String telefono = editTextTelefono.getText().toString();
                String condicion = spinnerCondicion.getSelectedItem().toString();

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("usuarios").document(documentId);

                // Actualiza los datos en Firestore
                docRef.update(
                        "nombre", nombres,
                        "correo", correo,
                        "telefono", telefono,
                        "condicion", condicion
                ).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Datos actualizados", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });








        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(getActivity(), IniciarSesion.class));
            }
        });



        return view;
    }
}