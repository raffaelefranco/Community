package it.unisannio.studenti.franco.raffaele.server.community.server.web.resources.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Scanner;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import it.unisannio.studenti.franco.raffaele.server.community.commons.InvalidKeyException;
import it.unisannio.studenti.franco.raffaele.server.community.commons.InvalidUsernameException;
import it.unisannio.studenti.franco.raffaele.server.community.commons.Question;
import it.unisannio.studenti.franco.raffaele.server.community.commons.User;
import it.unisannio.studenti.franco.raffaele.server.community.server.backend.wrapper.CommunityRegistryAPI;
import it.unisannio.studenti.franco.raffaele.server.community.server.backend.wrapper.UserRegistryAPI;

class CommunityRegistryJSONTest {

	class Settings {
		String storage_base_dir;
		String storage_base_file;
		String users_storage_base_dir;
		String users_storage_base_file;
		String web_base_dir;
	}

	static User u;
	static Gson gson = new Gson();
	static CommunityRegistryJSON communityRegistryJSON = new CommunityRegistryJSON();
	static UserRegJSON userRegJSON = new UserRegJSON();

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		Settings settings = null;
		u = new User("max", "1234".toCharArray());

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

		String userString = gson.toJson(u, User.class);

		try {
			@SuppressWarnings("unused")
			String response = gson.fromJson(userRegJSON.addUser(userString), String.class);
			// assertEquals("User added: " + u.getUsername(), response);
		} catch (ParseException e) {
			// fail();
		}

	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {

		File dir = new File("users");

		for (File file : dir.listFiles())
			if (!file.isDirectory())
				file.delete();

		File dir1 = new File("storage");

		for (File file : dir1.listFiles())
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
	void addQuestionTest1() throws ParseException, InvalidUsernameException {

		Question q = new Question("Question", "Text");
		q.setUser(u.getUsername());
		String question = gson.toJson(q);
		String response = gson.fromJson(communityRegistryJSON.addQuestion(question), String.class);

		assertEquals("Question added: " + q.getTitle(), response);
	}

	@Test
	void addQuestionTest2() throws JsonSyntaxException, ParseException, InvalidUsernameException {

		Question q = new Question("Question", "Text");
		q.setUser(u.getUsername());
		String question = gson.toJson(q);

		assertTrue(communityRegistryJSON.addQuestion(question).contains("duplicata"));

	}

	@SuppressWarnings("unchecked")
	@Test
	void getQuestionsTest() {

		Question q = new Question("Question1", "Text1");
		q.setUser(u.getUsername());
		String question = gson.toJson(q);
		try {
			@SuppressWarnings("unused")
			String response = gson.fromJson(communityRegistryJSON.addQuestion(question), String.class);
		} catch (JsonSyntaxException e1) {
			e1.printStackTrace();
		} catch (ParseException e1) {
			e1.printStackTrace();
		} catch (InvalidUsernameException e1) {
			e1.printStackTrace();
		}

		Question q1 = new Question("Question2", "Text2");
		q1.setUser(u.getUsername());
		String question1 = gson.toJson(q1);
		try {
			@SuppressWarnings("unused")
			String response = gson.fromJson(communityRegistryJSON.addQuestion(question1), String.class);
		} catch (JsonSyntaxException e1) {
			e1.printStackTrace();
		} catch (ParseException e1) {
			e1.printStackTrace();
		} catch (InvalidUsernameException e1) {
			e1.printStackTrace();
		}

		ArrayList<String> questions = new ArrayList<String>();

		try {
			questions = gson.fromJson(communityRegistryJSON.getQuestions(), ArrayList.class);

		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}

		assertEquals(2, questions.size());
	}

}
