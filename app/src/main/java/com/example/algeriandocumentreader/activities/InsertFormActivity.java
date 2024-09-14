package com.example.algeriandocumentreader.activities;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.algeriandocumentreader.R;
import com.example.algeriandocumentreader.models.BacCredentials;
import com.example.algeriandocumentreader.utils.DateUtil;
import com.example.algeriandocumentreader.utils.Utils;

public class InsertFormActivity extends AppCompatActivity {
    Button submitButton ;
    EditText documentNumberInput ;
    EditText birthDateInput ;
    EditText expiredDateInput ;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_form);
        submitButton = findViewById(R.id.submit_button);
        documentNumberInput = findViewById(R.id.document_number_input);
        birthDateInput = findViewById(R.id.birth_date_input);
        expiredDateInput = findViewById(R.id.expired_date_input);
        // Retrieve BacCredentials from Intent
        BacCredentials bacCredentials = (BacCredentials) getIntent().getSerializableExtra("BacCredentials");
        if (bacCredentials != null) {
            // Populate the fields with the retrieved data
            documentNumberInput.setText(bacCredentials.getDocumentNumber());
            birthDateInput.setText(DateUtil.convertFromMrzDate(bacCredentials.getBirthDate()));
            expiredDateInput.setText(DateUtil.convertFromMrzDate(bacCredentials.getExpiredDate()));
        }
        submitButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, ScanNfcActivity.class);
            intent.putExtra("bacCredentials", new BacCredentials(
                    documentNumberInput.getText().toString(),
                    Utils.reverseToDateCode(birthDateInput.getText().toString()),
                    Utils.reverseToDateCode(expiredDateInput.getText().toString())
            ));
            startActivity(intent);
        });
    }
}
