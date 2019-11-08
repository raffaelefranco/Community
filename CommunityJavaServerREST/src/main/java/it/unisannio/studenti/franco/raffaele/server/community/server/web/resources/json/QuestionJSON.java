package it.unisannio.studenti.franco.raffaele.server.community.server.web.resources.json;

import java.text.ParseException;
import java.util.StringTokenizer;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;

import it.unisannio.studenti.franco.raffaele.server.community.commons.ErrorCodes;
import it.unisannio.studenti.franco.raffaele.server.community.commons.InvalidKeyException;
import it.unisannio.studenti.franco.raffaele.server.community.commons.InvalidStatusException;
import it.unisannio.studenti.franco.raffaele.server.community.commons.Question;
import it.unisannio.studenti.franco.raffaele.server.community.commons.Response;
import it.unisannio.studenti.franco.raffaele.server.community.server.backend.wrapper.CommunityRegistryAPI;

public class QuestionJSON extends ServerResource {

	@Get
	public String getQuestion() throws ParseException {
		Gson gson = new Gson();
		CommunityRegistryAPI nrapi = CommunityRegistryAPI.instance();
		try {
			Question q = nrapi.getQuestionByTitle((getAttribute("title").replace("%20", " ")));
			return gson.toJson(q, Question.class);
		} catch (InvalidKeyException e) {
			Status s = new Status(ErrorCodes.INVALID_KEY_CODE);
			setStatus(s);
			return gson.toJson(e, InvalidKeyException.class);
		}
	}
	
	@Post
	public String postResponse(String payload) throws ParseException {
		Gson gson = new Gson();
		CommunityRegistryAPI nrapi = CommunityRegistryAPI.instance();
		try {
			Question q = nrapi.getQuestionByTitle((getAttribute("title").replace("%20", " ")));
			String response = gson.fromJson(payload, String.class);
			StringTokenizer st = new StringTokenizer(response, ":", false);
			String username = st.nextToken();
			String text = st.nextToken();
			nrapi.addResponse(new Response(text), q.getTitle(), username);
			return gson.toJson(q, Question.class);
		} catch (InvalidKeyException e) {
			Status s = new Status(ErrorCodes.INVALID_KEY_CODE);
			setStatus(s);
			return gson.toJson(e, InvalidKeyException.class);
		} catch (InvalidStatusException e) {
			Status s = new Status(ErrorCodes.INVALID_KEY_CODE);
			setStatus(s);
			return gson.toJson(e, InvalidStatusException.class);
		}
	}
}
