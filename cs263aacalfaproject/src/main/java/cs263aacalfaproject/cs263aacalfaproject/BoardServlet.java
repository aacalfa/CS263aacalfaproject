package cs263aacalfaproject.cs263aacalfaproject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.images.Composite;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class BoardServlet extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		UserService userService = UserServiceFactory.getUserService();
		User currentUser = userService.getCurrentUser();

		if (currentUser == null) {
			resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));
		}

		BlobKey blobKey = new BlobKey(req.getParameter("blob-key"));
		BlobInfoFactory blobInfoFactory = new BlobInfoFactory();
		BlobstoreService blobstoreService = BlobstoreServiceFactory
				.getBlobstoreService();
		BlobInfo blobinfo = blobInfoFactory.loadBlobInfo(blobKey);
		ImagesService imagesService = ImagesServiceFactory.getImagesService();
		List<Composite> listComposites = new ArrayList<Composite>();

		long blobSize = blobinfo.getSize();
		// Google Image API limits fetches from image bytes to 1MB. Must read
		// them in chunks
		byte[] bytes = Serve.readImageData(blobKey, blobSize);

		Image image = ImagesServiceFactory.makeImage(bytes);
		Composite aPaste = ImagesServiceFactory.makeComposite(image, 0, 0, 1f,
				Composite.Anchor.TOP_LEFT);
		listComposites.add(aPaste);

		String radioValue = req.getParameter("attrname");
		if (!radioValue.equals("null")) {
			// Get attribute image
			DatastoreService datastore = DatastoreServiceFactory
					.getDatastoreService();
			Query gaeQuery = new Query("MapAtributes");
			PreparedQuery pq = datastore.prepare(gaeQuery);
			List<Entity> list = pq.asList(FetchOptions.Builder.withDefaults());
			BlobInfo attrinfo = null;
			BlobKey attrblobKey = null;
			for (Entity obj : list) {
				String attrname = (String) obj.getProperty("attrname");
				attrname = attrname.replace("_ATTR.png", "");
				if(!attrname.equals(radioValue))
					continue;
				
				String blobkeyStr = (String) obj.getProperty("blob-key");
				attrblobKey = new BlobKey(blobkeyStr);
				attrinfo = blobInfoFactory.loadBlobInfo(attrblobKey);
			}
			byte[] attrbytes = blobstoreService.fetchData(attrblobKey, 0,
					attrinfo.getSize());
			Image attrimage = ImagesServiceFactory.makeImage(attrbytes);

			Composite bPaste = ImagesServiceFactory.makeComposite(attrimage, 0,
					0, 1.0f, Composite.Anchor.CENTER_CENTER);
			listComposites.add(bPaste);
		}

		Image newImage = imagesService.composite(listComposites,
				image.getWidth(), image.getHeight(), 0L,
				ImagesService.OutputEncoding.PNG);

		// serve the image
		resp.setContentType("image/png");
		resp.getOutputStream().write(newImage.getImageData());
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String radioValue = request.getParameter("radios");

		// Show board with map
		request.setAttribute("attrname", radioValue);
		request.getRequestDispatcher("board.jsp").forward(request, response);
	}
}
