package cs263aacalfaproject.cs263aacalfaproject;

//file Upload.java

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.memcache.*;
import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.images.*;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class Upload extends HttpServlet {
	private BlobstoreService blobstoreService = BlobstoreServiceFactory
			.getBlobstoreService();

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		Map<String, List<BlobKey>> blobFields = blobstoreService
				.getUploads(req);
		List<BlobKey> blobKeys = blobFields.get("myFile");

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		// User clicked on Submit/View uploaded maps, check if there are maps
		// already uploaded
		if (blobFields.size() == 0) {
			Query gaeQuery = new Query("GameMap");
			PreparedQuery pq = datastore.prepare(gaeQuery);
			List<Entity> list = pq.asList(FetchOptions.Builder.withDefaults());
			if (!list.isEmpty()) {
				// Show the main board menu. User will select one of available
				// maps
				res.sendRedirect("/menu.jsp");
			} else {
				res.sendRedirect("/serve?blob-key=" + ""); // Error message will
															// be shown
			}
		} else { // User chose to upload a new map
			// Traverse through map
			for (BlobKey blobKey : blobKeys) {
				BlobInfoFactory blobInfoFactory = new BlobInfoFactory();
				BlobInfo blobInfo = blobInfoFactory.loadBlobInfo(blobKey);
				String blobFilename = blobInfo.getFilename();
				if (blobFilename.contains("_BF4")) { // Loading a map
					Entity imagedata = new Entity("GameMap",
							blobKey.getKeyString());
					imagedata.setProperty("blob-key", blobKey.getKeyString());

					imagedata.setProperty("mapname", blobFilename);
					// Add to data store
					datastore.put(imagedata);
					// Also add information to memcache
					addBlobInfoMemCache(blobInfo);
				} else if (blobFilename.contains("_ATTR")) { // Loading a map
																// attribute
					Entity imagedata = new Entity("MapAtributes",
							blobKey.getKeyString());
					imagedata.setProperty("blob-key", blobKey.getKeyString());

					imagedata.setProperty("attrname", blobFilename);
					// Add to data store
					datastore.put(imagedata);
				}
			}
			res.sendRedirect("/menu.jsp");

		}
	}

	private void addBlobInfoMemCache(BlobInfo blobInfo) {

		long blobSize = blobInfo.getSize();
		// Google Image API limits fetches from image bytes to 1MB. Must
		// read them in chunks
		byte[] bytes = Serve.readImageData(blobInfo.getBlobKey(), blobSize);
		// Add map image to composite
		Image image = ImagesServiceFactory.makeImage(bytes);

		// Using the synchronous cache
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		syncCache.setErrorHandler(ErrorHandlers
				.getConsistentLogAndContinue(Level.INFO));
		String value = blobInfo.getFilename() + "&" + blobInfo.getSize() + "&"
				+ image.getWidth() + "&" + image.getHeight() + "&" + image.getFormat().toString();
		syncCache.put(blobInfo.getBlobKey(), value); // populate cache

	}
}