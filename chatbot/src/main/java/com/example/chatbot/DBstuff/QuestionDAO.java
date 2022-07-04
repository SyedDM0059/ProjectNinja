package com.example.chatbot.DBstuff;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class QuestionDAO {
    @Autowired
    private MongoTemplate mongoTemplate; //template. Its magic everything we need that is not extensive has alr been written

    public List<Question> findAll() {
        return mongoTemplate.findAll(Question.class);
    }

    public void saveAll(final List<Question> questions) {
        mongoTemplate.insertAll(questions);
    }
    public Question findById(final String questionId) {
        return mongoTemplate.findById(questionId, Question.class);
    }
}
