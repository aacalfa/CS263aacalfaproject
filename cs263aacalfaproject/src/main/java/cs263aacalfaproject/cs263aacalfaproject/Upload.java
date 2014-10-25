package cs263aacalfaproject.cs263aacalfaproject;

//file Upload.java

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;

public class Upload extends HttpServlet {
	private BlobstoreService blobstoreService = BlobstoreServiceFactory
			.getBlobstoreService();

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
		BlobKey blobKey = blobs.get("myFile");

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		if (blobKey == null) {
			Query gaeQuery = new Query("GameMap");
			PreparedQuery pq = datastore.prepare(gaeQuery);
			List<Entity> list = pq.asList(FetchOptions.Builder.withDefaults());
			BlobKey testKey = null;
			if (!list.isEmpty()) {
				for (Entity obj : list) {
					System.out.println(obj);
					testKey = new BlobKey(obj.getProperty("blob-key").toString());
				}
				res.sendRedirect("/serve?blob-key=" + testKey.getKeyString());
			}
			else {
				res.sendRedirect("/serve?blob-key=" + ""); // Error message will be shown
			}
			// }
			// else
			// res.sendRedirect("/board.jsp");
		} else {
			Entity imagedata = new Entity("GameMap", blobKey.getKeyString());
			imagedata.setProperty("blob-key", blobKey.getKeyString());
			// Add to data store
			datastore.put(imagedata);
			res.sendRedirect("/serve?blob-key=" + blobKey.getKeyString());
		}
	}
}