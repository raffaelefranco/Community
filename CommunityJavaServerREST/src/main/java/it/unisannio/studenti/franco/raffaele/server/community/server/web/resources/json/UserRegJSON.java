package it.unisannio.studenti.franco.raffaele.server.community.server.web.resources.json;

import java.text.ParseException;
import java.util.ArrayList;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;

import it.unisannio.studenti.franco.raffaele.server.community.commons.ErrorCodes;
import it.unisannio.studenti.franco.raffaele.server.community.commons.InvalidUsernameException;
import it.unisannio.studenti.franco.raffaele.server.community.commons.User;
import it.unisannio.studenti.franco.raffaele.server.community.server.backend.wrapper.CommunityRegistryAPI;
import it.unisannio.studenti.franco.raffaele.server.community.server.backend.wrapper.UserRegistryAPI;

public class UserRegJSON extends ServerResource {

	@Post
	public String addUser(String payload) throws ParseException {
		Gson gson = new Gson();
		UserRegistryAPI urapi = UserRegistryAPI.instance();
		User u = gson.fromJson(payload, User.class);
		try {
			urapi.addUser(u);
			return gson.toJson("User added: " + u.getUsername(), String.class);
		} catch (InvalidUsernameException e) {
			Status s = new Status(ErrorCodes.INVALID_KEY_CODE);
			setStatus(s);
			return gson.toJson(e, InvalidUsernameException.class);
		}
	}

	@Get
	public String getOpenedQuestions() {
		Gson gson = new Gson();
		CommunityRegistryAPI crapi = CommunityRegistryAPI.instance();
		UserRegistryAPI urapi = UserRegistryAPI.instance();
		try {
			User u = urapi.getUser(getAttribute("username"));
			return gson.toJson(crapi.getRequestsByUser(u.getUsername()), ArrayList.class);
		} catch (InvalidUsernameException | NullPointerException e) {
			Status s = new Status(ErrorCodes.INVALID_KEY_CODE);
			setStatus(s);
			return gson.toJson(e, InvalidUsernameException.class);
		}
		
	}
}
