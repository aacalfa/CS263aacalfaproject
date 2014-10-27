package cs263aacalfaproject.cs263aacalfaproject;

//file Upload.java

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.datastore.*;

public class Upload extends HttpServlet {
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
		BlobKey blobKey = blobs.get("myFile");

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		// User clicked on Submit/View uploaded maps, check if there are maps already uploaded
		if (blobKey == null) {
			Query gaeQuery = new Query("GameMap");
			PreparedQuery pq = datastore.prepare(gaeQuery);
			List<Entity> list = pq.asList(FetchOptions.Builder.withDefaults());
			BlobKey testKey = null;
			if (!list.isEmpty()) {
				for (Entity obj : list) {
					testKey = new BlobKey(obj.getProperty("blob-key").toString());
				}

				res.sendRedirect("/board.jsp");
				//res.sendRedirect("/serve?blob-key=" + testKey.getKeyString());
			}
			else {
				res.sendRedirect("/serve?blob-key=" + ""); // Error message will be shown
			}
		} else {
			Entity imagedata = new Entity("GameMap", blobKey.getKeyString());
			imagedata.setProperty("blob-key", blobKey.getKeyString());
			BlobInfoFactory blobInfoFactory = new BlobInfoFactory();
			BlobInfo blobInfo = blobInfoFactory.loadBlobInfo(blobKey);
			String blobFilename = blobInfo.getFilename();
			imagedata.setProperty("mapname", blobFilename);
			// Add to data store
			datastore.put(imagedata);
			res.sendRedirect("/serve?blob-key=" + blobKey.getKeyString());
		}
	}
}