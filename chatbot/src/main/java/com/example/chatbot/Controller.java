package com.example.chatbot;

import com.google.cloud.dialogflow.v2.QueryResult;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
public class Controller {

    dialogflowService dialogflowService = new dialogflowService();
    // To keep track of users
    int totalBA = 0;
//    JSONObject BA = new JSONObject();
    String BAstring = new String();
    int totalExp = 0;
//    JSONObject exp = new JSONObject();
    String expstring = new String();


    @PostMapping("/test")
    @ResponseBody
    public String theRealTHing(@RequestParam MultiValueMap<String,String> payload) throws IOException {

        String text = payload.get("Body").get(0);
//        PhoneNumber userNumber = new PhoneNumber("whatsapp:"+jsonPayload.getString("From"));
//        PhoneNumber DCMNumber = new PhoneNumber("whatsapp:+17406392618");

        // Try making session id with userNumber
        QueryResult df = dialogflowService.detectIntentTexts("focused-house-354313", text, "1234", "en-US");
//        Body messageBody = new Body.Builder(fulfillment).build();
//        Message message = new Message.Builder().body(messageBody).build();
//        return new MessagingResponse.Builder().message(message).build().toXml();
        return df.getFulfillmentText();
    }

    // Look into putting this into Google cloud functions
    @PostMapping
    @ResponseBody
    public String test(@RequestBody String payload){

        JSONObject temp = new JSONObject();
        JSONObject json = new JSONObject(payload); // Hold the request sent from dialogflow in this JSONObject
        JSONObject fulfillment = new JSONObject(); // The response object to be sent back to dialogflow
        JSONObject BAtemp;
        JSONObject exptemp;
        JSONArray context = new JSONArray();


        String sessionID = json.getString("session"); // To hold the unique sessionID from dialogflow
        String intent = json.getJSONObject("queryResult").getJSONObject("intent").getString("displayName"); // To extract the intent of the user input from the dialogflow request

        if (intent.equals("BA")) {

            BAtemp = json.getJSONObject("queryResult").getJSONObject("parameters");
            totalBA += Integer.parseInt(BAtemp.getString("percentage").substring(0, BAtemp.getString("percentage").lastIndexOf("%")));
//            BA.put(BAtemp.getString("BA"), BAtemp.getString("percentage"));
            BAstring += BAtemp.getString("BA") + ": " + BAtemp.getString("percentage") + "\n";
            if (totalBA < 100){
                temp.put("name", sessionID+"/contexts/quote");
                temp.put("lifespanCount", 2);
                context.put(temp);
                fulfillment.put("outputContexts", context);
                fulfillment.put("fulfillmentText", "What are your other business activities? (total business activity must add up to 100%)\n\n"+BAstring);
            } else if ( totalBA > 100) {
                temp.put("name", sessionID+"/contexts/quote");
                temp.put("lifespanCount", 2);
                context.put(temp);
                fulfillment.put("outputContexts", context);
                fulfillment.put("fulfillmentText", "Total business activity cannot be more than 100%, please state your business activities again");
                totalBA = 0;
                BAstring = new String();
//                BA = new JSONObject();
            } else {
                temp.put("name", sessionID+"/contexts/turnover");
                temp.put("lifespanCount", 2);
                context.put(temp);
                fulfillment.put("outputContexts", context);
                totalBA = 0;
                BAstring = new String();
                fulfillment.put("fulfillmentText", "What is your company's annual turnover?");
            }
        } else if (intent.equals("Exposure")) {

            exptemp = json.getJSONObject("queryResult").getJSONObject("parameters");
            totalExp += Integer.parseInt(exptemp.getString("percentage").substring(0, exptemp.getString("percentage").lastIndexOf("%")));
            expstring += exptemp.getString("Exposure") + ": " + exptemp.getString("percentage") + "\n";
            if (totalExp < 100){
                temp.put("name", sessionID+"/contexts/exposure");
                temp.put("lifespanCount", 2);
                context.put(temp);
                fulfillment.put("outputContexts", context);
                fulfillment.put("fulfillmentText", "What is your other market exposure? (total exposure must add up to 100%)\n\n"+expstring);
            } else if ( totalExp > 100) {
                temp.put("name", sessionID+"/contexts/exposure");
                temp.put("lifespanCount", 2);
                context.put(temp);
                fulfillment.put("outputContexts", context);
                fulfillment.put("fulfillmentText", "Total exposure cannot be more than 100%, please state your exposure again");
                totalExp = 0;
                expstring = new String();
            } else {
                temp.put("name", sessionID+"/contexts/name");
                temp.put("lifespanCount", 99);
                context.put(temp);
                fulfillment.put("outputContexts", context);
                totalExp = 0;
                fulfillment.put("fulfillmentText", "Great, thanks! Now we just need to know a bit more about you. What is your name?");
                expstring = new String();
            }
        }
        return fulfillment.toString();
    }
}
