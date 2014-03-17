package dtu.library.app;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class LibraryApp {

	private boolean adminLoggedIn = false;
	private List<Medium> media = new ArrayList<Medium>();
	private List<User> users = new ArrayList<User>();
	private DateServer dateServer = new DateServer();
	private MailService mailService;

	public List<Medium> getMedia() {
		return Collections.unmodifiableList(media);
	}

	public boolean adminLoggedIn() {
		return adminLoggedIn;
	}

	public boolean adminLogin(String password) {
		return adminLoggedIn = "adminadmin".equals(password);
	}

	public void addMedium(Medium medium) throws OperationNotAllowedException {
		if (!adminLoggedIn)
			throw new OperationNotAllowedException("Add book");
		medium.setLibraryApp(this);
		media.add(medium);
	}

	public List<Medium> search(String string) {
		ArrayList<Medium> result = new ArrayList<Medium>();
		for (Medium medium : media) {
			if (medium.getTitle().contains(string) || medium.getAuthor().contains(string)
					|| medium.getSignature().contains(string)) {
				result.add(medium);
			}
		}
		return Collections.unmodifiableList(result);
	}

	public void adminLogoff() {
		adminLoggedIn = false;
	}

	public List<User> getUsers() {
		return Collections.unmodifiableList(users);
	}

	public void register(User user) throws OperationNotAllowedException {
		if (!adminLoggedIn)
			throw new OperationNotAllowedException("Register user");
		user.setLibraryApp(this);
		users.add(user);
	}

	public User userByCprNumber(String string) {
		for (User user : users) {
			if (string.equals(user.getCprNumber())) {
				return user;
			}
		}
		return null;
	}

	public Medium mediaBySignature(String signature) {
		for (Medium medium : media) {
			if (signature.equals(medium.getSignature())) {
				return medium;
			}
		}
		return null;
	}

	public void deleteMedium(String signature) throws OperationNotAllowedException {
		if (!adminLoggedIn()) {
			throw new OperationNotAllowedException("Delete medium");
		}
		Medium foundMedia = null;
		for (Medium oneMedia : media) {
			if (signature.equals(oneMedia.getSignature())) {
				foundMedia = oneMedia;
			}
		}
		media.remove(foundMedia);
	}

	void setDateServer(DateServer dateServer) {
		this.dateServer = dateServer;
		
	}

	public Calendar getDate() {
		return dateServer.getDate();
	}

	public void setMailService(MailService ms) {
		mailService = ms;
		
	}

	public void sendEMailReminder() {
		for (User user : users) {
			int count = 0;
			for (Medium medium : user.getBorrowedMedia()) {
				if (medium.isOverdue()) {
					count++;
				}
			}
			if (count > 0) {
				mailService.send(user.getEmail(),"Overdue book(s)","You have "+count+" overdue book(s)");
			}
		}
	}

	public void unregister(String cpr) throws OperationNotAllowedException {
		if (!adminLoggedIn()) {
			throw new OperationNotAllowedException("Unregister user");
		}
		User user = userByCprNumber(cpr);
		users.remove(user);
	}
}
