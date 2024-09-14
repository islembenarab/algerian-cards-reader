package com.example.algeriandocumentreader.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.algeriandocumentreader.R;

public class MainActivity extends AppCompatActivity {
    Button scanButton;

    Button insertFormButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scanButton = findViewById(R.id.scan_document);
        insertFormButton = findViewById(R.id.insert_form);
        scanButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ScanActivity.class);
            startActivity(intent);
            finish();
        });
        insertFormButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, InsertFormActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
