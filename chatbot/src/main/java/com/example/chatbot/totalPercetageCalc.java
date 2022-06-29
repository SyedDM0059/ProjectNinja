package com.example.chatbot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class totalPercetageCalc {

    Map<String, List<Integer>> userPercentages = new HashMap<String, List<Integer>>();

    public JSONObject totalPercentage(JSONObject payload) {

        String sessionID = payload.getString("session"); // To hold the unique sessionID from dialogflow
        String intent = payload.getJSONObject("queryResult").getJSONObject("intent").getString("displayName"); // To extract the intent of the user input from the dialogflow request
        JSONObject params = payload.getJSONObject("queryResult").getJSONObject("parameters");
        int percentage = Integer.parseInt(params.getString("percentage").substring(0, params.getString("percentage").lastIndexOf("%")));
        String user = sessionID.split("/")[sessionID.split("/").length-1];

        JSONObject fulfillment = new JSONObject();

        // Create unique context objects based on each sessionID
        JSONArray quoteContext = new JSONArray("[{\"name\":\"" + sessionID + "/contexts/quote\", \"lifespanCount\": 2}]");
        JSONArray turnoverContext = new JSONArray("[{\"name\":\"" + sessionID + "/contexts/turnover\", \"lifespanCount\": 2}]");
        JSONArray exposureContext = new JSONArray("[{\"name\":\"" + sessionID + "/contexts/exposure\", \"lifespanCount\": 2}]");
        JSONArray nameContext = new JSONArray("[{\"name\":\"" + sessionID + "/contexts/name\", \"lifespanCount\": 99}]");

        // If new user, create a list of their total percentages (Business Activity, Exposure)
        if (!userPercentages.containsKey(user)) {
            userPercentages.put(user, Arrays.asList(0, 0));
        }

        boolean BA = intent.equals("BA");
        int x = BA ? 0 : 1;

        userPercentages.get(user).set(x, userPercentages.get(user).get(x) + percentage);

        if (userPercentages.get(user).get(x) == 100) {
            fulfillment.put("outputContexts", BA ? turnoverContext : nameContext);
            fulfillment.put("fulfillmentText", BA ? "What is your company's annual turnover?" : "Great, thanks! Now we just need to know a bit more about you. What is your name?");
        } else {
            fulfillment.put("outputContexts", BA ? quoteContext : exposureContext);
            if (userPercentages.get(user).get(x) < 100) {
                fulfillment.put("fulfillmentText", BA ? "What are your other business activities? (total business activity must add up to 100%)" :
                        "What is your other market exposure? (total exposure must add up to 100%)");
            } else {
                fulfillment.put("fulfillmentText", BA ? "Total business activity cannot be more than 100%, please state your business activities again" :
                        "Total exposure cannot be more than 100%, please state your exposure again");
                userPercentages.get(user).set(x, 0);
            }
            System.out.println(fulfillment.toString());
        }
        return fulfillment;
    }
}
