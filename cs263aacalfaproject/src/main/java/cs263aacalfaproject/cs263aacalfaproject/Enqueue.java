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

@Path("/jerseyws")
public class Enqueue {

	@POST
	@Path("/addmsg")
	@Consumes(MediaType.APPLICATION_JSON)
	public void sendMessage(String jsonstring) throws UnsupportedEncodingException {
		
		UserService userService = UserServiceFactory.getUserService();
		User currentUser = userService.getCurrentUser();

		//if (currentUser == null) {
			//response.sendRedirect(userService.createLoginURL(request.getRequestURI()));
		//}
		
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