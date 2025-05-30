package com.churchvisitor.app.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
}
