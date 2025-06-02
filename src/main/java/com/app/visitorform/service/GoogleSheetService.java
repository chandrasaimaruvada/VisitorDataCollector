package com.app.visitorform.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.util.*;

@Service
public class GoogleSheetService {

    private static final String APPLICATION_NAME = "Church QR Form";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private final String spreadsheetId;
    private final String credentialsPath;

    public GoogleSheetService(
            @Value("${google.sheet.spreadsheetId}") String spreadsheetId,
            @Value("${google.sheet.credentialsPath}") String credentialsPath) {
        this.spreadsheetId = spreadsheetId;
        this.credentialsPath = credentialsPath;
    }

    public void saveToSheet(Map<String, String> data) {
        try (FileInputStream serviceAccount = new FileInputStream(credentialsPath)) {
            GoogleCredential credential = GoogleCredential.fromStream(serviceAccount)
                    .createScoped(Collections.singleton("https://www.googleapis.com/auth/spreadsheets"));

            Sheets sheetsService = new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            List<Object> row = new ArrayList<>(data.values());
            ValueRange appendBody = new ValueRange().setValues(Collections.singletonList(row));

            sheetsService.spreadsheets().values()
                    .append(spreadsheetId, "Sheet1!A1", appendBody)
                    .setValueInputOption("RAW")
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<List<Object>> getAllData() {
        try (FileInputStream serviceAccount = new FileInputStream(credentialsPath)) {
            GoogleCredential credential = GoogleCredential.fromStream(serviceAccount)
                    .createScoped(Collections.singleton("https://www.googleapis.com/auth/spreadsheets.readonly"));

            Sheets sheetsService = new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            ValueRange response = sheetsService.spreadsheets().values()
                    .get(spreadsheetId, "Sheet1")
                    .execute();

            return response.getValues();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<String> getDistinctMonths() {
        List<List<Object>> allData = getAllData();
        if (allData.isEmpty()) return Collections.emptyList();

        // Skip header (assumed first row)
        List<String> months = new ArrayList<>();
        for (int i = 1; i < allData.size(); i++) {
            List<Object> row = allData.get(i);
            if (!row.isEmpty()) {
                String dateStr = row.get(0).toString();
                if (dateStr.length() >= 7) {  // yyyy-MM assumed
                    String month = dateStr.substring(0, 7);
                    if (!months.contains(month)) {
                        months.add(month);
                    }
                }
            }
        }
        Collections.sort(months); // ascending order
        return months;
    }

    public Map<String, Object> getMonthlyAnalytics(String month) {
        List<List<Object>> allData = getAllData();
        if (allData.isEmpty()) return Collections.emptyMap();

        Map<String, Integer> counts = new HashMap<>();

        for (int i = 1; i < allData.size(); i++) {
            List<Object> row = allData.get(i);
            if (row.size() < 2) continue; // skip incomplete rows

            String dateStr = row.get(0).toString();
            if (month != null && !month.equals("all") && !dateStr.startsWith(month)) continue;

            String category = row.get(1).toString();  // Adjust index for your analytics category
            counts.put(category, counts.getOrDefault(category, 0) + 1);
        }

        List<String> labels = new ArrayList<>(counts.keySet());
        List<Integer> data = new ArrayList<>();
        for (String key : labels) {
            data.add(counts.get(key));
        }

        Map<String, Object> chartData = new HashMap<>();
        chartData.put("labels", labels);
        chartData.put("data", data);
        return chartData;
    }


}
