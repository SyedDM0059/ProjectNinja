package com.example.chatbot;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class DialogflowFulfillment {
    // To hold all related info for each unique user
    JSONObject userInfo = new JSONObject();
    //List of business activities that are marked as excluded
    List<String> excludedBizActivities = new ArrayList<>();
    RestTemplate restTemplate = new RestTemplate();

    public JSONObject fulfillment(JSONObject payload) {

        // Parse the payload to retrieve the relevant information
        String sessionID = payload.getString("session"); // To hold the unique sessionID from dialogflow
        String intent = payload.getJSONObject("queryResult").getJSONObject("intent").getString("displayName"); // To extract the intent of the user input from the dialogflow request
        JSONObject params = payload.getJSONObject("queryResult").getJSONObject("parameters");
        String user = sessionID.split("/")[sessionID.split("/").length-1];

        // Create unique context objects based on each sessionID
        JSONArray restartContext = new JSONArray("[{\"name\":\"" + sessionID + "/contexts/restart\", \"lifespanCount\": 2}]");
        JSONArray quoteContext = new JSONArray("[{\"name\":\"" + sessionID + "/contexts/quote\", \"lifespanCount\": 2}]");
        JSONArray turnoverContext = new JSONArray("[{\"name\":\"" + sessionID + "/contexts/turnover\", \"lifespanCount\": 2}]");
        JSONArray exposureContext = new JSONArray("[{\"name\":\"" + sessionID + "/contexts/exposure\", \"lifespanCount\": 2}]");
        JSONArray nameContext = new JSONArray("[{\"name\":\"" + sessionID + "/contexts/name\", \"lifespanCount\": 99}]");

        // To be returned to dialogflow to respond to user. Refer to https://cloud.google.com/dialogflow/es/docs/reference/rest/v2/WebhookResponse to view the correct format to respond to dialogflow
        JSONObject fulfillment = new JSONObject();

        switch (intent) {
            case "BA":
            case "Exposure":

                // Check if the given business activity is one of the excluded ones
                if (excludedBizActivities.contains(params.getString(intent))) {
                    fulfillment.put("outputContexts", restartContext);
                    fulfillment.put("fulfillmentText", "Your application has been rejected.\nIn case of any questions or inquiries, please contact us:\nEmail: no-reply@brokerabc.com\nPhone: 8888 8888.\n\nTo start over, type \"restart\". Good bye!");
                    return fulfillment;
                }

                int percentage = Integer.parseInt(params.getString("percentage").substring(0, params.getString("percentage").lastIndexOf("%")));

                // If new user, add mappings for Business Activity, Exposure, BizActs (String of users input business activities), Exp (String of users input exposure)
                if (!userInfo.has(user)) {
                    userInfo.put(user, new JSONObject("{\"BA\":0,\"Exposure\":0, \"BizActs\":\"\", \"Exp\":\"\"}"));
                }

                boolean BA = intent.equals("BA");

                // Update user percentage and input data
                userInfo.getJSONObject(user).put(intent, userInfo.getJSONObject(user).getInt(intent) + percentage);
                userInfo.getJSONObject(user).put(BA ? "BizActs" : "Exp", userInfo.getJSONObject(user).getString(BA ? "BizActs" : "Exp")
                        + params.getString(intent) + ": " + params.getString("percentage") + "\n");

                // Validation of input data total business activity or exposure adds up to 100%
                if (userInfo.getJSONObject(user).getInt(intent) == 100) {

                    fulfillment.put("outputContexts", BA ? turnoverContext : nameContext);
                    fulfillment.put("fulfillmentText", BA ? "What is your company's annual turnover?" : "Great, thanks! Now we just need to know a bit more about you. What is your name?");
                    userInfo.put(user, new JSONObject("{\"BA\":0,\"Exposure\":0, \"BizActs\":\"\", \"Exp\":\"\"}"));
                } else {

                    fulfillment.put("outputContexts", BA ? quoteContext : exposureContext);
                    if (userInfo.getJSONObject(user).getInt(intent) < 100) {

                        fulfillment.put("fulfillmentText", BA ? "What are your other business activities? (total business activity must add up to 100%)\n\n" + userInfo.getJSONObject(user).getString("BizActs") :
                                "What is your other market exposure? (total exposure must add up to 100%)\n\n" + userInfo.getJSONObject(user).getString("Exp"));
                    } else {

                        fulfillment.put("fulfillmentText", BA ? "Total business activity cannot be more than 100%, please state your business activities again" :
                                "Total exposure cannot be more than 100%, please state your exposure again");
                        userInfo.put(user, new JSONObject("{\"BA\":0,\"Exposure\":0, \"BizActs\":\"\", \"Exp\":\"\"}"));
                    }
                }
                break;
            case "quote-start":
            case "Welcome - yes":
            case "reset":

                // Reset the list of excluded business activities so that it is refreshed whenever the conversation restarts
                excludedBizActivities = new ArrayList<>();
                StringBuilder BizActs = new StringBuilder("Please enter your business activity and its corresponding percentage (eg. Retail: 20%/ B0001: 30%):\n\n");

                // Handle the bearer token thing
                HttpHeaders headers = new HttpHeaders();

                //headers.setBearerAuth(getBearerAuth())
                headers.setBearerAuth("eyJhbGciOiJIUzUxMiJ9.eyJhdWQiOiJodHRwczovL2RjbXAtZGV2LmRpc2NvdmVybWFya2V0LmNvbSIsInN1YiI6IjYwZGM3MmFmYmI3MDc2NzU3MmQ1NTZjZSIsImNvZGUiOiIiLCJwZXJtaXNzaW9ucyI6WyJ0ZW5hbnRzLmNyZWF0ZS5hbnkiLCJ0ZW5hbnRzLnVwZGF0ZS5hbnkiLCJwbGFucy5jcmVhdGUuYW55IiwicGxhbnMucmVhZC5hbnkiLCJwbGFucy51cGRhdGUuYW55IiwicGxhbnMuZGVsZXRlLmFueSIsInVzZXJzLmNyZWF0ZS5hbnkiLCJiZW5lZml0cy5jcmVhdGUuYW55IiwidXNlcnMudXBkYXRlLmFueSIsInRlbmFudHMucmVhZC5hbnkiLCJ1c2Vycy5yZWFkLmFueSIsImJlbmVmaXRzLnJlYWQuYW55IiwidXNlcnMuZGVsZXRlLmFueSIsInRlbmFudHMuZGVsZXRlLmFueSIsInRhcmlmZnMuY3JlYXRlLmFueSIsImJlbmVmaXRzLmRlbGV0ZS5hbnkiLCJ0YXJpZmZzLnJlYWQuYW55IiwidGFyaWZmcy5kZWxldGUuYW55IiwidGFyaWZmcy51cGRhdGUuYW55IiwiaW5zdXJlcnMuY3JlYXRlLmFueSIsImluc3VyZXJzLmRlbGV0ZS5hbnkiLCJpbnN1cmVycy5yZWFkLmFueSIsImluc3VyZXJzLnVwZGF0ZS5hbnkiLCJiZW5lZml0cy51cGRhdGUuYW55IiwiZGVwZW5kYW50cy5jcmVhdGUuYW55IiwiZGVwZW5kYW50cy5yZWFkLmFueSIsImRlcGVuZGFudHMudXBkYXRlLmFueSIsImRlcGVuZGFudHMuZGVsZXRlLmFueSIsInJvbGVzLnJlYWQuYW55Iiwicm9sZXMuY3JlYXRlLmFueSIsInJvbGVzLnVwZGF0ZS5hbnkiLCJyb2xlcy5kZWxldGUuYW55IiwicGVybWlzc2lvbnMuY3JlYXRlLmFueSIsInBlcm1pc3Npb25zLnJlYWQuYW55IiwicGVybWlzc2lvbnMudXBkYXRlLmFueSIsInBlcm1pc3Npb25zLmRlbGV0ZS5hbnkiLCJhdWRpdC5yZWFkLmFueSIsInBvbGljeS5yZWFkLnRlbmFudCIsInBvbGljeS5yZWFkLnRlbmFudCIsInBvbGljeS5yZWFkLnRlbmFudCIsInBvbGljeS5yZWFkLnRlbmFudCIsInBvbGljeS5yZWFkLnRlbmFudCIsInBvbGljeS5yZWFkLnRlbmFudCIsInBvbGljeS5yZWFkLnRlbmFudCIsImNsYWltcy5yZWFkLnRlbmFudCIsImRlcGVuZGFudHMuY3JlYXRlLmFueSIsImRlcGVuZGFudHMucmVhZC5hbnkiLCJkZXBlbmRhbnRzLnVwZGF0ZS5hbnkiLCJkZXBlbmRhbnRzLmRlbGV0ZS5hbnkiLCJwcm9wb3NhbHMuY3JlYXRlLnRlbmFudCIsInRlbmFudHMucmVhZC5vd24iLCJwcm9wb3NhbHMudXBkYXRlLnRlbmFudCIsInByb3Bvc2Fscy5yZWFkLnRlbmFudCIsInByb3Bvc2Fscy5kZWxldGUudGVuYW50IiwiY3VzdG9tZXJzLmNyZWF0ZS50ZW5hbnQiLCJjdXN0b21lcnMucmVhZC50ZW5hbnQiLCJjdXN0b21lcnMudXBkYXRlLnRlbmFudCIsImN1c3RvbWVycy5kZWxldGUudGVuYW50IiwiY3VzdG9tZXJzLnJlYWQub3duIiwic2Nhbi5jcmVhdGUudGVuYW50Iiwic2Nhbi5yZWFkLnRlbmFudCIsInVzZXJzLnJlYWQub3duIiwidXNlcnMudXBkYXRlLm93biIsInVzZXJzLmNyZWF0ZS5hbnkiLCJjdXN0b21lcnMuY3JlYXRlLnRlbmFudCIsImN1c3RvbWVycy51cGRhdGUudGVuYW50IiwiY3VzdG9tZXJzLnJlYWQudGVuYW50IiwicHJvcG9zYWxzLnVwZGF0ZS50ZW5hbnQiLCJwcm9wb3NhbHMuZGVsZXRlLnRlbmFudCIsInByb3Bvc2Fscy5jcmVhdGUudGVuYW50IiwiY3VzdG9tZXJzLmRlbGV0ZS50ZW5hbnQiLCJwcm9wb3NhbHMucmVhZC50ZW5hbnQiLCJwb2xpY3kucmVhZC50ZW5hbnQiLCJ1c2Vycy51cGRhdGUub3duIiwidXNlcnMucmVhZC5vd24iLCJjbGFpbXMucmVhZC50ZW5hbnQiXSwicm9sZXMiOlsiUk8wMDAwMDAxIiwiUk8wMDAwMDI2IiwiUk8wMDAwMDA3Il0sInRlbmFudElkIjoiNjBjYjFhOGQyOTNiZWE0OTY4MjRkM2UxIiwiaXNzIjoiaHR0cHM6Ly9kY21wLWRldi5kaXNjb3Zlcm1hcmtldC5jb20iLCJ0ZW5hbnRDb2RlIjoiVEUwMDAwMDAxIiwiZXhwIjoxNjU2OTI0NTkyLCJpYXQiOjE2NTY5MjAwOTIsImVtYWlsIjoidG9iZWRlbGV0ZWRAZGlzY292ZXJtYXJrZXQuY29tIn0.tN_p8sAYk3pU1_jQZHY2bh9s6TYmhr5Ol8phmSKg8YWGJlt3WUTIZDjivdPIdvfBoH63PtHvdpdX9Dv6YTLtoQ");
                HttpEntity<String> httpEntity = new HttpEntity<>("", headers);

                // Retrieve the risk details from the DCM risk details API
                ResponseEntity<String> response = restTemplate.exchange("https://product-service-dev.discovermarket.com/v2/riskdetailinfos/619c9d2e4b0253465a797fd1/620db4ca930b8e4c589482b5",
                        HttpMethod.GET, httpEntity, String.class);

                JSONObject riskDetails = new JSONObject(response.getBody());
                JSONArray activitiesList = riskDetails.getJSONObject("data").getJSONObject("customerCategory").getJSONArray("objectTypes").getJSONObject(0).getJSONArray("riskDetailDataGroups").getJSONObject(0).getJSONArray("dataDetailAttributes").getJSONObject(0).getJSONArray("options");

                // To generate the String of business activities to display to user, as well as to update the list of excluded business activites
                for (int i = 1; i < activitiesList.length(); i++) {

                    JSONObject activity = activitiesList.getJSONObject(i);
                    BizActs.append(activity.getString("key")).append(": ").append(activity.getString("value")).append("\n");

                    if (activity.getString("category").equals("excluded")) {

                        excludedBizActivities.add(activity.getString("value"));
                    }
                }

                fulfillment.put("outputContexts", quoteContext);
                fulfillment.put("fulfillmentText", BizActs.toString());
                break;
            case "ET":
                int turnover = params.getJSONObject("unit-currency").getInt("amount");

                if (turnover <= 0) {
                    fulfillment.put("outputContexts", turnoverContext);
                    fulfillment.put("fulfillmentText", "Annual turnover must be a positive non-zero number, please re-enter");
                }
                break;
        }
        return fulfillment;
    }
}
