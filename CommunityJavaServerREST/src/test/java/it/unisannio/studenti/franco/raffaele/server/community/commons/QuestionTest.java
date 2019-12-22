package it.unisannio.studenti.franco.raffaele.server.community.commons;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
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
