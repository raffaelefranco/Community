package it.unisannio.studenti.franco.raffaele.server.community.server.backend;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import it.unisannio.studenti.franco.raffaele.server.community.commons.Costant;
import it.unisannio.studenti.franco.raffaele.server.community.commons.InvalidUsernameException;
import it.unisannio.studenti.franco.raffaele.server.community.commons.Question;
import it.unisannio.studenti.franco.raffaele.server.community.commons.User;

public class UserRegistry {

	public UserRegistry() {
		reg = new LinkedHashMap<String, User>();
	}

	public Map<String, User> getUsers() {
		return reg;
	}

	public User getUser(String username) throws InvalidUsernameException {
		User u = reg.get(username);
		if (u != null)
			return u;
		throw new InvalidUsernameException("Username non valido: " + username);
	}

	public ArrayList<String> getOpenedQuestions(String username) {
		User u = reg.get(username);
		ArrayList<String> requests = new ArrayList<String>();
		if (u.getQuestions() == null)
			return null;
		else {
			for (Question r : u.getQuestions().values()) {
				if (r.getStatus().equals(Costant.STATUSOPEN))
					requests.add(r.getTitle());
			}
			return requests;
		}
	}
	
	public ArrayList<String> getClosedQuestions(String username) {
		User u = reg.get(username);
		ArrayList<String> requests = new ArrayList<String>();
		if (u.getQuestions() == null)
			return null;
		else {
			for (Question r : u.getQuestions().values()) {
				if (r.getStatus().equals(Costant.STATUSCLOSED))
					requests.add(r.getTitle());
			}
			return requests;
		}
	}

	public void addQuestion(User u, Question r) {
		r.setUser(u.getUsername());
		u.addQuestion(r);
	}

	public void addUser(User u) throws InvalidUsernameException {
		if (reg.containsKey(u.getUsername()))
			throw new InvalidUsernameException("Username Duplicato:" + u.getUsername());
		reg.put(u.getUsername(), u);
	}

	public void update(User u) {
		reg.put(u.getUsername(), u);
	}

	public void removeUser(String username) throws InvalidUsernameException {
		if (!reg.containsKey(username))
			throw new InvalidUsernameException("Username non valido: " + username);
		reg.remove(username);
	}

	public void save(String fileOutName) throws IOException {
		FileOutputStream fileOut = new FileOutputStream(fileOutName);
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(reg);
		out.close();
		fileOut.close();
	}

	@SuppressWarnings("unchecked")
	public void load(String fileName) throws IOException, ClassNotFoundException {
		FileInputStream fileIn = new FileInputStream(fileName);
		ObjectInputStream in = new ObjectInputStream(fileIn);
		reg = ((LinkedHashMap<String, User>) in.readObject());
		in.close();
		fileIn.close();
	}

	private LinkedHashMap<String, User> reg;
}
