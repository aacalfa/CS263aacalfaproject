package cs263aacalfaproject.cs263aacalfaproject;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.images.*;
import com.google.appengine.api.users.*;

public class BoardServlet extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		UserService userService = UserServiceFactory.getUserService();
		User currentUser = userService.getCurrentUser();

		if (currentUser == null) {
			resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));
		}

		// Get all appengine structures
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
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

		// Add map image to composite
		Image image = ImagesServiceFactory.makeImage(bytes);
		Composite aPaste = ImagesServiceFactory.makeComposite(image, 0, 0, 1f,
				Composite.Anchor.TOP_LEFT);
		listComposites.add(aPaste);

		// First, get all existing attribute images, if any
		List<Pair<BlobKey, ImageCoordinate>> attrImageList = null;
		Entity userData = null;
		try {
			attrImageList = getUSerAttList();
		} catch (EntityNotFoundException e) {
			// Must create the entity
			Key currUserKey = KeyFactory.createKey("UserData",
					currentUser.getEmail());
			userData = new Entity(currUserKey);
			datastore.put(userData);
			// Initialize list
			attrImageList = new ArrayList<Pair<BlobKey, ImageCoordinate>>();

		}
		for (Pair<BlobKey, ImageCoordinate> pair : attrImageList) {
			BlobKey attrBlobKey = pair.getL();
			ImageCoordinate imgCoord = pair.getR();

			BlobInfo attrInfo = blobInfoFactory.loadBlobInfo(attrBlobKey);
			// Get attribute image
			byte[] attrBytes = blobstoreService.fetchData(attrBlobKey, 0,
					attrInfo.getSize());
			Image attrImage = ImagesServiceFactory.makeImage(attrBytes);

			// Create composite and add it to the list.
			Composite newComposite = ImagesServiceFactory.makeComposite(
					attrImage, imgCoord.getXCoord(), imgCoord.getYCoord(),
					1.0f, Composite.Anchor.TOP_LEFT);
			listComposites.add(newComposite);
		}

		String radioValue = req.getParameter("attrname");
		if (!radioValue.equals("null")) {
			// Get attribute coordinates
			int xcoord = Integer.parseInt(req.getParameter("xcoord"));
			int ycoord = Integer.parseInt(req.getParameter("ycoord"));

			// Get attribute image
			Query gaeQuery = new Query("MapAtributes");
			PreparedQuery pq = datastore.prepare(gaeQuery);
			List<Entity> list = pq.asList(FetchOptions.Builder.withDefaults());
			BlobInfo attrinfo = null;
			BlobKey attrblobKey = null;
			for (Entity obj : list) {
				String attrname = (String) obj.getProperty("attrname");
				attrname = attrname.replace("_ATTR.png", "");
				if (!attrname.equals(radioValue))
					continue;

				String blobkeyStr = (String) obj.getProperty("blob-key");
				attrblobKey = new BlobKey(blobkeyStr);
				attrinfo = blobInfoFactory.loadBlobInfo(attrblobKey);
			}
			// Create attribute image
			byte[] attrbytes = blobstoreService.fetchData(attrblobKey, 0,
					attrinfo.getSize());
			Image attrimage = ImagesServiceFactory.makeImage(attrbytes);

			// Add attribute image to composite
			// Check if coordinates values are valid
			if (xcoord <= image.getWidth() && ycoord <= image.getHeight()) {
				// Adjust xcoord and ycoord to correspond to center of attribute
				// image
				xcoord -= attrimage.getWidth() / 2;
				ycoord -= attrimage.getHeight() / 2;
				Composite bPaste = ImagesServiceFactory.makeComposite(
						attrimage, xcoord, ycoord, 1.0f,
						Composite.Anchor.TOP_LEFT);
				listComposites.add(bPaste);
				// Update the user's attribute image list and set its property
				attrImageList.add(new Pair<BlobKey, ImageCoordinate>(
						attrblobKey, new ImageCoordinate(xcoord, ycoord)));
				try {
					setUSerAttList(attrImageList);
				} catch (EntityNotFoundException e) {
					Pair<BlobKey, ImageCoordinate> pair = new Pair<BlobKey, ImageCoordinate>(
							attrblobKey, new ImageCoordinate(xcoord, ycoord));
					String prop = pair.getL().getKeyString() + "&"
							+ pair.getR().getXCoord() + "&"
							+ pair.getR().getYCoord();
					userData.setProperty(prop, prop);
					datastore.put(userData);
				}
			}
		}

		// Form new image with map and attribute composites
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

	private List<Pair<BlobKey, ImageCoordinate>> getUSerAttList()
			throws EntityNotFoundException {
		UserService userService = UserServiceFactory.getUserService();
		User currentUser = userService.getCurrentUser();

		// Check if user's entity already exists in datastore
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Entity userData = datastore.get(KeyFactory.createKey("UserData",
				currentUser.getEmail()));

		// Get list of attribute images
		Map<String, Object> properties = userData.getProperties();
		List<Pair<BlobKey, ImageCoordinate>> attrImagesList = new ArrayList<Pair<BlobKey, ImageCoordinate>>();

		// Read all properties which contain the image blobkey and its
		// coordinates
		for (Entry<String, Object> property : properties.entrySet()) {
			final Object v = property.getValue();
			if (v instanceof String) {
				String[] tokens = ((String) v).split("&");

				if (tokens.length != 3) // Something really bad happened
					System.out.println("ERROR");

				BlobKey blobKey = new BlobKey(tokens[0]);
				int width = Integer.parseInt(tokens[1]);
				int height = Integer.parseInt(tokens[2]);
				attrImagesList.add(new Pair<BlobKey, ImageCoordinate>(blobKey,
						new ImageCoordinate(width, height)));
			}
		}

		return attrImagesList;
	}

	private void setUSerAttList(
			List<Pair<BlobKey, ImageCoordinate>> attrImageList)
			throws EntityNotFoundException {
		UserService userService = UserServiceFactory.getUserService();
		User currentUser = userService.getCurrentUser();

		// Check if user's entity already exists in datastore
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Entity userData = datastore.get(KeyFactory.createKey("UserData",
				currentUser.getEmail()));

		// Update property if entity exists
		if (userData != null) {
			// Since datastore does not accept most of types, we will create a
			// property containing the blobkey string, the x coordinate and the
			// y coordinate, all in the same string
			for (Pair<BlobKey, ImageCoordinate> pair : attrImageList) {
				String prop = pair.getL().getKeyString() + "&"
						+ pair.getR().getXCoord() + "&"
						+ pair.getR().getYCoord();
				userData.setProperty(prop, prop);
			}
		}
		datastore.put(userData);

	}
}
