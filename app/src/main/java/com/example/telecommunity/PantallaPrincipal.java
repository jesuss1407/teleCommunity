package com.example.telecommunity;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class PantallaPrincipal extends AppCompatActivity {

    RecyclerView recyclerView;
    List<DataClass> dataList;
    MyAdapter adapter;
    DataClass androidData;
    SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal);
        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.search);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(PantallaPrincipal.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        dataList = new ArrayList<>();
        androidData = new DataClass("Basquet", R.string.app_name, "Inicio: 11/09", R.drawable.basquet);
        dataList.add(androidData);
        androidData = new DataClass("Futbol", R.string.app_name, "Inicio: 11/10", R.drawable.futsal_damas);
        dataList.add(androidData);
        androidData = new DataClass("Voley", R.string.app_name, "Inicio: 08/10", R.drawable.basquet);
        dataList.add(androidData);
        androidData = new DataClass("Ajedrez", R.string.app_name, "Inicio: 01/09", R.drawable.futsal_damas);
        dataList.add(androidData);
        androidData = new DataClass("Camera", R.string.app_name, "Inicio: 11/09", R.drawable.basquet);
        dataList.add(androidData);
        androidData = new DataClass("Camera", R.string.app_name, "Java", R.drawable.basquet);
        dataList.add(androidData);


        adapter = new MyAdapter(PantallaPrincipal.this, dataList);
        recyclerView.setAdapter(adapter);
    }
    private void searchList(String text){
        List<DataClass> dataSearchList = new ArrayList<>();
        for (DataClass data : dataList){
            if (data.getDataTitle().toLowerCase().contains(text.toLowerCase())) {
                dataSearchList.add(data);
            }
        }
        if (dataSearchList.isEmpty()){
            Toast.makeText(this, "Not Found", Toast.LENGTH_SHORT).show();
        } else {
            adapter.setSearchList(dataSearchList);
        }
    }
}