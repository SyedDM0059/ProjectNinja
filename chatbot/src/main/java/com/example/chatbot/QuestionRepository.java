package com.example.chatbot;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuestionRepository extends MongoRepository<Question, String>, CustomQuestionRepository { // second must follow what id is, int or string

}
