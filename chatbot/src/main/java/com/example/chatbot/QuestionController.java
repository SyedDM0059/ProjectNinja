package com.example.chatbot;

import com.example.chatbot.DBstuff.Question;
import com.example.chatbot.DBstuff.QuestionDAO;
import com.example.chatbot.DBstuff.QuestionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

// make endpoints here for interactions
@RestController
public class QuestionController {
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private QuestionDAO questionDAO;

    @PostMapping("/question") // first api to create many questions
    public void addQuestions(@RequestBody final List<Question> questions){
        questionDAO.saveAll(questions);
    }

    @PostMapping("/hugs")
    @ResponseBody
    public String hugs(@RequestBody String payload) throws IOException { // second api webhook to dialogflow
        JSONObject json = new JSONObject(payload);
        String sessionID = json.getString("session");
        String intent = json.getJSONObject("queryResult").getJSONObject("intent").getString("displayName");
        JSONObject fulfillment = new JSONObject();
        JSONObject qn = new JSONObject(questionDAO.findById("1"));
//        System.out.print(qn);
        ObjectMapper mapper = new ObjectMapper();
        String qne = qn.toString();
        JsonNode jsonNode = mapper.readTree(qne);
        String result = jsonNode.get("question").asText();
        if (intent.equals("test")) {
            fulfillment.put("fulfillmentText", result);
        }
        return fulfillment.toString();
    }

    @GetMapping("/question") // third api to fetch questions from db
    public List<Question> findQuestions() {
        return questionDAO.findAll();
    }

    @GetMapping("/question/{questionId}") // fourth api to fetch particular question
    public Question findQuestion(@PathVariable final String questionId) {
        return questionDAO.findById(questionId);
    }

    @PatchMapping("/question/{questionId}") // fifth api for partial Updates
    public void partialUpdateQuestion(@PathVariable final String questionId, @RequestBody final Question question) throws Exception {
        for (final Field field: Question.class.getDeclaredFields()) {
            final String fieldName = field.getName();
            if(fieldName.equals("id")){
                continue;
            }
            final Method getter = Question.class.getDeclaredMethod("get" + StringUtils.capitalize(fieldName));
            final Object fieldValue = getter.invoke(question);

            if(Objects.nonNull(fieldValue)){
                questionRepository.partialUpdate(questionId, fieldName, fieldValue);
            }
        }
    }

    // All dialogflow fulfillment requests come here
    DialogflowFulfillment dialogflowFulfillment = new DialogflowFulfillment();
    @PostMapping("/fulfillment")
    @ResponseBody
    public String test(@RequestBody String payload){
        JSONObject json = new JSONObject(payload); // Hold the request sent from dialogflow in this JSONObject
        return dialogflowFulfillment.fulfillment(json).toString();
    }


}
