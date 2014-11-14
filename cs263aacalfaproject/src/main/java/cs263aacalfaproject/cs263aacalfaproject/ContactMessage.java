package cs263aacalfaproject.cs263aacalfaproject;

public class ContactMessage {

	private String username;
	private String emailtext;
	
	public ContactMessage() {
	}
	
	public ContactMessage(String name, String message) {
		this.username = name;
		this.emailtext = message;
	}
	
	public String getName() {
		return username;
	}
	
	public String getMessage() {
		return emailtext;
	}
	
	public String setName(String name) {
		username = name;
		return username;
	}
	
	public String setMessage(String newmsg) {
		emailtext = newmsg;
		return emailtext;
	}
}
