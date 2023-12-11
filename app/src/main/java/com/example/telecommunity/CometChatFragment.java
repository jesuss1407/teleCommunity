package com.example.telecommunity;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.core.GroupMembersRequest;
import com.cometchat.chat.core.MessagesRequest;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.BaseMessage;
import com.cometchat.chat.models.GroupMember;
import com.cometchat.chat.models.TextMessage;
import com.example.telecommunity.adapter.ChatAdapter;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class CometChatFragment extends Fragment {

    private String groupID;
    private RecyclerView chatRecyclerView;
    private EditText messageEditText;
    private ImageButton sendButton;
    private ChatAdapter chatAdapter;
    private List<TextMessage> messages;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comet_chat, container, false);

        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        messageEditText = view.findViewById(R.id.messageEditText);
        sendButton = view.findViewById(R.id.sendButton);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Bundle bundle = getArguments();
        if (bundle != null) {
            groupID = bundle.getString("GROUP_ID");
        }

        if (groupID != null) {
            loadGroupMessages(groupID);
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar una animación de rotación
                final RotateAnimation rotateAnim = new RotateAnimation(0.0f, 360.0f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnim.setDuration(500); // Duración en milisegundos
                rotateAnim.setRepeatCount(1); // Cuántas veces se repite la animación
                sendButton.startAnimation(rotateAnim);

                // Envía tu mensaje
                sendMessage();

                // La animación se detendrá automáticamente, pero si necesitas hacer algo
                // después de que se envíe el mensaje, coloca ese código aquí.
            }
        });

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        String currentUserID = sharedPreferences.getString("currentUserID", "");

        messages = new ArrayList<>();
        chatAdapter = new ChatAdapter(messages, currentUserID);
        chatRecyclerView.setAdapter(chatAdapter);


        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        CometChat.addMessageListener("UNIQUE_LISTENER_ID", new CometChat.MessageListener() {
            @Override
            public void onTextMessageReceived(TextMessage textMessage) {
                // Aquí se agrega el mensaje recibido al RecyclerView
                messages.add(textMessage);
                chatAdapter.notifyDataSetChanged();
                // Podrías querer desplazarte al último mensaje aquí
            }

            // ... (otros métodos de MessageListener si son necesarios)
        });
    }


    @Override
    public void onPause() {
        super.onPause();
        CometChat.removeMessageListener("UNIQUE_LISTENER_ID");
    }

    private void loadGroupMessages(String groupID) {
        MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                .setGUID(groupID)
                .setLimit(30)
                .build();

        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> baseMessages) {
                for (BaseMessage baseMessage : baseMessages) {
                    if (baseMessage instanceof TextMessage) {
                        messages.add((TextMessage) baseMessage);
                    }
                    // Aquí puedes añadir más instancias de mensajes si las necesitas, como MediaMessage, CustomMessage, etc.
                }
                chatAdapter.notifyDataSetChanged();
                // Desplazarse al último mensaje si es necesario
                if (!messages.isEmpty()) {
                    chatRecyclerView.scrollToPosition(messages.size() - 1);
                }
            }

            @Override
            public void onError(CometChatException e) {
                Log.e("CometChat", "Error al cargar mensajes: " + e.getMessage());
            }
        });
    }

    private void sendMessage() {
        String messageText = messageEditText.getText().toString().trim();
        if (!messageText.isEmpty()) {
            // Crea y envía un mensaje de texto utilizando CometChat
            TextMessage textMessage = new TextMessage(groupID, messageText, CometChatConstants.RECEIVER_TYPE_GROUP);

            CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
                @Override
                public void onSuccess(TextMessage textMessage) {
                    Log.d("CometChat", "Mensaje enviado exitosamente");
                    messages.add(textMessage); // Añade el mensaje a la lista
                    chatAdapter.notifyItemInserted(messages.size() - 1); // Notifica al adaptador que se insertó un nuevo ítem
                    messageEditText.setText(""); // Limpia el EditText
                    chatRecyclerView.scrollToPosition(messages.size() - 1); // Desplaza al último mensaje


                    getEventTitleAndSendNotifications(groupID);

                }

                @Override
                public void onError(CometChatException e) {
                    Log.e("CometChat", "Error al enviar mensaje: " + e.getMessage());
                }
            });
        }
    }

    private void getEventTitleAndSendNotifications(String groupID) {
        Task<DocumentSnapshot> documentSnapshotTask = db.collection("publicaciones").document(groupID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String eventTitle = documentSnapshot.getString("nombre");
                // Una vez que tenemos el nombre del evento, procedemos a obtener los códigos de usuario
                getUserCodesAndSendNotifications(groupID, eventTitle);
            } else {
                Log.e("Firestore", "El documento del evento no existe");
            }
        }).addOnFailureListener(e -> Log.e("Firestore", "Error al obtener el título del evento", e));
    }

    private void getUserCodesAndSendNotifications(String groupID, String eventTitle) {
        GroupMembersRequest groupMembersRequest = new GroupMembersRequest.GroupMembersRequestBuilder(groupID).setLimit(30).build();

        groupMembersRequest.fetchNext(new CometChat.CallbackListener<List<GroupMember>>() {
            @Override
            public void onSuccess(List<GroupMember> list) {
                List<String> uids = new ArrayList<>();
                for (GroupMember member : list) {
                    uids.add(member.getUid());
                }
                Log.d("CometChatFragment", "UIDs of group members: " + uids);
                // Aquí tendrías que convertir los UIDs a códigos de usuario si es necesario.
                sendNotificationToGroup(uids, "Hay nuevos mensajes en el chat del evento " + eventTitle, "chat", eventTitle);
            }

            @Override
            public void onError(CometChatException e) {
                Log.e("CometChatFragment", "Group member fetch failed with exception: " + e.getMessage());
            }
        });
    }



    private void sendNotificationToGroup(List<String> userCodes, String message, String tipo, String titulo) {
        Log.d("CometChatFragment", "Enviando notificaciones al grupo. Usuarios: " + userCodes.size());

        // Obtener la fecha y hora actual para el timestamp de la notificación
        for (String userCode : userCodes) {
            // Ahora verifica si ya existe una notificación para ese usuario y grupo
            checkAndCreateNotification(userCode, message, tipo, titulo);
        }
    }


    private void checkAndCreateNotification(String userCode, String message, String tipo, String titulo) {
        Log.d("CometChatFragment", "Verificando si ya existe una notificación. Usuario: " + userCode);

        db.collection("notificaciones")
                .whereEqualTo("codigo", userCode)
                .whereEqualTo("titulo", titulo)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().isEmpty()) {
                        // No existe una notificación previa, crea una nueva
                        createNotification(Integer.parseInt(userCode), message, tipo, titulo);
                    } else {
                        Log.d("Firestore", "Ya existe una notificación para el usuario con código: " + userCode);
                    }
                });
    }

    private void createNotification(Integer userCode, String message, String tipo, String titulo) {
        Log.d("CometChatFragment", "Creando notificación. Usuario: " + userCode);

        Map<String, Object> notification = new HashMap<>();
        // Convertir el código de usuario a String antes de guardarlo
        notification.put("codigo", userCode.toString());
        notification.put("cuerpo", message);
        // Utilizar FieldValue.serverTimestamp() para obtener la marca de tiempo del servidor
        notification.put("timestamp", FieldValue.serverTimestamp());
        notification.put("tipo", tipo);
        notification.put("titulo", titulo);

        db.collection("notificaciones")
                .add(notification)
                .addOnSuccessListener(documentReference -> Log.d("Firestore", "Notificación guardada con ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.e("Firestore", "Error al guardar la notificación", e));
    }

}