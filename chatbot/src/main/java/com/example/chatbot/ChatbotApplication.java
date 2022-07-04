package com.example.chatbot;

import com.example.chatbot.DBstuff.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


//@ComponentScan({"com"})
//@EntityScan("com.example.chatbot")
//@EnableMongoRepositories()
@SpringBootApplication
public class ChatbotApplication {

	ChildPlans childPlans = new ChildPlans();
	@Autowired
	private QuestionRepository questionRepository;
	public static void main(String[] args) {

		SpringApplication.run(ChatbotApplication.class, args);
	}

//	@Override
//	public void run(String... args) throws Exception {
//		//Question qn1 = new Question("1", "why?", "23/6/2022");
////		Question qn2 = new Question("2", "why sia?", "23/6/2022");
////		Question qn3 = new Question("3", "why must I?", "23/6/2022");
////		Question qn4 = new Question("4", "why why?", "23/6/2022");
////
////		questionRepository.save(qn1);
////		questionRepository.save(qn2);
////		questionRepository.save(qn3);
////		questionRepository.save(qn4);
//
////		System.out.println("***********");
////		System.out.println(childPlans.childPlans.getJSONArray("data").getJSONObject(0).getString("productDescription"));
////		List<Question> questions = questionRepository.findAll();
////		for(Question q : questions) {
////			System.out.println(q.toString());
////		}
//	}
}
