package com.example.chatbot;

import com.fasterxml.jackson.annotation.JsonAlias;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

public class fulfillment {

    //    Map<String, List<Integer>> userPercentages = new HashMap<String, List<Integer>>();
    JSONObject userInfo = new JSONObject();

    RestTemplate restTemplate = new RestTemplate();

    public JSONObject totalPercentage(JSONObject payload) {

        String sessionID = payload.getString("session"); // To hold the unique sessionID from dialogflow
        String intent = payload.getJSONObject("queryResult").getJSONObject("intent").getString("displayName"); // To extract the intent of the user input from the dialogflow request
        JSONObject params = payload.getJSONObject("queryResult").getJSONObject("parameters");
        String user = sessionID.split("/")[sessionID.split("/").length-1];

        JSONObject fulfillment = new JSONObject();

        if (intent.equals("BA") || intent.equals("Exposure")) {

            int percentage = Integer.parseInt(params.getString("percentage").substring(0, params.getString("percentage").lastIndexOf("%")));

            // Create unique context objects based on each sessionID
            JSONArray quoteContext = new JSONArray("[{\"name\":\"" + sessionID + "/contexts/quote\", \"lifespanCount\": 2}]");
            JSONArray turnoverContext = new JSONArray("[{\"name\":\"" + sessionID + "/contexts/turnover\", \"lifespanCount\": 2}]");
            JSONArray exposureContext = new JSONArray("[{\"name\":\"" + sessionID + "/contexts/exposure\", \"lifespanCount\": 2}]");
            JSONArray nameContext = new JSONArray("[{\"name\":\"" + sessionID + "/contexts/name\", \"lifespanCount\": 99}]");

            // If new user, create a list of their total percentages (Business Activity, Exposure)
            if (!userInfo.has(user)) {
                userInfo.put(user, new JSONObject("{\"BA\":0,\"Exposure\":0, \"BizActs\":\"\", \"Exp\":\"\"}"));
            }

            boolean BA = intent.equals("BA");
            int x = BA ? 0 : 1;

            userInfo.getJSONObject(user).put(intent, userInfo.getJSONObject(user).getInt(intent) + percentage);
            userInfo.getJSONObject(user).put(BA ? "BizActs" : "Exp", userInfo.getJSONObject(user).getString(BA ? "BizActs" : "Exp")
                    + params.getString(intent) + ":" + params.getString("percentage") + "\n");

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
        } else if (intent.equals("quote-start")) {

            // Handle the bearer token thing
            HttpHeaders headers = new HttpHeaders();

            //headers.setBearerAuth(getBearerAuth())
            headers.setBearerAuth("eyJhbGciOiJIUzUxMiJ9.eyJhdWQiOiJodHRwczovL2RjbXAtZGV2LmRpc2NvdmVybWFya2V0LmNvbSIsInN1YiI6IjYwZGM3MmFmYmI3MDc2NzU3MmQ1NTZjZSIsImNvZGUiOiIiLCJwZXJtaXNzaW9ucyI6WyJ0ZW5hbnRzLmNyZWF0ZS5hbnkiLCJ0ZW5hbnRzLnVwZGF0ZS5hbnkiLCJwbGFucy5jcmVhdGUuYW55IiwicGxhbnMucmVhZC5hbnkiLCJwbGFucy51cGRhdGUuYW55IiwicGxhbnMuZGVsZXRlLmFueSIsInVzZXJzLmNyZWF0ZS5hbnkiLCJiZW5lZml0cy5jcmVhdGUuYW55IiwidXNlcnMudXBkYXRlLmFueSIsInRlbmFudHMucmVhZC5hbnkiLCJ1c2Vycy5yZWFkLmFueSIsImJlbmVmaXRzLnJlYWQuYW55IiwidXNlcnMuZGVsZXRlLmFueSIsInRlbmFudHMuZGVsZXRlLmFueSIsInRhcmlmZnMuY3JlYXRlLmFueSIsImJlbmVmaXRzLmRlbGV0ZS5hbnkiLCJ0YXJpZmZzLnJlYWQuYW55IiwidGFyaWZmcy5kZWxldGUuYW55IiwidGFyaWZmcy51cGRhdGUuYW55IiwiaW5zdXJlcnMuY3JlYXRlLmFueSIsImluc3VyZXJzLmRlbGV0ZS5hbnkiLCJpbnN1cmVycy5yZWFkLmFueSIsImluc3VyZXJzLnVwZGF0ZS5hbnkiLCJiZW5lZml0cy51cGRhdGUuYW55IiwiZGVwZW5kYW50cy5jcmVhdGUuYW55IiwiZGVwZW5kYW50cy5yZWFkLmFueSIsImRlcGVuZGFudHMudXBkYXRlLmFueSIsImRlcGVuZGFudHMuZGVsZXRlLmFueSIsInJvbGVzLnJlYWQuYW55Iiwicm9sZXMuY3JlYXRlLmFueSIsInJvbGVzLnVwZGF0ZS5hbnkiLCJyb2xlcy5kZWxldGUuYW55IiwicGVybWlzc2lvbnMuY3JlYXRlLmFueSIsInBlcm1pc3Npb25zLnJlYWQuYW55IiwicGVybWlzc2lvbnMudXBkYXRlLmFueSIsInBlcm1pc3Npb25zLmRlbGV0ZS5hbnkiLCJhdWRpdC5yZWFkLmFueSIsInBvbGljeS5yZWFkLnRlbmFudCIsInBvbGljeS5yZWFkLnRlbmFudCIsInBvbGljeS5yZWFkLnRlbmFudCIsInBvbGljeS5yZWFkLnRlbmFudCIsInBvbGljeS5yZWFkLnRlbmFudCIsInBvbGljeS5yZWFkLnRlbmFudCIsInBvbGljeS5yZWFkLnRlbmFudCIsImNsYWltcy5yZWFkLnRlbmFudCIsImRlcGVuZGFudHMuY3JlYXRlLmFueSIsImRlcGVuZGFudHMucmVhZC5hbnkiLCJkZXBlbmRhbnRzLnVwZGF0ZS5hbnkiLCJkZXBlbmRhbnRzLmRlbGV0ZS5hbnkiLCJwcm9wb3NhbHMuY3JlYXRlLnRlbmFudCIsInRlbmFudHMucmVhZC5vd24iLCJwcm9wb3NhbHMudXBkYXRlLnRlbmFudCIsInByb3Bvc2Fscy5yZWFkLnRlbmFudCIsInByb3Bvc2Fscy5kZWxldGUudGVuYW50IiwiY3VzdG9tZXJzLmNyZWF0ZS50ZW5hbnQiLCJjdXN0b21lcnMucmVhZC50ZW5hbnQiLCJjdXN0b21lcnMudXBkYXRlLnRlbmFudCIsImN1c3RvbWVycy5kZWxldGUudGVuYW50IiwiY3VzdG9tZXJzLnJlYWQub3duIiwic2Nhbi5jcmVhdGUudGVuYW50Iiwic2Nhbi5yZWFkLnRlbmFudCIsInVzZXJzLnJlYWQub3duIiwidXNlcnMudXBkYXRlLm93biIsInVzZXJzLmNyZWF0ZS5hbnkiLCJjdXN0b21lcnMuY3JlYXRlLnRlbmFudCIsImN1c3RvbWVycy51cGRhdGUudGVuYW50IiwiY3VzdG9tZXJzLnJlYWQudGVuYW50IiwicHJvcG9zYWxzLnVwZGF0ZS50ZW5hbnQiLCJwcm9wb3NhbHMuZGVsZXRlLnRlbmFudCIsInByb3Bvc2Fscy5jcmVhdGUudGVuYW50IiwiY3VzdG9tZXJzLmRlbGV0ZS50ZW5hbnQiLCJwcm9wb3NhbHMucmVhZC50ZW5hbnQiLCJwb2xpY3kucmVhZC50ZW5hbnQiLCJ1c2Vycy51cGRhdGUub3duIiwidXNlcnMucmVhZC5vd24iLCJjbGFpbXMucmVhZC50ZW5hbnQiXSwicm9sZXMiOlsiUk8wMDAwMDAxIiwiUk8wMDAwMDI2IiwiUk8wMDAwMDA3Il0sInRlbmFudElkIjoiNjBjYjFhOGQyOTNiZWE0OTY4MjRkM2UxIiwiaXNzIjoiaHR0cHM6Ly9kY21wLWRldi5kaXNjb3Zlcm1hcmtldC5jb20iLCJ0ZW5hbnRDb2RlIjoiVEUwMDAwMDAxIiwiZXhwIjoxNjU2NjUxODI2LCJpYXQiOjE2NTY2NDczMjYsImVtYWlsIjoidG9iZWRlbGV0ZWRAZGlzY292ZXJtYXJrZXQuY29tIn0.cgVobjLm4vuifvvUeTA8RERbMbdqkvKIu5uTQeReIrkmNgPdDD5DqDXib2AE5acuoJqDZE9BLttje8VKI1gArQ");
            HttpEntity<String> httpEntity = new HttpEntity<>("", headers);
            ResponseEntity<String> response = restTemplate.exchange("https://product-service-dev.discovermarket.com/v2/riskdetailinfos/619c9d2e4b0253465a797fd1/620db4ca930b8e4c589482b5",
                    HttpMethod.GET, httpEntity, String.class);
            JSONObject riskDetails = new JSONObject(response.getBody());
            System.out.println(riskDetails.toString());

        }
        return fulfillment;
    }
}
