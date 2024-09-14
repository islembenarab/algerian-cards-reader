package com.example.algeriandocumentreader.activities;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;


import com.example.algeriandocumentreader.R;
import com.example.algeriandocumentreader.adapters.PersonDetailsAdapter;
import com.example.algeriandocumentreader.models.EDocument;
import com.example.algeriandocumentreader.utils.ImageUtil;

import java.util.LinkedHashMap;
import java.util.Map;

public class ScanResultActivity extends AppCompatActivity {
//    ImageView imageView = findViewById(R.id.face_image);
    Button button ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result_activity); // The layout that displays the scanned document data
        button = findViewById(R.id.scanAgain);
        EDocument eDocument = (EDocument) getIntent().getSerializableExtra("eDocument");
        GridView gridView = findViewById(R.id.grid_view);
        assert eDocument != null;
        PersonDetailsAdapter adapter = new PersonDetailsAdapter(this, eDocument.getDocumentDetailsMap());
        gridView.setAdapter(adapter);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(ScanResultActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

}
