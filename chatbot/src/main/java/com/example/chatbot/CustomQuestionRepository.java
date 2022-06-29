package com.example.chatbot;

public interface CustomQuestionRepository { //partial
    void partialUpdate(final String questionId, final String fieldName, final Object fieldValue);
}
