<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.FetchOptions" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ page import="com.google.appengine.api.datastore.Query" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="java.util.*" %>
<%@ page import="com.google.appengine.api.blobstore.*" %>
<%@ page import="com.google.appengine.api.images.*" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>



<html>
<head>
    <link type="text/css" rel="stylesheet" href="/stylesheets/main.css"/>
		<title>Strategy Board Menu</title>
</head>

<body>

<%!
private byte[] readImageData(BlobKey blobKey, long blobSize) {
	    BlobstoreService blobStoreService = BlobstoreServiceFactory.getBlobstoreService();
	    byte[] allTheBytes = new byte[(int)blobSize];
	    long amountLeftToRead = blobSize;
	    long startIndex = 0;
	    while (amountLeftToRead > 0) {
	        long amountToReadNow = Math.min(BlobstoreService.MAX_BLOB_FETCH_SIZE - 1, amountLeftToRead);

	        byte[] chunkOfBytes = blobStoreService.fetchData(blobKey, startIndex, startIndex + amountToReadNow - 1);

	        System.arraycopy(chunkOfBytes, 0, allTheBytes, (int)startIndex, chunkOfBytes.length);
	        
	        amountLeftToRead -= amountToReadNow;
	        startIndex += amountToReadNow;
	    }

	    return allTheBytes;
	}
%>

<%
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user == null)
			response.sendRedirect("/");

		String blobKeystr = request.getParameter( "blob-key" );
		if ( blobKeystr == null )
			response.sendRedirect("/");

		BlobKey blobKey = new BlobKey(request.getParameter("blob-key"));
		String blobkeyStr = blobKey.getKeyString();
		// Find corresponding map image blobinfo instance
		
		BlobInfoFactory blobInfoFactory = new BlobInfoFactory();
		BlobInfo blobinfo = blobInfoFactory.loadBlobInfo(blobKey);

		long blobSize = blobinfo.getSize();
		// Google Image API limits fetches from image bytes to 1MB. Must read them in chunks
		byte[] bytes = readImageData(blobKey, blobSize);
		
		Image image = ImagesServiceFactory.makeImage(bytes);

		// serve the image
		//res.setContentType("image/png");
		//res.getOutputStream().write(image.getImageData());
%>

<img src="boardshow?blob-key=<%= blobkeyStr%>" />

<p>This is a test.</p>

</body>
</html>

