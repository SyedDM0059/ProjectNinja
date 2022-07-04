package com.example.chatbot.DBstuff;

public interface CustomQuestionRepository { //partial
    void partialUpdate(final String questionId, final String fieldName, final Object fieldValue);
}
