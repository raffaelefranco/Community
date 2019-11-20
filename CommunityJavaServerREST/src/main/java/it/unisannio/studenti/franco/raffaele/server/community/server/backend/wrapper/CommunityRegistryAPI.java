package it.unisannio.studenti.franco.raffaele.server.community.server.backend.wrapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import it.unisannio.studenti.franco.raffaele.server.community.commons.InvalidKeyException;
import it.unisannio.studenti.franco.raffaele.server.community.commons.InvalidStatusException;
import it.unisannio.studenti.franco.raffaele.server.community.commons.InvalidUsernameException;
import it.unisannio.studenti.franco.raffaele.server.community.commons.Question;
import it.unisannio.studenti.franco.raffaele.server.community.commons.Response;
import it.unisannio.studenti.franco.raffaele.server.community.server.backend.CommunityRegistry;

public class CommunityRegistryAPI {
	public static synchronized CommunityRegistryAPI instance() {
		if (instance == null)
			instance = new CommunityRegistryAPI();
		return instance;
	}

	public synchronized Question getQuestionByTitle(String title) throws InvalidKeyException {
		return cr.getQuestionByTitle(title);
	}
	
	public synchronized Response getResponseByUserEText(String user, String text) throws InvalidKeyException {
		return cr.getResponse(user, text);
	}

	public synchronized ArrayList<String> getRequests() throws InvalidKeyException {
		return cr.getRequests();
	}
	
	public synchronized ArrayList<String> getRequestsByUser(String username) {
		return cr.getRequestsByUser(username);
	}

	public synchronized ArrayList<String> getClosedQuestions(String username) {
		return cr.getClosedQuestions(username);
	}
	
	public synchronized Set<String> titles() {
		return cr.titles();
	}

	public synchronized void addQuestion(Question q) throws InvalidKeyException {
		cr.addQuestion(q);
		commit();
	}

	public synchronized void addResponse(Response response, String title, String user)
			throws InvalidKeyException, InvalidStatusException {
		cr.addResponse(response, title, user);
		commit();
	}

	public synchronized void updateQuestion(Question q) throws InvalidKeyException {
		cr.updateQuestion(q);
		commit();
	}

	public synchronized void setStatusQuestion(String title, String username) throws InvalidKeyException, InvalidStatusException, InvalidUsernameException {
		cr.setStatusQuestion(title, username);
		commit();
	}

	public synchronized void setScoreQuestion(String title, String username, String score) throws InvalidKeyException, InvalidStatusException, InvalidUsernameException {
		cr.setScoreQuestion(title, username, score);
		commit();
	}
	public synchronized void setScoreResponse(String user, String text, String score) throws InvalidKeyException, InvalidStatusException, InvalidUsernameException {

		cr.setScoreResponse(user, text, score);
		commit();
	}
	
	protected CommunityRegistryAPI() {
		cr = new CommunityRegistry();
	}

	public void setStorageFiles(String rootDirForStorageFile, String baseStorageFile) {
		this.rootDirForStorageFile = rootDirForStorageFile;
		this.baseStorageFile = baseStorageFile;
		System.err.println("Users Storage Directory: " + this.rootDirForStorageFile);
		System.err.println("Users Storage Base File: " + this.baseStorageFile);
	}

	protected int buildStorageFileExtension() {
		final File folder = new File(rootDirForStorageFile);
		int c;
		int max = -1;

		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.getName().substring(0, baseStorageFile.length()).equalsIgnoreCase(baseStorageFile)) {
				try {
					c = Integer.parseInt(fileEntry.getName().substring(baseStorageFile.length() + 1));
				} catch (NumberFormatException  | StringIndexOutOfBoundsException e) {
					c = -1;
				}
				if (c > max)
					max = c;
			}
		}
		return max;
	}

	public synchronized void commit() {
		int extension = buildStorageFileExtension();
		String fileName = rootDirForStorageFile + baseStorageFile + "." + (extension + 1);
		System.err.println("Commit storage to: " + fileName);
		try {
			cr.save(fileName);
		} catch (IOException e) {
			System.err.println("Commit filed " + e.getMessage() + " " + e.getCause());
		}
	}

	public synchronized void restore() {
		int extension = buildStorageFileExtension();
		if (extension == -1) {
			System.err.println("No data to load - starting a new registry");
		} else {
			String fileName = rootDirForStorageFile + baseStorageFile + "." + extension;
			System.err.println("Restore storage from: " + fileName);
			try {
				cr.load(fileName);
			} catch (IOException e) {
				System.err.println("Restore filed - starting a new registry " + e.getCause() + " " + e.getMessage());
				cr = new CommunityRegistry();
			} catch (ClassNotFoundException e) {
				System.err.println("Restore filed - starting a new registry " + e.getCause() + " " + e.getMessage());
				cr = new CommunityRegistry();
			}
		}
	}

	private static CommunityRegistryAPI instance;
	private CommunityRegistry cr;
	private String rootDirForStorageFile;
	private String baseStorageFile;
}
