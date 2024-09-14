package com.example.algeriandocumentreader.services;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;


import com.example.algeriandocumentreader.activities.InsertFormActivity;
import com.example.algeriandocumentreader.activities.ScanActivity;
import com.example.algeriandocumentreader.models.BacCredentials;
import com.example.algeriandocumentreader.views.OverlayView;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;


import androidx.camera.core.ImageProxy;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LiveTextRecognizer {

    private TextRecognizer recognizer;
    private OverlayView overlayView;
    private BacCredentials bacCredentials;
    ScanActivity scanActivity;
    public LiveTextRecognizer(OverlayView textViewOverlay, ScanActivity scanActivity) {
        this.overlayView = textViewOverlay;
        overlayView.setVisibility(View.VISIBLE);
        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        this.scanActivity=scanActivity;
    }

    public void processImage(ImageProxy imageProxy) {
        int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
        @SuppressLint("UnsafeOptInUsageError")
        InputImage image = InputImage.fromMediaImage(Objects.requireNonNull(imageProxy.getImage()),rotationDegrees );

        recognizer.process(image)
                .addOnSuccessListener(visionText -> {
                    int count = 0;
                    for (Text.TextBlock block : visionText.getTextBlocks()) {
                        String text = block.getText();
                        for (int i = 0; i < text.length(); i++) {
                            if (text.charAt(i) == '<') {
                                count++;
                            }
                        }
                    }
                    if (visionText.getText().contains("P<DZA")) {
                        if (count > 20) {
                            processAlgerianPassport(visionText.getText());
                        }
                    } else if (visionText.getText().contains("IDDZA")) {
                        if (count > 42) {
                            processAlgerianID(visionText.getText());
                        }
                    }
                    imageProxy.close();
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.e("TextRecognition", "Failed to recognize text", e);
                }).addOnCompleteListener(task -> {
                    imageProxy.close(); // Make sure to close the image after processing
                });
    }



    private void processAlgerianID(String text) {
        text = text.replace(" ", "");
        String[] split = text.split("IDDZA");
        String name = split[0];
        String iDMrz = split[1];
        Log.i("IDMrz", iDMrz);
        // Step 1: Remove all non-alphanumeric characters (keep only letters and numbers)
        String cleanedText = iDMrz.replaceAll("[^a-zA-Z0-9\\s]", "");

        // Step 2: Split the cleaned text by spaces and newlines
        String[] parts = cleanedText.split("\\s+");  // Split by any whitespace (spaces, newlines, etc.)

        // Step 3: Log or process each part
        String idNumber = parts[0].substring(0, parts[0].length() - 1);
        String datesCard = parts[1];



        // Pattern to match the format: 6 digits, a character (M/F), and another 6 digits
        String regexPattern = "(\\d{7})([MF])(\\d{6})";
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(datesCard);
        if (matcher.find()) {
            // Extract the first 6 digits
            String birthDate = matcher.group(1).substring(0, matcher.group(1).length() - 1);
            // Extract the gender character (M/F)
            String gender = matcher.group(2);
            // Extract the second 6 digits
            String expiredDate = matcher.group(3);
            Log.d("IDMrz", "ID Number: " + idNumber);
            // Log the extracted values
            Log.d("TextRecognition", "First  digits: " + birthDate);
            Log.d("TextRecognition", "Gender: " + gender);
            Log.d("TextRecognition", "Second 6 digits: " + expiredDate);
            bacCredentials = new BacCredentials(idNumber, birthDate, expiredDate);

            Intent intent = new Intent(scanActivity, InsertFormActivity.class);
            intent.putExtra("BacCredentials",  bacCredentials);
            scanActivity.startActivity(intent);
        }
    }

    private void processAlgerianPassport(String text) {
        text = text.replace(" ", "");
        String[] split = text.split("P<DZA");
        String name = split[0];
        String passportMrz = split[1];
        String cleanedText = passportMrz.replaceAll("[^a-zA-Z0-9\\s]", "");
        String[] parts = cleanedText.split("\\s+");
        Log.i("PassportMrz", passportMrz);
        String regexPattern = "(\\d{10})DZA(\\d{7})([MF])(\\d{6})";
        // Compile the pattern
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(cleanedText);
        // Check if a match is found
        if (matcher.find()) {
            // Extract the groups
            String documentId = matcher.group(1).substring(0, matcher.group(1).length() - 1);
            String birthDate = matcher.group(2).substring(0, matcher.group(2).length() - 1);
            String gender = matcher.group(3);
            String expiredDate = matcher.group(4);

            // Output the extracted values
            bacCredentials = new BacCredentials(documentId, birthDate, expiredDate);
            Intent intent = new Intent(scanActivity, InsertFormActivity.class);
            intent.putExtra("BacCredentials",  bacCredentials);
            scanActivity.startActivity(intent);
        }
    }


}

