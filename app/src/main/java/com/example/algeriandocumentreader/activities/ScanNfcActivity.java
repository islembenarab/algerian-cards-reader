package com.example.algeriandocumentreader.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.example.algeriandocumentreader.R;
import com.example.algeriandocumentreader.models.BacCredentials;
import com.example.algeriandocumentreader.services.NfcService;

import pl.droidsonroids.gif.GifImageView;

public class ScanNfcActivity extends AppCompatActivity {
    NfcService nfcService;
    GifImageView gifImageView;
    public ProgressBar progressBar; // Progress bar to show scanning status
    public TextView nfcPromptMessage;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.algeriandocumentreader.R.layout.activity_scan_nfc);
        BacCredentials bacCredentials = (BacCredentials) getIntent().getSerializableExtra("bacCredentials");
        gifImageView = findViewById(R.id.scan_nfc_gif);
        progressBar = findViewById(R.id.nfcProgressBar);
        nfcPromptMessage = findViewById(R.id.nfcPromptMessage);
        nfcService = new NfcService(this, bacCredentials);
        nfcService.checkNfcAdapterSupport();
        nfcService.checkNfcEnabled();
        pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        nfcService.enableForegroundDispatch(pendingIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcService.disableForegroundDispatch();
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        nfcService.handleNfcIntent(intent);
    }
}
