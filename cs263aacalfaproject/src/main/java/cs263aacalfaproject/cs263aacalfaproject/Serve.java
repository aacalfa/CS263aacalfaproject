package cs263aacalfaproject.cs263aacalfaproject;

import java.io.*;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.images.*;
import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.datastore.*;

public class Serve extends HttpServlet {
	
	public static byte[] readImageData(BlobKey blobKey, long blobSize) {
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
			throws IOException, ServletException {
		if (req.getParameter("blob-key") == "") {
			PrintWriter out = res.getWriter();
		    out.println("<html><body>");
		    out.println("<script type=\"text/javascript\">");
		    out.println("var popwin = window.open(\"error.jsp\",'width=200, height=10')");
		    out.println("setTimeout(function(){ popwin.close(); window.location.href='/';},2000)");
		    out.println("</script>");
		    out.println("</body></html>");
		}
		else { // Valid map selected, show strategy board.
			BlobKey blobKey = new BlobKey(req.getParameter("blob-key"));
			// Show board with map
			req.setAttribute("blob-key", blobKey.getKeyString());
			req.getRequestDispatcher("board.jsp").forward(req, res);
		}
	}
}

