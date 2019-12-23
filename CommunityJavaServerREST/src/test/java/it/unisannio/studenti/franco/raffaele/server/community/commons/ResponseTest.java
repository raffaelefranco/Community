package it.unisannio.studenti.franco.raffaele.server.community.commons;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ResponseTest {

	static Response r;
	static Response r1;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		r = new Response("text");
		r1 = new Response("text");
		r.setScore(10);
		r1.setScore(5);

		r.decrementScore(3);
		r1.incrementScore(2);
		System.out.println(r.getScore());
		System.out.println(r.getText());
		System.out.println(r.getUser());
		System.out.println(r.getQuestion());

		Question question = new Question("Title", "Text");

		r1.setQuestion(question);
		System.out.println(r.toString());

		Response response = new Response();

		response.setText("Text");
		response.setUser("Raffaele");

	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		System.out.println(r.toString());
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test() {
		r.setUser("Raffaele");
		assertTrue(r.getText().equals(r1.getText()) && r.getScore() == r1.getScore());
	}

}
