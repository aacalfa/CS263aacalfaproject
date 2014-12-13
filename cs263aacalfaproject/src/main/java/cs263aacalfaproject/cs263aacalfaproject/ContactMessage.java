package cs263aacalfaproject.cs263aacalfaproject;

/**
 * Represents a feedback email message
 * @author Andre Abreu Calfa
 *
 */
public class ContactMessage {

	private String username;
	private String emailtext;
	
	public ContactMessage() {
	}
	
	/**
	 * Creates a Message object.
	 * @param name User name.
	 * @param message Message body.
	 */
	public ContactMessage(String name, String message) {
		this.username = name;
		this.emailtext = message;
	}
	
	/**
	 * Returns user name.
	 * @return
	 */
	public String getName() {
		return username;
	}
	
	/**
	 * Returns the message body.
	 * @return
	 */
	public String getMessage() {
		return emailtext;
	}
	
	/**
	 * Sets the user name.
	 * @param name The user name
	 * @return
	 */
	public String setName(String name) {
		username = name;
		return username;
	}
	
	/**
	 * Sets the message body.
	 * @param newmsg New message body.
	 * @return
	 */
	public String setMessage(String newmsg) {
		emailtext = newmsg;
		return emailtext;
	}
}
