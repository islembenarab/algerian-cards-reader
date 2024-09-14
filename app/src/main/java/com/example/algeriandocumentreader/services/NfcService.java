package com.example.algeriandocumentreader.services;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.view.View;
import android.widget.Toast;

import com.example.algeriandocumentreader.activities.ScanNfcActivity;
import com.example.algeriandocumentreader.models.BacCredentials;
import com.example.algeriandocumentreader.tasks.ReadTask;

import org.jmrtd.BACKey;

public class NfcService {
    private ScanNfcActivity scanNfcActivity;
    private BacCredentials bacCredentials;
    private NfcAdapter nfcAdapter;
    public NfcService(ScanNfcActivity scanNfcActivity, BacCredentials bacCredentials) {
        this.scanNfcActivity = scanNfcActivity;
        this.bacCredentials = bacCredentials;
        nfcAdapter = NfcAdapter.getDefaultAdapter(scanNfcActivity);
    }
    public void checkNfcAdapterSupport() {
        if (nfcAdapter == null) {
            // NFC is not supported
            scanNfcActivity.finish();
            Toast.makeText(scanNfcActivity, "NFC is not supported", Toast.LENGTH_SHORT).show();
        }

    }
    // Check if NFC is enabled
    public void checkNfcEnabled() {
        if (!nfcAdapter.isEnabled()) {
            // NFC is not enabled
            Toast.makeText(scanNfcActivity, "Please enable NFC to use this feature.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(android.provider.Settings.ACTION_NFC_SETTINGS);
            scanNfcActivity.startActivity(intent);
        }
    }
    public void disableForegroundDispatch() {
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(scanNfcActivity);
        }
    }
    public void enableForegroundDispatch(PendingIntent pendingIntent) {
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(scanNfcActivity, pendingIntent, null, null);
        }
    }

    public void handleNfcIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            IsoDep isoDep = IsoDep.get(tag);
            if (isoDep != null) {
                // Start background task to read the NFC data
                scanNfcActivity.progressBar.setVisibility(View.VISIBLE);
                scanNfcActivity.nfcPromptMessage.setText("Scanning... Please hold still.");
                new ReadTask(isoDep,
                        new BACKey(bacCredentials.getDocumentNumber(), bacCredentials.getBirthDate(), bacCredentials.getExpiredDate())
                        ,scanNfcActivity
                        ,scanNfcActivity.progressBar
                        ,scanNfcActivity.nfcPromptMessage).execute();
            }
        }
    }
}
