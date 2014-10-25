package cs263aacalfaproject.cs263aacalfaproject;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

public class Serve extends HttpServlet {
	private BlobstoreService blobstoreService = BlobstoreServiceFactory
			.getBlobstoreService();

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
			blobstoreService.serve(blobKey, res);
		}
	}
}