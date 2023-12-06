package com.example.shohscan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private Button scanButton;
    private Button displayDataButton; // Кнопка для перехода к DisplayDataActivity
    private TextView resultTextView;
    private TextView sumTextView;
    private StringBuilder scannedDataBuilder;
    private double totalSum;
    private ArrayList<ScannedData> scannedDataList; // Список сканированных данных

    // Инициализация Firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация Firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        // Получение ссылки на вашу базу данных, "scanned_data" - это имя вашей таблицы
        databaseReference = firebaseDatabase.getReference("scanned_data");

        scanButton = findViewById(R.id.scanButton);
        displayDataButton = findViewById(R.id.displayDataButton); // Находим кнопку для отображения данных
        resultTextView = findViewById(R.id.resultTextView);
        sumTextView = findViewById(R.id.sumTextView);

        scannedDataBuilder = new StringBuilder();
        totalSum = 0;
        scannedDataList = new ArrayList<>();

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Инициировать сканирование QR-кода
                new IntentIntegrator(MainActivity.this).initiateScan();
            }
        });

        // Добавляем обработчик для кнопки отображения данных
        displayDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DisplayDataActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null && result.getContents() != null) {
            String scannedData = result.getContents();
            double scannedValue = parseScannedValue(scannedData);

            if (scannedValue != -1) {
                // Создать объект ScannedData с датой
                String currentTime = getCurrentTime();
                String currentDate = getCurrentDate();
                ScannedData scannedDataItem = new ScannedData(scannedValue, currentTime, currentDate);
                scannedDataList.add(scannedDataItem);

                // Пересчитать общую сумму
                totalSum += scannedValue;

                // Обновить интерфейс
                scannedDataBuilder.append("\n").append(scannedData);
                resultTextView.setText("\nРезультат сканирования:" + scannedDataBuilder.toString() );
                sumTextView.setText("Общая сумма: " + totalSum);
                resultTextView.append("\nСканированное время: " + currentTime);

                // Получить уникальный ключ для новой записи
                String key = databaseReference.push().getKey();

                // Сохранить данные в Firebase Realtime Database
                databaseReference.child(key).setValue(scannedDataItem); // Сохранение в базе данных

            } else {
                resultTextView.setText("\nИ QR-код ай дига чияй ЧУМО  ракамдорша биёв   ");
            }
        }
    }

    // Метод для извлечения числового значения из сканированного QR-кода
    private double parseScannedValue(String scannedData) {
        try {
            return Double.parseDouble(scannedData);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // Метод для получения текущего времени в формате "час:минута:секунда"
    private String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    // Метод для получения текущей даты в формате "год-месяц-день"
    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy", Locale.getDefault());
        return dateFormat.format(new Date());
    }
}
