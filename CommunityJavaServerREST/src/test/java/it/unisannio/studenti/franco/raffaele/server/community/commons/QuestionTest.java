package it.unisannio.studenti.franco.raffaele.server.community.commons;

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

}
