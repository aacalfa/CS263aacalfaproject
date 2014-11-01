package cs263aacalfaproject.cs263aacalfaproject;

import java.io.*;
import java.util.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.images.*;
import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.datastore.*;

public class Serve extends HttpServlet {
	
	public byte[] readImageData(BlobKey blobKey, long blobSize) {
	    BlobstoreService blobStoreService = BlobstoreServiceFactory
	            .getBlobstoreService();
	    byte[] allTheBytes = new byte[(int)blobSize];
	    long amountLeftToRead = blobSize;
	    long startIndex = 0;
	    while (amountLeftToRead > 0) {
	        long amountToReadNow = Math.min(
	                BlobstoreService.MAX_BLOB_FETCH_SIZE - 1, amountLeftToRead);

	        byte[] chunkOfBytes = blobStoreService.fetchData(blobKey,
	                startIndex, startIndex + amountToReadNow - 1);

	        System.arraycopy(chunkOfBytes, 0, allTheBytes, (int)startIndex, chunkOfBytes.length);
	        
	        amountLeftToRead -= amountToReadNow;
	        startIndex += amountToReadNow;
	    }

	    return allTheBytes;
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		if (req.getParameter("blob-key") == "") {
			PrintWriter out = res.getWriter();
		    out.println("<html><body>");
		    out.println("<script type=\"text/javascript\">");
		    out.println("var popwin = window.open(\"error.jsp\",'width=200, height=10')");
		    out.println("setTimeout(function(){ popwin.close(); window.location.href='/';},2000)");
		    out.println("</script>");
		    out.println("</body></html>");
		}
		else {
			BlobKey blobKey = new BlobKey(req.getParameter("blob-key"));
			// Find corresponding map image blobinfo instance
			BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
			BlobInfoFactory blobInfoFactory = new BlobInfoFactory();
			BlobInfo blobinfo = blobInfoFactory.loadBlobInfo(blobKey);

			ImagesService imagesService = ImagesServiceFactory.getImagesService();
			long blobSize = blobinfo.getSize();
			// Google Image API limits fetches from image bytes to 1MB. Must read them in chunks
			byte[] bytes = readImageData(blobKey, blobSize);
			
			Image image = ImagesServiceFactory.makeImage(bytes);
			
			List<Composite> listComposites=new ArrayList<Composite>();

			Composite aPaste = ImagesServiceFactory.makeComposite(image, 0, 0, 1f, Composite.Anchor.TOP_LEFT);
			listComposites.add( aPaste );

			// Get attribute image
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			Query gaeQuery = new Query("MapAtributes");
			PreparedQuery pq = datastore.prepare(gaeQuery);
			List<Entity> list = pq.asList(FetchOptions.Builder.withDefaults());
			BlobInfo attrinfo = null;
			BlobKey attrblobKey = null; 
			for(Entity obj : list) {
		        String blobkeyStr = (String)obj.getProperty("blob-key");
		        String attrname = (String)obj.getProperty("attrname");
		        attrblobKey = new BlobKey(blobkeyStr);
		        attrinfo = blobInfoFactory.loadBlobInfo(attrblobKey);
		    }
			byte[] attrbytes = blobstoreService.fetchData(attrblobKey, 0, attrinfo.getSize());
			Image attrimage = ImagesServiceFactory.makeImage(attrbytes);

			Composite bPaste = ImagesServiceFactory.makeComposite(attrimage, 0, 0, 1.0f, Composite.Anchor.CENTER_CENTER);
			listComposites.add( bPaste );

			Image newImage = imagesService.composite(listComposites, image.getWidth(), image.getHeight(), 0L, ImagesService.OutputEncoding.PNG);
			
			// serve the image
			res.setContentType("image/png");
			res.getOutputStream().write(newImage.getImageData());
		}
	}
}

