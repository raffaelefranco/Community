package it.unisannio.studenti.franco.raffaele.communityandroidclient.commons;

import java.io.Serializable;
import java.util.LinkedHashSet;

public class Question implements Serializable {

	private static final long serialVersionUID = 1L;

	public Question(String title, String text) {
		this.title = title;
		this.text = text;
		this.status = Costant.STATUSOPEN;
		this.user = null;
		this.responses = new LinkedHashSet<Response>();
	}

	public String getTitle() {
		return title;
	}

	public String getText() {
		return text;
	}

	public String getStatus() {
		return status;
	}

	public String getUser() {
		return user;
	}

	public LinkedHashSet<Response> getResponses() {
		return responses;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setResponses(LinkedHashSet<Response> responses) {
		this.responses = responses;
	}

	public void addResponse(Response response) {
		responses.add(response);
	}

	public String toString() {
		return "title=" + title + ", text=" + text + ", status=" + status + ", user=" + user + ", responses="
				+ responses.toString() + ";";
	}

	private String title;
	private String text;
	private String status;
	private String user;
	private LinkedHashSet<Response> responses;

}
