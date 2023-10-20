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

import java.util.ArrayList;
import java.util.List;


public class NotificationFragment extends Fragment {
    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<NotificationItem> notificationList = new ArrayList<>();
        notificationList.add(new NotificationItem(R.drawable.gato, "Marcelo ha comentado", "Hace 5 minutos", "🔥Necesitamos barra para basket masculinoooo!!"));
        notificationList.add(new NotificationItem(R.drawable.gato, "Sara irá a un evento", "Hace 10 minutos", "¡Mira esta foto increíble que Sara ha compartido en su perfil! 😍"));
        notificationList.add(new NotificationItem(R.drawable.bell, "Angel participará una nueva actividad", "Hace 20 minutos", "¡Hay que Dotear!!!!😍"));

        notificationAdapter = new NotificationAdapter(notificationList, getActivity());
        recyclerView.setAdapter(notificationAdapter);

        return view;
    }
}