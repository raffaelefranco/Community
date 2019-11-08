package it.unisannio.studenti.franco.raffaele.server.community.server.backend.wrapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import it.unisannio.studenti.franco.raffaele.server.community.commons.InvalidUsernameException;
import it.unisannio.studenti.franco.raffaele.server.community.commons.Question;
import it.unisannio.studenti.franco.raffaele.server.community.commons.User;
import it.unisannio.studenti.franco.raffaele.server.community.server.backend.UserRegistry;

public class UserRegistryAPI {

	public static synchronized UserRegistryAPI instance() {
		if (instance == null)
			instance = new UserRegistryAPI();
		return instance;
	}

	protected UserRegistryAPI() {
		ur = new UserRegistry();
	}

	public UserRegistry getUserRegistry() {
		return ur;
	}

	public synchronized User getUser(String username) throws InvalidUsernameException {
		return ur.getUser(username);
	}

	public synchronized void addQuestion(User u, Question q) {
		ur.addQuestion(u, q);
	}

	public synchronized ArrayList<String> getOpenedQuestions(String username) throws InvalidUsernameException {
		return ur.getOpenedQuestions(username);
	}
	
	public synchronized ArrayList<String> getClosedQuestions(String username) throws InvalidUsernameException {
		return ur.getClosedQuestions(username);
	}

	public synchronized void addUser(User u) throws InvalidUsernameException {
		ur.addUser(u);
		commit();
	}

	// Imposto le informazioni di storage degli utent
	public void setStorageFiles(String rootDirForStorageFile, String baseStorageFile) {
		this.rootDirForStorageFile = rootDirForStorageFile;
		this.baseStorageFile = baseStorageFile;
		System.err.println("Users Storage Directory: " + this.rootDirForStorageFile);
		System.err.println("Users Storage Base File: " + this.baseStorageFile);
	}

	// Costruisco l'estensione del file in base ai file gi� presenti all'interno
	// della cartella
	protected int buildStorageFileExtension() {
		final File folder = new File(rootDirForStorageFile);
		int c;
		int max = -1;

		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.getName().substring(0, baseStorageFile.length()).equalsIgnoreCase(baseStorageFile)) {
				try {
					c = Integer.parseInt(fileEntry.getName().substring(baseStorageFile.length() + 1));
				} catch (/*NumberFormatException | */StringIndexOutOfBoundsException e) {
					c = -1;
				}
				if (c > max)
					max = c;
			}
		}
		return max;
	}

	// Effettuo il salvataggio (commit) delle modifiche su file
	public synchronized void commit() {
		int extension = buildStorageFileExtension();
		String fileName = rootDirForStorageFile + baseStorageFile + "." + (extension + 1);
		System.err.println("Commit storage to: " + fileName);
		try {
			ur.save(fileName);
		} catch (IOException e) {
			System.err.println("Commit failed " + e.getMessage() + " " + e.getCause());
		}
	}

	// Effettuo il recupero delle informazioni degli utenti (restore)
	public synchronized void restore() {
		int extension = buildStorageFileExtension();
		if (extension == -1) {
			System.err.println("No data to load - starting a new registry");
		} else {
			String fileName = rootDirForStorageFile + baseStorageFile + "." + extension;
			System.err.println("Restore storage from: " + fileName);
			try {
				ur.load(fileName);
			} catch (/*ClassNotFoundException | */IOException e) {
				System.err.println("Restore failed - starting a new registry " + e.getCause() + " " + e.getMessage());
				ur = new UserRegistry();
			} catch (ClassNotFoundException e) {
				System.err.println("Restore failed - starting a new registry " + e.getCause() + " " + e.getMessage());
				ur = new UserRegistry();
			}
		}
	}

	private static UserRegistryAPI instance;
	private UserRegistry ur;
	private String rootDirForStorageFile;
	private String baseStorageFile;

}
