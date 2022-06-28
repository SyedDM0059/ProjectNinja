package com.example.chatbot;

import com.google.api.gax.rpc.ApiException;
import com.google.cloud.dialogflow.v2.*;
import com.google.common.collect.Maps;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class dialogflowService {
    // DialogFlow API Detect Intent sample with text inputs.
    public static QueryResult detectIntentTexts(
            String projectId, String texts, String sessionId, String languageCode)
            throws IOException, ApiException {
//        JSONObject queryResults = new JSONObject();
        // Instantiates a client
        QueryResult queryResult;
        try (SessionsClient sessionsClient = SessionsClient.create()) {
            // Set the session name using the sessionId (UUID) and projectID (my-project-id)
            SessionName session = SessionName.of(projectId, sessionId);
//            System.out.println("Session Path: " + session.toString());

            // Set the text (hello) and language code (en-US) for the query
            TextInput.Builder textInput =
                    TextInput.newBuilder().setText(texts).setLanguageCode(languageCode);

            // Build the query with the TextInput
            QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();

            // Performs the detect intent request
            DetectIntentResponse response = sessionsClient.detectIntent(session, queryInput);

            // Display the query result
            queryResult = response.getQueryResult();

        }
        return queryResult;
    }
}
