package it.unisannio.studenti.franco.raffaele.server.community.server.web.resources.json;

import java.util.StringTokenizer;

import org.restlet.data.Status;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;

import it.unisannio.studenti.franco.raffaele.server.community.commons.ErrorCodes;
import it.unisannio.studenti.franco.raffaele.server.community.commons.InvalidUsernameException;
import it.unisannio.studenti.franco.raffaele.server.community.commons.User;
import it.unisannio.studenti.franco.raffaele.server.community.server.backend.wrapper.UserRegistryAPI;

public class UserLogJSON extends ServerResource {

	@Post
	public String checkUser(String payload) {
		Gson gson = new Gson();
		UserRegistryAPI urapi = UserRegistryAPI.instance();
		String response = gson.fromJson(payload, String.class);
		StringTokenizer st = new StringTokenizer(response, ";", false);
		String username = st.nextToken();
		String password = st.nextToken();
		User u = null;
		try { 
			u = urapi.getUser(username);
		} catch (InvalidUsernameException e) {
			Status s = new Status(ErrorCodes.INVALID_KEY_CODE);
			setStatus(s);
			return gson.toJson(false, Boolean.class);
		}
		if (u != null && u.getUsername().equalsIgnoreCase(username)
				&& String.copyValueOf(u.getPassword()).equalsIgnoreCase(password))
			return gson.toJson(true, Boolean.class);
		else {
			Status s = new Status(ErrorCodes.INVALID_CREDENTIAL_CODE);
			setStatus(s);
			return gson.toJson(false, Boolean.class);
		}
	}
}
