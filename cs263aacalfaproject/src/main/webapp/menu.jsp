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

<%
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user == null)
			response.sendRedirect("/");
%>
<p>These are the current available maps. Please select one and prepare for the battle!</p>

<%
			BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
			BlobInfoFactory blobInfoFactory = new BlobInfoFactory();
			Iterator<BlobInfo> iterator = new BlobInfoFactory().queryBlobInfos();
			while(iterator.hasNext()) {
				BlobInfo blobinfo = iterator.next();
				String mapname = blobinfo.getFilename();
				if (!mapname.contains("_BF4")) // Not actually a map
					continue;

				String blobkeyStr = blobinfo.getBlobKey().getKeyString();
				mapname = mapname.replace("_BF4.png", "");
				mapname = mapname.replace("_", " ");
				%>
				<a href="/serve?blob-key=<%= blobkeyStr%>"><%= mapname %> <br /></a>
				<%
			}
%>

</body>
</html>
