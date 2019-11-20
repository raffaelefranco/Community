package it.unisannio.studenti.franco.raffaele.server.community.server.backend;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import it.unisannio.studenti.franco.raffaele.server.community.commons.Costant;
import it.unisannio.studenti.franco.raffaele.server.community.commons.InvalidKeyException;
import it.unisannio.studenti.franco.raffaele.server.community.commons.InvalidStatusException;
import it.unisannio.studenti.franco.raffaele.server.community.commons.InvalidUsernameException;
import it.unisannio.studenti.franco.raffaele.server.community.commons.Question;
import it.unisannio.studenti.franco.raffaele.server.community.commons.Response;

public class CommunityRegistry {

	public CommunityRegistry() {
		this.questions = new LinkedHashMap<String, Question>();
		this.responses = new LinkedHashSet<Response>();
	}

	public Question getQuestionByTitle(String title) throws InvalidKeyException {
		Question q = questions.get(title);
		if (q != null)
			return q;
		throw new InvalidKeyException("Chiave non valida: " + title);
	}

	public Response getResponse(String user, String text) throws InvalidKeyException {

		
		for (Response r : responses) {
		
			if (r.getUser().equals(user) && r.getText().equals(text))
				return r;
		}
		return null;
	}

	public Set<String> titles() {
		return questions.keySet();
	}

	public ArrayList<String> getResponsesByQuestionTitle(String title) throws InvalidKeyException {
		Question q = questions.get(title);
		if (q != null) {
			ArrayList<String> responses = new ArrayList<String>();
			for (Response r : q.getResponses())
				responses.add(r.getText());
			return responses;
		}
		throw new InvalidKeyException("Chiave non valida: " + title);
	}

	public void addQuestion(Question question) throws InvalidKeyException {
		if (questions.containsKey(question.getTitle()))
			throw new InvalidKeyException("Key duplicata: " + question.getTitle());
		questions.put(question.getTitle(), question);
	}

	public void addResponse(Response response, String title, String user)
			throws InvalidKeyException, InvalidStatusException {
		if (!questions.containsKey(title))
			throw new InvalidKeyException("Chiave non valida: " + title);
		if ((getQuestionByTitle(title).getStatus() == Costant.STATUSOPEN))
			throw new InvalidStatusException("La richiesta già stata chiusa");
		response.setUser(user);
		getQuestionByTitle(title).addResponse(response);
		responses.add(response);
	}

	public void updateQuestion(Question question) throws InvalidKeyException {
		Question q = questions.get(question.getTitle());
		if (q != null) {
			q.setText(question.getTitle());
			q.setStatus(question.getStatus());
			q.setResponses(question.getResponses());
		} else
			throw new InvalidKeyException("Chiave non valida: " + question.getTitle());
	}

	public void setStatusQuestion(String title, String username)
			throws InvalidKeyException, InvalidStatusException, InvalidUsernameException {
		if (!questions.containsKey(title))
			throw new InvalidKeyException("Chiave non valida: " + title);
		if (!(getQuestionByTitle(title).getStatus().equals(Costant.STATUSOPEN)))
			throw new InvalidStatusException("La richiesta � gi� stata chiusa");
		Question q = questions.get(title);
		if (!(q.getUser().equals(username)))
			throw new InvalidUsernameException("La richiesta non pu� essere chiusa da " + username);
		q.setStatus(Costant.STATUSCLOSED);
	}

	public void setScoreQuestion(String title, String username, String score)
			throws InvalidKeyException, InvalidStatusException, InvalidUsernameException {
		if (!questions.containsKey(title))
			throw new InvalidKeyException("Chiave non valida: " + title);

		Question q = questions.get(title);

		if (score.contentEquals("5"))
			q.incrementScore(5);
		else if (score.contentEquals("2"))
			q.decrementScore(2);
		else
			throw new InvalidStatusException("Score sbagliato!");

	}
	
	public void setScoreResponse(String user, String text, String score)
			throws InvalidKeyException, InvalidStatusException, InvalidUsernameException {
			
		Response r = getResponse(user, text);
		
		if (score.contentEquals("10"))
			r.incrementScore(10);
		else if (score.contentEquals("2"))
			r.decrementScore(2);
		responses.add(r);

	}

	public boolean contains(String key) {
		return questions.containsKey(key);
	}

	public void save(String fileOutName) throws IOException {
		FileOutputStream fileOut = new FileOutputStream(fileOutName);
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(questions);
		out.close();
		fileOut.close();
	}

	@SuppressWarnings("unchecked")
	public void load(String fileName) throws IOException, ClassNotFoundException {
		FileInputStream fileIn = new FileInputStream(fileName);
		ObjectInputStream in = new ObjectInputStream(fileIn);
		questions = (LinkedHashMap<String, Question>) in.readObject();
		for(Question q : questions.values()) {
			responses.addAll(q.getResponses());
		}
		in.close();
		fileIn.close();
	}

	public ArrayList<String> getRequests() {
		ArrayList<String> requests = new ArrayList<String>();

		for (Question q : questions.values())
			if (q.getStatus().equals(Costant.STATUSOPEN))
				requests.add(q.getTitle());

		return requests;
	}

	public ArrayList<String> getRequestsByUser(String username) {
		ArrayList<String> requests = new ArrayList<String>();

		for (Question q : questions.values())
			if (q.getUser().equals(username) && q.getStatus().equals(Costant.STATUSOPEN))
				requests.add(q.getTitle());

		return requests;
	}

	public ArrayList<String> getClosedQuestions(String username) {
		ArrayList<String> requests = new ArrayList<String>();

		for (Question r : questions.values()) {
			if (r.getStatus().equals(Costant.STATUSCLOSED) && r.getUser().equals(username))
				requests.add(r.getTitle());
		}
		return requests;
	}

	public LinkedHashMap<String, Question> questions;
	public LinkedHashSet<Response> responses;
	public String user;
}
