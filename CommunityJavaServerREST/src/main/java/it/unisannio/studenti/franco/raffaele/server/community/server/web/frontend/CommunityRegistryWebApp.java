package it.unisannio.studenti.franco.raffaele.server.community.server.web.frontend;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.resource.Directory;
import org.restlet.routing.Router;

import com.google.gson.Gson;

import it.unisannio.studenti.franco.raffaele.server.community.server.backend.wrapper.CommunityRegistryAPI;
import it.unisannio.studenti.franco.raffaele.server.community.server.backend.wrapper.UserRegistryAPI;
import it.unisannio.studenti.franco.raffaele.server.community.server.web.resources.json.CommunityRegJSON;
import it.unisannio.studenti.franco.raffaele.server.community.server.web.resources.json.CommunityRegistryJSON;
import it.unisannio.studenti.franco.raffaele.server.community.server.web.resources.json.QuestionJSON;
import it.unisannio.studenti.franco.raffaele.server.community.server.web.resources.json.ScoreRequestJSON;
import it.unisannio.studenti.franco.raffaele.server.community.server.web.resources.json.UserLogJSON;
import it.unisannio.studenti.franco.raffaele.server.community.server.web.resources.json.UserRegJSON;

public class CommunityRegistryWebApp extends Application {

	private class Settings {
		public int port;
		public String storage_base_dir;
		public String storage_base_file;
		public String users_storage_base_dir;
		public String users_storage_base_file;
		public String web_base_dir;
	}

	private static String rootDirForWebStaticFiles;

	public Restlet createInboundRoot() {
		Router router = new Router(getContext());

		Directory directory = new Directory(getContext(), rootDirForWebStaticFiles);
		directory.setListingAllowed(true);
		directory.setDeeplyAccessible(true);

		router.attach("/CommunityApplication/login", UserLogJSON.class);
		router.attach("/CommunityApplication/registration", UserRegJSON.class);
		router.attach("/CommunityApplication/requests", CommunityRegistryJSON.class);
		router.attach("/CommunityApplication/opened_requests/{username}", UserRegJSON.class);
		router.attach("/CommunityApplication/opened_requests", CommunityRegistryJSON.class);
		router.attach("/CommunityApplication/closed_requests/{username}", CommunityRegJSON.class);
		router.attach("/CommunityApplication/closed_requests/status/{username}", CommunityRegJSON.class);
		router.attach("/CommunityApplication/opened_requests_title/{title}", QuestionJSON.class);
		router.attach("/CommunityApplication/opened_requests/{title}/response", QuestionJSON.class);
		router.attach("/CommunityApplication/closed_requests/{username}/{title}", QuestionJSON.class);
		router.attach("/CommunityApplication/score_requests/{username}", ScoreRequestJSON.class);
		router.attach("/CommunityApplication/score_responses/{username}", ScoreResponseJSON.class);
		
		return router;
	}

	public static void main(String[] args) {

		Gson gson = new Gson();
		Settings settings = null;

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

		try {
			Component component = new Component(); 
			component.getServers().add(Protocol.HTTP, settings.port);
			component.getClients().add(Protocol.FILE); 
			component.getDefaultHost().attach(new CommunityRegistryWebApp());

			component.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
