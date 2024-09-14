package com.example.algeriandocumentreader.tasks;

import android.content.Intent;

import android.nfc.tech.IsoDep;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.algeriandocumentreader.activities.ScanNfcActivity;
import com.example.algeriandocumentreader.activities.ScanResultActivity;

import com.example.algeriandocumentreader.models.EDocument;

import com.example.algeriandocumentreader.services.NfcDocumentReader;

import net.sf.scuba.smartcards.CardService;

import org.jmrtd.BACKeySpec;
import org.jmrtd.PassportService;

public class ReadTask extends AsyncTask<Void, Void, EDocument> {

    private IsoDep isoDep;
    private BACKeySpec bacKey;
    private ScanNfcActivity scanNfcActivity;
    private ProgressBar progressBar;
    private TextView nfcPromptMessage;

    public ReadTask(IsoDep isoDep, BACKeySpec bacKey, ScanNfcActivity scanNfcActivity, ProgressBar progressBar, TextView nfcPromptMessage) {
        this.isoDep = isoDep;
        this.bacKey = bacKey;
        this.scanNfcActivity = scanNfcActivity;
        this.progressBar = progressBar;
        this.nfcPromptMessage = nfcPromptMessage;
    }

    @Override
    protected EDocument doInBackground(Void... params) {
        try {
            CardService cardService = CardService.getInstance(isoDep);
            PassportService passportService = new PassportService(cardService, PassportService.NORMAL_MAX_TRANCEIVE_LENGTH, PassportService.DEFAULT_MAX_BLOCKSIZE, true, false);
            NfcDocumentReader documentReader = new NfcDocumentReader(passportService, bacKey);
            return documentReader.readDocument();
        } catch (Exception e) {
            Log.e("NFC", "Error reading NFC", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(EDocument eDocument) {
        progressBar.setVisibility(View.GONE);
        nfcPromptMessage.setText("Scan complete!");

        if (eDocument != null) {
            Intent intent = new Intent(scanNfcActivity, ScanResultActivity.class);
            intent.putExtra("eDocument", eDocument);

            scanNfcActivity.startActivity(intent);
        }
    }
}
