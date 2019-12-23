package it.unisannio.studenti.franco.raffaele.server.community.commons;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashSet;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QuestionTest {

	static Question q;
	static Question q1;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		q = new Question("question", "text");
		q1 = new Question("question", "text");
		q.setScore(10);
		q1.setScore(5);

		q.decrementScore(3);
		q1.incrementScore(2);
		System.out.println(q.getScore());
		System.out.println(q.getStatus());
		System.out.println(q.getText() + q.getTitle() + q.getUser());
		System.out.println(q1.getResponses());

		Response r = new Response("text");
		Response r1 = new Response("text1");
		LinkedHashSet<Response> responses = new LinkedHashSet<Response>();
		responses.add(r);
		q.addResponse(r1);
		q.setResponses(responses);
		
		Question question = new Question("Title", "Text");
		
		question.setStatus("CLOSE");
		question.setTitle("new Title");
		question.setText("new Text");
		

	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		System.out.println(q.toString());
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test() {
		assertTrue(q.getTitle().equals(q1.getTitle()) && q.getText().equals(q1.getText())
				&& q.getScore() == q1.getScore() && q.getStatus() == q1.getStatus());
	}

	@Test
	void dependentAssertions() {
		// Within a code block, if an assertion fails the
		// subsequent code in the same block will be skipped.
		assertAll("properties", () -> {
			String title = q.getTitle();
			assertNotNull(title);
			// Executed only if the previous assertion is valid.
			assertAll("title", () -> assertTrue(title.startsWith("q")), () -> assertTrue(title.endsWith("n")));
		}, () -> {
			// Grouped assertion, so processed independently
			// of results of first name assertions.
			String text = q.getText();
			assertNotNull(text);
			// Executed only if the previous assertion is valid.
			assertAll("text", () -> assertTrue(text.startsWith("t")), () -> assertTrue(text.endsWith("t")));
		});
	}

}
