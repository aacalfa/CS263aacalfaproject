package cs263aacalfaproject.cs263aacalfaproject;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * Handles marker manipulations, using JAXRS REST
 * @author Andre Abreu Calfa
 *
 */
@Path("/jerseyws")
public class MarkEditor {

	@POST
	@Path("/deleteall")
	/**
	 * Deletes all markers of current user, in the currently viewed map. 
	 * @param currMapKey Blobkey of current map being displayed.
	 * @return
	 * @throws EntityNotFoundException
	 * @throws URISyntaxException
	 */
	public Response deleteAllMarkers(@QueryParam("currMap") String currMapKey)
			throws EntityNotFoundException, URISyntaxException {
		UserService userService = UserServiceFactory.getUserService();
		User currentUser = userService.getCurrentUser();

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Entity userData = datastore.get(KeyFactory.createKey("UserData",
				currentUser.getEmail()));
		// Get list of attribute images
		Map<String, Object> properties = userData.getProperties();
		// Read all properties which contain the image blobkey and its
		// coordinates
		String mapBlobKeyStr = null;
		for (Entry<String, Object> property : properties.entrySet()) {
			final Object v = property.getValue();
			if (v instanceof String) {
				// Before removing, the map image blobkey must be temporarily
				// saved
				// so that we can redirect this method to the correct place
				String[] tokens = ((String) v).split("&");
				mapBlobKeyStr = tokens[0];
				// Delete only markers from current map.
				if (!mapBlobKeyStr.equals(currMapKey))
					continue;
				// Remove property from entity
				userData.removeProperty((String) v);
			}
		}
		// MUST call put to update entity!
		datastore.put(userData);

		// Refresh board
		String uriRedirect = "/board.jsp?blob-key=" + currMapKey;
		java.net.URI location = new java.net.URI(uriRedirect);
		return Response.temporaryRedirect(location).build();
	}

}