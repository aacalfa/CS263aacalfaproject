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

@SuppressWarnings("serial")
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
		BlobKey mapBlobKey = new BlobKey(req.getParameter("blob-key"));
		BlobInfoFactory blobInfoFactory = new BlobInfoFactory();
		BlobstoreService blobstoreService = BlobstoreServiceFactory
				.getBlobstoreService();
		BlobInfo blobinfo = blobInfoFactory.loadBlobInfo(mapBlobKey);
		ImagesService imagesService = ImagesServiceFactory.getImagesService();
		List<Composite> listComposites = new ArrayList<Composite>();
		List<Pair<BlobKey, ImageCoordinate>> attrImageList = null;
		Entity userData = null;

		long blobSize = blobinfo.getSize();
		// Google Image API limits fetches from image bytes to 1MB. Must read
		// them in chunks
		byte[] bytes = Serve.readImageData(mapBlobKey, blobSize);

		// Add map image to composite
		Image image = ImagesServiceFactory.makeImage(bytes);
		Composite aPaste = ImagesServiceFactory.makeComposite(image, 0, 0, 1f,
				Composite.Anchor.TOP_LEFT);
		listComposites.add(aPaste);

		// Try getting the attribute images list. If it doesn't exist,
		// create one.
		try {
			attrImageList = getUSerAttList(mapBlobKey.getKeyString());
		} catch (EntityNotFoundException e) {
			// Must create the entity
			Key currUserKey = KeyFactory.createKey("UserData",
					currentUser.getEmail());
			userData = new Entity(currUserKey);
			datastore.put(userData);
			// Initialize list
			attrImageList = new ArrayList<Pair<BlobKey, ImageCoordinate>>();

		}
		
		// Check if user has just added a new attribute image by double clicking
		// on the map image, if yes, add it.
		String radioValue = req.getParameter("attrname");
		if (radioValue != null && !radioValue.equals("null")
				&& !radioValue.equals("DELETE")) {
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
					setUSerAttList(mapBlobKey.getKeyString(), attrImageList);
				} catch (EntityNotFoundException e) {
					Pair<BlobKey, ImageCoordinate> pair = new Pair<BlobKey, ImageCoordinate>(
							attrblobKey, new ImageCoordinate(xcoord, ycoord));
					// The property string will contain: map image blobkey +
					// attribute image blobkey + x coordinate of attr image +
					// y coordinate of attr image.
					String prop = mapBlobKey.getKeyString() + "&"
							+ pair.getL().getKeyString() + "&"
							+ pair.getR().getXCoord() + "&"
							+ pair.getR().getYCoord();
					userData.setProperty(prop, prop);
					datastore.put(userData);
				}
			}
		}
		// User performed a single click on the map, check if it
		// is a valid delete. If yes, delete the marker
		else if (radioValue.equals("DELETE")) {
			int xcoord = Integer.parseInt(req.getParameter("xcoord"));
			int ycoord = Integer.parseInt(req.getParameter("ycoord"));
			try {
				deleteSelectedMarker(mapBlobKey.getKeyString(),
						new ImageCoordinate(xcoord, ycoord));
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
		}

		// Update the attribute images list that will be shown
		try {
			attrImageList = getUSerAttList(mapBlobKey.getKeyString());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		
		// Now loop through list and get all existing attribute images, if any
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

	private List<Pair<BlobKey, ImageCoordinate>> getUSerAttList(String mapBlobKeyStr)
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

				if (tokens.length != 4) { // Something really bad happened
					System.out.println("ERROR");
					continue;
				}
				// Check if attribute image corresponds to the current map image loaded
				if (!mapBlobKeyStr.equals(tokens[0]))
					continue;

				BlobKey attrblobKey = new BlobKey(tokens[1]);
				int xcoord = Integer.parseInt(tokens[2]);
				int ycoord = Integer.parseInt(tokens[3]);
				attrImagesList.add(new Pair<BlobKey, ImageCoordinate>(
						attrblobKey, new ImageCoordinate(xcoord, ycoord)));
			}
		}

		return attrImagesList;
	}

	private void setUSerAttList(String mapBlobKeyStr,
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
				String prop = mapBlobKeyStr + "&" + pair.getL().getKeyString()
						+ "&" + pair.getR().getXCoord() + "&"
						+ pair.getR().getYCoord();
				userData.setProperty(prop, prop);
			}
		}
		datastore.put(userData);

	}

	public void deleteSelectedMarker(String currMapKey, ImageCoordinate imgCoord)
			throws EntityNotFoundException {
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
		for (Entry<String, Object> property : properties.entrySet()) {
			final Object v = property.getValue();
			if (v instanceof String) {
				String[] tokens = ((String) v).split("&");
				String mapBlobKeyStr = tokens[0];

				// Check if property marker is from the same map
				if (!mapBlobKeyStr.equals(currMapKey))
					continue;

				// Now check if coordinates match the given marker
				int xcoord = Integer.parseInt(tokens[2]);
				int ycoord = Integer.parseInt(tokens[3]);
				if (Math.abs(imgCoord.getXCoord() - xcoord) > 30
						|| Math.abs(imgCoord.getYCoord() - ycoord) > 30)
					continue;
				// If this point is reached, we found the correct marker to
				// delete
				// Remove property from entity
				userData.removeProperty((String) v);
			}
		}
		// MUST call put to update entity!
		datastore.put(userData);
	}
}
