package cs263aacalfaproject.cs263aacalfaproject;

import java.io.IOException;
import java.util.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.gson.Gson;

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
			String mapname = blobinfo.getFilename();
			if (!mapname.contains("_BF4")) // Not actually a map
				continue;

			long blobSize = blobinfo.getSize();
			// Google Image API limits fetches from image bytes to 1MB. Must
			// read them in chunks
			byte[] bytes = Serve.readImageData(blobinfo.getBlobKey(), blobSize);
			// Add map image to composite
			Image image = ImagesServiceFactory.makeImage(bytes);

			MapData mapdt = new MapData(blobinfo.getFilename(), blobSize,
					image.getWidth(), image.getHeight(), image.getFormat());
			mapList.add(mapdt);
		}
		// Send data to JSON
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(new Gson().toJson(mapList));
	}
}
