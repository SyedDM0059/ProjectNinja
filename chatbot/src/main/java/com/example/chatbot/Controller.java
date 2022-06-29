package com.example.chatbot;

import com.google.cloud.dialogflow.v2.QueryResult;
import com.twilio.type.PhoneNumber;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;


@RestController
public class Controller {

    dialogflowService dialogflowService = new dialogflowService();
    totalPercetageCalc totalPercetageCalc = new totalPercetageCalc();


    @PostMapping("/test")
    @ResponseBody
    public String theRealTHing(@RequestParam MultiValueMap<String,String> payload) throws IOException {

        String text = payload.get("Body").get(0);
        PhoneNumber userNumber = new PhoneNumber("whatsapp:"+payload.get("From"));

        // Try making session id with userNumber
        QueryResult df = dialogflowService.detectIntentTexts("focused-house-354313", text, userNumber.toString(), "en-US");

        return df.getFulfillmentText();
    }

    // Look into putting this into Google cloud functions
    @PostMapping
    @ResponseBody
    public String test(@RequestBody String payload){

        JSONObject json = new JSONObject(payload); // Hold the request sent from dialogflow in this JSONObject
        return totalPercetageCalc.totalPercentage(json).toString();
    }
}
