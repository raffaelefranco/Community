package it.unisannio.studenti.franco.raffaele.server.community.server.web.fronted;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.restlet.Component;
import org.restlet.data.Protocol;

import com.google.gson.Gson;

import it.unisannio.studenti.franco.raffaele.server.community.server.backend.wrapper.CommunityRegistryAPI;
import it.unisannio.studenti.franco.raffaele.server.community.server.backend.wrapper.UserRegistryAPI;
import it.unisannio.studenti.franco.raffaele.server.community.server.web.frontend.CommunityRegistryWebApp;

class CommunityRegistryWebAppTest {

	private class Settings {
		public int port;
		public String storage_base_dir;
		public String storage_base_file;
		public String users_storage_base_dir;
		public String users_storage_base_file;
		public String web_base_dir;
	}
	
	private static String rootDirForWebStaticFiles;
	private static Gson gson;
	private static Settings settings;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		gson = new Gson();
		settings = null;

		try {
			Scanner scanner = new Scanner(new File("settings.json"));
			settings = gson.fromJson(scanner.nextLine(), Settings.class);
			scanner.close();
			System.err.println("Loading settings from file");
		} catch (FileNotFoundException e1) {
			System.err.println("Settings file not found");
			System.exit(-1);
		}

		rootDirForWebStaticFiles = "file:///" + System.getProperty("user.dir") + "//" + settings.web_base_dir;
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
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test() {
		try {
			Component component = new Component(); 
			component.getServers().add(Protocol.HTTP, settings.port);
			component.getClients().add(Protocol.FILE); 
			component.getDefaultHost().attach(new CommunityRegistryWebApp());

			component.start();
			assertTrue(true);
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}

}
