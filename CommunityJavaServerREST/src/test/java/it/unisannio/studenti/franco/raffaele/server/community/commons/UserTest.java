package it.unisannio.studenti.franco.raffaele.server.community.commons;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserTest {

	static User u;
	static User u1;
	static User u2;
	static Question question;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		u = new User("raffaele", "123".toCharArray());
		u1 = new User("gerardo", "123".toCharArray());
		u2 = new User("gerardo", "123".toCharArray());
		
		question = new Question("Title", "Text");
		Question question1 = new Question("Title1", "Text1");
		Response response = new Response("Text");
		Response response1 = new Response("Text1");
		
		User user = new User("Raffaele", "1234".toCharArray());
		
		user.setUsername("Gerardo");
		user.setPassword("password".toCharArray());
		user.addQuestion(question);
		user.addResponse(response);
		
		LinkedHashMap<String, Question> questions = new LinkedHashMap<String, Question>();
		questions.put(question.getTitle(), question);
		questions.put(question1.getTitle(), question1);
		
		LinkedHashSet<Response> responses = new LinkedHashSet<Response>();
		responses.add(response);
		responses.add(response1);
		
		user.setQuestions(questions);
		user.setResponses(responses);
		
		System.out.println(user.getQuestions());
		System.out.println(user.getResponses());
		
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		System.out.println(u.toString());
		u.removeQuestion(question);
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test() {
		assertFalse(u.getPassword().length != u.getPassword().length);
	}

	@Test
	void test1() {
		assertTrue(u2.getUsername().equals(u1.getUsername()));
	}
	
	@Test 
	void groupedAssertions() { 
		assertAll("users", 
				() -> assertEquals("gerardo", u2.getUsername()), 
				() -> assertEquals(3, u2.getPassword().length)); 
		}
}
