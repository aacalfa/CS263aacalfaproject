package cs263aacalfaproject.cs263aacalfaproject;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.appengine.api.taskqueue.*;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.*;


/**
 * Taskqueue that enqueues feedback email messages.
 * @author aacalfa
 *
 */
@Path("/jerseyws")
public class Enqueue {

	@POST
	@Path("/addmsg")
	@Consumes(MediaType.APPLICATION_JSON)
	/**
	 * Add message to the taskqueue to be sent by Worker class.
	 * @param jsonstring String containing a ContactMessage object (will be translated with GSON)
	 * @throws UnsupportedEncodingException
	 */
	public void sendMessage(String jsonstring) throws UnsupportedEncodingException {
		
		UserService userService = UserServiceFactory.getUserService();
		User currentUser = userService.getCurrentUser();
		
		// Read the email form which was sent from JSON
		JsonReader jsonreader = new JsonReader(new StringReader(jsonstring));
		Gson gson = new Gson();

		// convert the json string back to object
		ContactMessage obj = gson.fromJson(jsonreader, ContactMessage.class);

		// Add the task to the default queue.
		Queue queue = QueueFactory.getDefaultQueue();
		queue.add(withUrl("/worker").param("name", obj.getName())
				.param("emailaddr", currentUser.getEmail())
				.param("emailtext", obj.getMessage()));
	}
}