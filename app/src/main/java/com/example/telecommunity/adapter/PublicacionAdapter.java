package com.example.telecommunity.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.Group;
import com.example.telecommunity.DetallePublicacionActivity;
import com.example.telecommunity.R;
import com.example.telecommunity.entity.Publicaciondto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;


public class PublicacionAdapter extends RecyclerView.Adapter<PublicacionAdapter.PublicacionViewHolder> {
    private static final String TAG = "PublicacionAdapter";
    private List<Publicaciondto> publicaciones;
    private Context context;


    public PublicacionAdapter(List<Publicaciondto> publicaciones, Context context) {
        this.publicaciones = publicaciones;
        this.context = context;
    }

    @NonNull
    @Override
    public PublicacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_publicacion, parent, false);
        return new PublicacionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PublicacionViewHolder holder, int position) {
        Publicaciondto publicacion = publicaciones.get(position);
        holder.bind(publicacion);

        // OnClickListener para abrir la actividad de detalle de publicación
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetallePublicacionActivity.class);
            intent.putExtra("publicacionId", publicacion.getId());
            intent.putExtra("latitud", publicacion.getLatitud());
            intent.putExtra("longitud", publicacion.getLongitud());

            Log.d(TAG, "Publicacion ID: " + publicacion.getId());
            context.startActivity(intent);
        });

        holder.btnComentar.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetallePublicacionActivity.class);
            intent.putExtra("publicacionId", publicacion.getId());
            intent.putExtra("latitud", publicacion.getLatitud());
            intent.putExtra("longitud", publicacion.getLongitud());
            Log.d(TAG, "Publicacion ID: " + publicacion.getId());

            context.startActivity(intent);
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (user != null && user.getEmail() != null) {
            String userEmail = user.getEmail();

            // Consulta para obtener el código del usuario
            db.collection("usuarios").whereEqualTo("correo", userEmail).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot userDocument = queryDocumentSnapshots.getDocuments().get(0);
                            Long codigo = userDocument.getLong("codigo");

                            if (codigo != null) {
                                String userCodigoAsString = codigo.toString();

                                // Verificar si el usuario ya está unido al evento
                                db.collection("usuarios")
                                        .document(userCodigoAsString)
                                        .collection("eventos")
                                        .document(publicacion.getId())
                                        .get()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful() && task.getResult().exists()) {
                                                // El usuario ya se unió al evento
                                                holder.btnUnirse.setText("Unido");
                                                holder.btnUnirse.setEnabled(false);
                                            } else {
                                                // El usuario no se ha unido al evento
                                                holder.btnUnirse.setText("Unirse");
                                                holder.btnUnirse.setEnabled(true);

                                                // Establecer el OnClickListener para unirse al evento
                                                holder.btnUnirse.setOnClickListener(v -> {
                                                    showJoinEventDialog(position, userCodigoAsString);
                                                });
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(e -> Log.w(TAG, "Error obteniendo el documento del usuario", e));
        }
    }


    private void showJoinEventDialog(int position, String userCodigo) {
        String eventName = publicaciones.get(position).getNombre();
        String eventId = publicaciones.get(position).getId();
        final String[] selectedRole = new String[1]; // Array para almacenar la selección temporalmente

        final CharSequence[] options = {"Barra", "Participante"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Elige tu rol en el evento " + eventName);

        builder.setSingleChoiceItems(options, -1, (dialogInterface, i) -> {
            selectedRole[0] = options[i].toString(); // Guarda la selección del usuario
        });

        // Botón de confirmar
        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            if (selectedRole[0] != null) {
                // Guardar la información sólo si se ha hecho una selección
                saveParticipantInfo(eventId, userCodigo, selectedRole[0], eventName);
            } else {
                Toast.makeText(context, "Debes seleccionar un rol para unirte al evento.", Toast.LENGTH_LONG).show();
            }
        });

        // Botón de cancelar
        builder.setNegativeButton("Cancelar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void saveParticipantInfo(String eventId, String userCodigo, String role, String eventName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> eventToJoin = new HashMap<>();
        eventToJoin.put("idEvento", eventId);
        eventToJoin.put("rol", role);

        db.collection("usuarios")
                .document(userCodigo)
                .collection("eventos")
                .document(eventId)
                .set(eventToJoin)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User successfully joined the event as " + role);
                    notifyDataSetChanged(); // Actualizar la interfaz si es necesario
                    // Mostrar un Toast aquí
                    Toast.makeText(context, "Te has unido exitosamente al evento como " + role, Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error joining event", e);
                    // También podrías querer mostrar un Toast si ocurre un error
                    Toast.makeText(context, "Error al unirse al evento. Intenta de nuevo.", Toast.LENGTH_LONG).show();
                });

        // Si tienes lógica para unirte o crear un grupo de chat, asegúrate de llamarla aquí también si es necesario
        joinOrCreateChatGroup(eventId, eventName);
    }

    private void joinOrCreateChatGroup(String eventId, String eventName) {
        String GUID = eventId; // Utiliza el ID del evento como identificador del grupo en CometChat

        CometChat.joinGroup(GUID, CometChatConstants.GROUP_TYPE_PUBLIC, "", new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group joinedGroup) {
                Log.d(TAG, "Unido al grupo de chat exitosamente: " + joinedGroup.toString());
            }

            @Override
            public void onError(CometChatException e) {
                if (e.getCode().equals("ERR_GROUP_NOT_FOUND")) {
                    createGroupForEvent(GUID, eventName);
                }
            }
        });
    }

    private void createGroupForEvent(String GUID, String eventName) {
        Group group = new Group(GUID, eventName, CometChatConstants.GROUP_TYPE_PUBLIC, "");
        CometChat.createGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group group) {
                joinOrCreateChatGroup(GUID, eventName); // Intenta unirse de nuevo después de crear el grupo
            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "Error al crear el grupo: " + e.getMessage());
            }
        });
    }


    @Override
    public int getItemCount() {
        return publicaciones.size();
    }

    class PublicacionViewHolder extends RecyclerView.ViewHolder {
        TextView postNombre;
        TextView postHora;
        TextView postFecha;
        TextView postUbicacion;
        TextView postActivity;
        TextView postContenido;
        ImageView userPhoto;
        ImageView photoSpace;
        Button btnComentar;
        Button btnUnirse;
        ImageView btnMaps;

        public PublicacionViewHolder(@NonNull View itemView) {
            super(itemView);
            postNombre = itemView.findViewById(R.id.post_nombre);
            postHora = itemView.findViewById(R.id.post_hora);
            postFecha = itemView.findViewById(R.id.post_fecha);
            postUbicacion = itemView.findViewById(R.id.post_ubicacion);
            postActivity = itemView.findViewById(R.id.post_activity);
            postContenido = itemView.findViewById(R.id.post_contenido);
            userPhoto = itemView.findViewById(R.id.user_photo);
            photoSpace = itemView.findViewById(R.id.photo_space);
            btnComentar = itemView.findViewById(R.id.button_comentar);
            btnUnirse = itemView.findViewById(R.id.button_unirse);
            btnMaps = itemView.findViewById(R.id.btnMaps);

            btnMaps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Publicaciondto publicacion = publicaciones.get(position);

                    double latitudDestino = publicacion.getLatitud();
                    double longitudDestino = publicacion.getLongitud();

                    // Crear una URI para la dirección de Google Maps con las coordenadas de destino
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitudDestino + "," + longitudDestino);

                    // Crear un intent para abrir Google Maps con la ruta
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps"); // Forzar el uso de la aplicación de Google Maps

                    // Verificar si Google Maps está instalado en el dispositivo
                    if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(mapIntent);
                    } else {
                        // Si Google Maps no está instalado, puedes mostrar un mensaje de error
                        Toast.makeText(context, "Google Maps no está instalado en este dispositivo.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        public void bind(Publicaciondto publicacion) {
            String nombreCompleto = publicacion.getNombreUsuario() + " " + publicacion.getApellidoUsuario();
            postNombre.setText(nombreCompleto);

            long timestamp = publicacion.getHoraCreacion();
            Date date = new Date(timestamp);

            // Formatear la hora
            SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");
            sdfHora.setTimeZone(TimeZone.getDefault());
            String hora = sdfHora.format(date);
            postHora.setText(hora);

            // Formatear la fecha
            SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yy");
            sdfFecha.setTimeZone(TimeZone.getDefault());
            String fecha = sdfFecha.format(date);
            postFecha.setText(fecha);

            // Aquí debes mostrar el nombre de la ubicación
            postUbicacion.setText("");

            postActivity.setText(publicacion.getNombre());
            postContenido.setText(publicacion.getContenido());

            // Cargar la imagen del perfil del usuario
            Glide.with(context)
                    .load(publicacion.getFotoUsuario())
                    .into(userPhoto);

            // Cargar la imagen de la publicación si existe
            if (publicacion.getUrlImagen() != null && !publicacion.getUrlImagen().isEmpty()) {
                photoSpace.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(publicacion.getUrlImagen())
                        .into(photoSpace);
            } else {
                photoSpace.setVisibility(View.GONE);
            }
            // Mostrar el nombre de la ubicación
            postUbicacion.setText(publicacion.getNombreUbicacion());
        }




    }
}
