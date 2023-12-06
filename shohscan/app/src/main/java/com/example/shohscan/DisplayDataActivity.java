package com.example.shohscan;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class DisplayDataActivity extends AppCompatActivity {
    private ListView listView;
    private DatabaseReference databaseReference;
    private List<ScannedData> scannedDataList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_data);

        listView = findViewById(R.id.listView);
        scannedDataList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        // Инициализация Firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        // Получение ссылки на вашу базу данных, "scanned_data" - это имя вашей таблицы
        databaseReference = firebaseDatabase.getReference("scanned_data");

        // Слушатель для получения данных из Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                scannedDataList.clear();
                adapter.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ScannedData scannedData = snapshot.getValue(ScannedData.class);
                    if (scannedData != null) {
                        scannedDataList.add(scannedData);
                        adapter.add("Нархаш:- " + scannedData.getValue() + " SOM, Вакт: " + scannedData.getTime()+"--"+ scannedData.getDate());
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Обработка ошибок при чтении из базы данных
            }
        });

        listView.setAdapter(adapter);
    }
}
