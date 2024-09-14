package com.example.algeriandocumentreader.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Utils {
    public static String convertToDate(String input) {
        // Extract the year, month, and day
        String yearPart = input.substring(0, 2);  // "00"
        String monthPart = input.substring(2, 4); // "08"
        String dayPart = input.substring(4, 6);   // "30"

        // Convert year (assuming "00" means 2000)
        int year = Integer.parseInt(yearPart);
        if (year < 50) {  // You can adjust the threshold as needed
            year += 2000;
        } else {
            year += 1900;
        }

        // Construct a LocalDate object
        LocalDate date = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            date = LocalDate.of(year, Integer.parseInt(monthPart), Integer.parseInt(dayPart));
        }

        // Format the date as "yyyy-MM-dd"
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return date.format(formatter);
        }
        return input;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String reverseToDateCode(String date) {
        // Parse the input string to a LocalDate
        DateTimeFormatter inputFormatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        }
        LocalDate localDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            localDate = LocalDate.parse(date, inputFormatter);
        }

        // Extract the last two digits of the year
        assert localDate != null;
        String yearPart = String.format("%02d", localDate.getYear() % 100);
        String monthPart = String.format("%02d", localDate.getMonthValue());
        String dayPart = String.format("%02d", localDate.getDayOfMonth());

        // Combine the year, month, and day parts into the final format
        return yearPart + monthPart + dayPart;
    }
}
