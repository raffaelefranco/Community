package it.unisannio.studenti.franco.raffaele.server.community.server.web.resources.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Scanner;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

import it.unisannio.studenti.franco.raffaele.server.community.commons.User;
import it.unisannio.studenti.franco.raffaele.server.community.server.backend.wrapper.CommunityRegistryAPI;
import it.unisannio.studenti.franco.raffaele.server.community.server.backend.wrapper.UserRegistryAPI;

class UserRegJSONTest {

	class Settings {
		String storage_base_dir;
		String storage_base_file;
		String users_storage_base_dir;
		String users_storage_base_file;
		String web_base_dir;
	}

	static User u, u1;
	static Gson gson = new Gson();
	static UserRegJSON userRegJSON = new UserRegJSON();
	static UserRegJSON userRegJSON1 = new UserRegJSON();

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		Settings settings = null;
		u = new User("raf", "1234".toCharArray());
		u1 = new User("raf", "pssw".toCharArray());

		try {
			Scanner scanner = new Scanner(new File("settings.json"));
			settings = gson.fromJson(scanner.nextLine(), Settings.class);
			scanner.close();
			System.err.println("Loading settings from file");
		} catch (FileNotFoundException e1) {
			System.err.println("Settings file not found");
			System.exit(-1);
		}

		String rootDirForWebStaticFiles = "file:///" + System.getProperty("user.dir") + "//" + settings.web_base_dir;
		System.err.println("Web Directory: " + rootDirForWebStaticFiles);

		CommunityRegistryAPI nrapi = CommunityRegistryAPI.instance();
		nrapi.setStorageFiles(System.getProperty("user.dir") + "//" + settings.storage_base_dir + "//",
				settings.storage_base_file);
		nrapi.restore();

		UserRegistryAPI urapi = UserRegistryAPI.instance();
		urapi.setStorageFiles(System.getProperty("user.dir") + "//" + settings.users_storage_base_dir + "//",
				settings.users_storage_base_file);
		urapi.restore();

	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {

		File dir = new File("users");

		for (File file : dir.listFiles())
			if (!file.isDirectory())
				file.delete();
	}

	@BeforeEach
	void setUp() throws Exception {

	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void addUserTest1() {

		String userString = gson.toJson(u, User.class);

		try {
			String response = gson.fromJson(userRegJSON.addUser(userString), String.class);
			assertEquals("User added: " + u.getUsername(), response);
		} catch (ParseException e) {
			fail();
		}
	}

	@Test
	void addUserTest2() {

		String userString = gson.toJson(u1, User.class);

		String response;
		try {
			response = userRegJSON1.addUser(userString);
			assertTrue(response.contains("Duplicato"));
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	@Test
	void getOpenedQuestionsTest() {
		userRegJSON.getOpenedQuestions();
		assertTrue(true);
	}
	
}
