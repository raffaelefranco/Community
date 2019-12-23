package it.unisannio.studenti.franco.raffaele.server.community.server.web.resources.json;

import java.text.ParseException;
import java.util.StringTokenizer;

import org.restlet.data.Status;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;

import it.unisannio.studenti.franco.raffaele.server.community.commons.ErrorCodes;
import it.unisannio.studenti.franco.raffaele.server.community.commons.InvalidKeyException;
import it.unisannio.studenti.franco.raffaele.server.community.commons.InvalidStatusException;
import it.unisannio.studenti.franco.raffaele.server.community.commons.InvalidUsernameException;
import it.unisannio.studenti.franco.raffaele.server.community.commons.Response;
import it.unisannio.studenti.franco.raffaele.server.community.server.backend.wrapper.CommunityRegistryAPI;

public class ScoreResponseJSON extends ServerResource {

	@Post
	public String scoreQuestion(String payload) throws ParseException {
		Gson gson = new Gson();
		CommunityRegistryAPI nrapi = CommunityRegistryAPI.instance();
		String response = gson.fromJson(payload, String.class);
		StringTokenizer st = new StringTokenizer(response, ";", false);
		String user = st.nextToken();
		String text = st.nextToken();
		String score = st.nextToken();

		Response q;
		try {
			q = nrapi.getResponseByUserEText(user, text);

			nrapi.setScoreResponse(q.getUser(), q.getText(), score);
			return gson.toJson("Response scored");

		} catch (InvalidKeyException | InvalidStatusException | InvalidUsernameException | NullPointerException e) {
			Status s = new Status(ErrorCodes.INVALID_KEY_CODE);
			setStatus(s);
			return gson.toJson(e, InvalidKeyException.class);
		}
	}

}
