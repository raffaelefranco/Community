package it.unisannio.studenti.franco.raffaele.server.community.commons;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
		assertTrue(r.getText().equals(r1.getText()) && r.getScore() == r1.getScore());
	}

}
