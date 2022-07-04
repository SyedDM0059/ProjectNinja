package com.example.chatbot.DBstuff;

import org.springframework.data.annotation.Id;

public class Question {
    @Id
    public String id;
    public String question;
    public String created;

    public Question() {

    }

    public Question(String id, String question, String created) {
        super();
        this.id = id;
        this.question = question;
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id='" + id + '\'' +
                ", question='" + question + '\'' +
                ", created='" + created + '\'' +
                '}';
    }
//    public String id;
//    public String name;
//    public Boolean auto;
//    public String condition;
//    public Arrays conditionalFollowupEvents;
//    public Arrays conditionalResponses;
//    public Arrays context;
//    public Boolean endInteraction;
//    public Arrays events;
//    public Boolean fallbackintent;
//    public Boolean liveAgentHandoff;
//    public String parentId;
//    public Arrays followUpIntents;
//    public Integer priority;
//    public Arrays responses
}
