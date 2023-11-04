package com.example.telecommunity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.telecommunity.adapter.NotificationAdapter;
import com.example.telecommunity.entity.NotificationItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class NotificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance(); // Firestore instance
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser(); // Current user

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        final List<NotificationItem> notificationList = new ArrayList<>();

        if (currentUser != null) {
            String userEmail = currentUser.getEmail();

            // Get the user code
            db.collection("usuarios")
                    .whereEqualTo("correo", userEmail)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot querySnapshot) {
                            if (!querySnapshot.isEmpty()) {
                                String userCode = String.valueOf(querySnapshot.getDocuments().get(0).getLong("codigo"));

                                // Fetch notifications of type 'donacion' and matching the user code
                                db.collection("notificaciones")
                                        .whereEqualTo("tipo", "donacion")
                                        .whereEqualTo("codigo", userCode)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (DocumentSnapshot document : task.getResult()) {
                                                        String title = document.getString("titulo");
                                                        String body = document.getString("cuerpo");

                                                        // Convert Firestore Timestamp to readable time string
                                                        String timeString = getTimeAgo(Objects.requireNonNull(document.getTimestamp("timestamp")));


                                                        notificationList.add(new NotificationItem(R.drawable.regalito, title, timeString, body));
                                                    }

                                                    notificationAdapter = new NotificationAdapter(notificationList, getActivity());
                                                    recyclerView.setAdapter(notificationAdapter);
                                                }
                                            }
                                        });
                            }
                        }
                    });
        }

        return view;
    }


    private String getTimeAgo(Timestamp timestamp) {
        long diff = new java.util.Date().getTime() - timestamp.toDate().getTime();
        if (diff < 60 * 1000) {  // menos de un minuto
            return "hace " + (diff / 1000) + " segundos";
        } else if (diff < 60 * 60 * 1000) {  // menos de una hora
            return "hace " + (diff / (60 * 1000)) + " minutos";
        } else if (diff < 24 * 60 * 60 * 1000) {  // menos de un día
            return "hace " + (diff / (60 * 60 * 1000)) + " horas";
        } else {
            return "hace " + (diff / (24 * 60 * 60 * 1000)) + " días";
        }
    }

}

