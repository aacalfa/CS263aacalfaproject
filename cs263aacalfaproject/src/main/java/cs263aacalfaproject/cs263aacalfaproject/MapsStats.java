package cs263aacalfaproject.cs263aacalfaproject;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.memcache.*;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.Image.Format;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.gson.Gson;

/**
 * Servlet that builds the MapData table in main menu.
 * @author Andre Abreu Calfa
 *
 */
public class MapsStats extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		UserService userService = UserServiceFactory.getUserService();
		User currentUser = userService.getCurrentUser();

		if (currentUser == null) {
			resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));
		}

		// List to be displayed in .JSP file
		List<MapData> mapList = new ArrayList<MapData>();

		// Get all maps loaded and gather their information
		Iterator<BlobInfo> iterator = new BlobInfoFactory().queryBlobInfos();
		while (iterator.hasNext()) {
			BlobInfo blobinfo = iterator.next();

			// Try to get information from memcache
			MemcacheService syncCache = MemcacheServiceFactory
					.getMemcacheService();
			syncCache.setErrorHandler(ErrorHandlers
					.getConsistentLogAndContinue(Level.INFO));
			// read from cache
			String value = (String) syncCache.get(blobinfo.getBlobKey());
			if (value != null) {
				String[] tokens = value.split("&");
				// Make sure we have all necessary information
				if (tokens.length == 5) {
					String mapName = tokens[0];
					long mapSize = Long.parseLong(tokens[1]);
					int mapWidth = Integer.parseInt(tokens[2]);
					int mapHeight = Integer.parseInt(tokens[3]);
					Format mapFormat = Format.valueOf(tokens[4]);
					MapData mapdt = new MapData(mapName, mapSize, mapWidth,
							mapHeight, mapFormat);
					mapList.add(mapdt);
				}
			} else { // Get map info the slower way
				String mapname = blobinfo.getFilename();
				if (!mapname.contains("_BF4")) // Not actually a map
					continue;

				long blobSize = blobinfo.getSize();
				// Google Image API limits fetches from image bytes to 1MB. Must
				// read them in chunks
				byte[] bytes = Serve.readImageData(blobinfo.getBlobKey(),
						blobSize);
				// Add map image to composite
				Image image = ImagesServiceFactory.makeImage(bytes);

				MapData mapdt = new MapData(blobinfo.getFilename(), blobSize,
						image.getWidth(), image.getHeight(), image.getFormat());
				mapList.add(mapdt);
			}
		}
		// Send data to JSON
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(new Gson().toJson(mapList));
	}
}
