package it.unisannio.studenti.franco.raffaele.server.community.commons;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	public User(String username, char[] password) {
		this.username = username;
		this.password = password;
		this.questions = new LinkedHashMap<String, Question>();
		this.responses = new LinkedHashSet<Response>();
	}

	public String getUsername() {
		return username;
	}

	public char[] getPassword() {
		return password;
	}

	public LinkedHashMap<String, Question> getQuestions() {
		return questions;
	}

	public LinkedHashSet<Response> getResponses() {
		return responses;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(char[] password) {
		this.password = password;
	}

	public void setQuestions(LinkedHashMap<String, Question> questions) {
		this.questions = questions;
	}

	public void setResponses(LinkedHashSet<Response> responses) {
		this.responses = responses;
	}

	public void addQuestion(Question question) {
		questions.put(question.getTitle(), question);
	}

	public void addResponse(Response response) {
		responses.add(response);
	}

	public void removeQuestion(Question question) {
		questions.remove(question.getTitle());
	}


	public String toString() {
		return "User: " + username + ", " + String.valueOf(password) + ".";
	}
	

	
	private String username;
	private char[] password;
	private LinkedHashMap<String, Question> questions;
	private LinkedHashSet<Response> responses;
}
