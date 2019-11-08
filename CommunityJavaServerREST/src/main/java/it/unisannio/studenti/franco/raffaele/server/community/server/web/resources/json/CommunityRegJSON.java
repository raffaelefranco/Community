package it.unisannio.studenti.franco.raffaele.server.community.server.web.resources.json;

import java.text.ParseException;
import java.util.ArrayList;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;

import it.unisannio.studenti.franco.raffaele.server.community.commons.ErrorCodes;
import it.unisannio.studenti.franco.raffaele.server.community.commons.InvalidKeyException;
import it.unisannio.studenti.franco.raffaele.server.community.commons.InvalidStatusException;
import it.unisannio.studenti.franco.raffaele.server.community.commons.InvalidUsernameException;
import it.unisannio.studenti.franco.raffaele.server.community.commons.Question;
import it.unisannio.studenti.franco.raffaele.server.community.commons.User;
import it.unisannio.studenti.franco.raffaele.server.community.server.backend.wrapper.CommunityRegistryAPI;
import it.unisannio.studenti.franco.raffaele.server.community.server.backend.wrapper.UserRegistryAPI;

public class CommunityRegJSON extends ServerResource {

	@Get
	public String getQuestions() throws InvalidKeyException {
		Gson gson = new Gson();
		CommunityRegistryAPI crapi = CommunityRegistryAPI.instance();
		UserRegistryAPI urapi = UserRegistryAPI.instance();
		try {
			User u = urapi.getUser(getAttribute("username"));
			return gson.toJson(crapi.getClosedQuestions(u.getUsername()), ArrayList.class);
		} catch (InvalidUsernameException e) {
			Status s = new Status(ErrorCodes.INVALID_KEY_CODE);
			setStatus(s);
			return gson.toJson(e, InvalidUsernameException.class);
		}
	}

	@Post
	public String closeQuestion(String payload) throws ParseException {
		Gson gson = new Gson();
		CommunityRegistryAPI nrapi = CommunityRegistryAPI.instance();
		String title = gson.fromJson(payload, String.class);
		Question q;
		try {
			q = nrapi.getQuestionByTitle(title);
			nrapi.setStatusQuestion(q.getTitle(), getAttribute("username"));
			return gson.toJson("Question closed: " + q.getTitle(), String.class);
		} catch (InvalidKeyException e) {
			Status s = new Status(ErrorCodes.INVALID_KEY_CODE);
			setStatus(s);
			return gson.toJson(e, InvalidKeyException.class);
		} catch (InvalidStatusException e) {
			Status s = new Status(ErrorCodes.INVALID_KEY_CODE);
			setStatus(s);
			return gson.toJson(e, InvalidStatusException.class);
		} catch (InvalidUsernameException e) {
			Status s = new Status(ErrorCodes.INVALID_KEY_CODE);
			setStatus(s);
			return gson.toJson(e, InvalidUsernameException.class);
		}
	}

}
