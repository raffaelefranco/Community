package it.unisannio.studenti.franco.raffaele.communityandroidclient.commons;

import java.io.Serializable;

public class Response implements Serializable {

	private static final long serialVersionUID = 1L;

	public Response(String text) {
		this.text = text;
		this.user = null;
		this.question = null;
		this.score = 0;
	}

	public String getText() {
		return text;
	}

	public String getUser() {
		return user;
	}

	public Question getQuestion() {
		return question;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public String toString() {
		return "ext=" + text + ", user=" + user + ";";
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public void incrementScore(int increment) {
		this.score+=increment;
	}

	public void decrementScore(int descrement) {
		this.score-=descrement;
	}

	private String text;
	private String user;
	private int score;
	private Question question;

}
