package it.unisannio.studenti.franco.raffaele.server.community.server.web.resources.json;

import static org.junit.jupiter.api.Assertions.assertTrue;

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

class QuestionJSONTest {

	class Settings {
		String storage_base_dir;
		String storage_base_file;
		String users_storage_base_dir;
		String users_storage_base_file;
		String web_base_dir;
	}

	static User user1, user2;
	static Gson gson = new Gson();
	static UserRegJSON userRegJSON = new UserRegJSON();
	static QuestionJSON questionJSON = new QuestionJSON();

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		Settings settings = null;
		user1 = new User("raffaele", "1234".toCharArray());
		user2 = new User("gerardo", "1234".toCharArray());
		
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
	void getQuestionTest() {
		try {
			questionJSON.getQuestion();
			assertTrue(true);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	@Test 
	void postResponseTest() {
		String payload = null;
		
		try {
			questionJSON.postResponse(payload);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
