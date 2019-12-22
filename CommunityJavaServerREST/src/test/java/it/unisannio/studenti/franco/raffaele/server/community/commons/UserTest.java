package it.unisannio.studenti.franco.raffaele.server.community.commons;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserTest {

	static User u;
	static User u1;
	static User u2;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		u = new User("raffaele", "123".toCharArray());
		u1 = new User("gerardo", "123".toCharArray());
		u2 = new User("gerardo", "123".toCharArray());
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
		assertFalse(u.getPassword().length != u.getPassword().length);
	}

	void test1() {
		assertTrue(u.getUsername().equals(u1.getUsername()));
	}
	
	@Test 
	void groupedAssertions() { 
		assertAll("users", 
				() -> assertEquals("gerardo", u2.getUsername()), 
				() -> assertEquals(3, u2.getPassword().length)); 
		}
}
