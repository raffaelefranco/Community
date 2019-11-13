package it.unisannio.studenti.franco.raffaele.server.community.server.web.resources.json;

import java.text.ParseException;
import java.util.ArrayList;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;

import it.unisannio.studenti.franco.raffaele.server.community.commons.ErrorCodes;
import it.unisannio.studenti.franco.raffaele.server.community.commons.InvalidKeyException;
import it.unisannio.studenti.franco.raffaele.server.community.commons.InvalidUsernameException;
import it.unisannio.studenti.franco.raffaele.server.community.commons.Question;
import it.unisannio.studenti.franco.raffaele.server.community.commons.User;
import it.unisannio.studenti.franco.raffaele.server.community.server.backend.wrapper.CommunityRegistryAPI;
import it.unisannio.studenti.franco.raffaele.server.community.server.backend.wrapper.UserRegistryAPI;

public class CommunityRegistryJSON extends ServerResource {

	@Get
	public String getQuestions() throws InvalidKeyException {
		Gson gson = new Gson();
		CommunityRegistryAPI nrapi = CommunityRegistryAPI.instance();
		return gson.toJson(nrapi.getRequests(), ArrayList.class);
	}

	@Put
	public String addQuestion(String payload) throws ParseException, InvalidUsernameException {
		Gson gson = new Gson();
		CommunityRegistryAPI nrapi = CommunityRegistryAPI.instance();
		Question q = gson.fromJson(payload, Question.class);
		UserRegistryAPI urapi = UserRegistryAPI.instance();
		User u = urapi.getUser(q.getUser());

		try {
			nrapi.addQuestion(q);
			urapi.addQuestion(u, q);
			return gson.toJson("Question added: " + q.getTitle(), String.class);
		} catch (InvalidKeyException e) {
			Status s = new Status(ErrorCodes.INVALID_KEY_CODE);
			setStatus(s);
			return gson.toJson(e, InvalidKeyException.class);
		}
	}


}
