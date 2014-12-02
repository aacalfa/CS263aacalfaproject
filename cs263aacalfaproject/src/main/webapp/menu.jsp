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

<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
<script src="jquery.serializeJSON.min.js"></script>
<script>

$.fn.serializeObject = function() {
    var o = {};
//    var a = this.serializeArray();
    $(this).find('input[type="hidden"], input[type="text"], input[type="password"], input[type="checkbox"]:checked, input[type="radio"]:checked, select, textarea').each(function() {
        if ($(this).attr('type') == 'hidden') { //if checkbox is checked do not take the hidden field
            var $parent = $(this).parent();
            var $chb = $parent.find('input[type="checkbox"][name="' + this.name.replace(/\[/g, '\[').replace(/\]/g, '\]') + '"]');
            if ($chb != null) {
                if ($chb.prop('checked')) return;
            }
        }
        if (this.name === null || this.name === undefined || this.name === '') return;
        var elemValue = null;
        if ($(this).is('select')) elemValue = $(this).find('option:selected').val();
        else elemValue = this.value;
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(elemValue || '');
        } else {
            o[this.name] = elemValue || '';
        }
    });
    return o;
    }

$(document).ready(function(){
$("#simplepost").click(function(e)
{
var MyForm = $("#ajaxform");
var MyFormCrt = JSON.stringify(MyForm.serializeObject());
 $.ajax(
 {
 url : "context/jerseyws/addmsg",
 type: "POST",
 data : MyFormCrt,
 dataType: "json",
 contentType: "application/json",
 success:function(maindta)
 {

alert("Your message has been successfully sent! Thank you for your feedback.");

 },
 error: function(jqXHR, textStatus, errorThrown)
 {
 }
 });
 e.preventDefault(); //STOP default action

});
});

$.getJSON("/mapsstats", function(list) {
    var table = $('#tabletest');
    $.each(list, function(index, data) {
        $('<tr>').appendTo(table)
            .append($('<td>').text(data.filename))
            .append($('<td>').text(data.size))
            .append($('<td>').text(data.width))
            .append($('<td>').text(data.height))
            .append($('<td>').text(data.format));
    });
});

</script>

<br>
<br>

<p>Available maps statistics:</p>
<table style="width:100%" id="tabletest">
  <tr>
    <td>Map file name</td>
    <td>Map file size (bytes)</td> 
    <td>Map width</td>
    <td>Map height</td>
    <td>Image format</td>
  </tr>
</table>				

<br>

<p>Suggestions? Bugs? Please contact the developer!</p>
<form method="post" name="ajaxform" id ="ajaxform">
		<p>
			Name : <input type="text" name="username" />
		</p>
		<p>
			Message : <textarea rows="10" name="emailtext" cols="50">Enter your suggestions here. </textarea>
		</p>
<input type="button" class="btn btn-info" id="simplepost" value="Send message"></form>
	</form>

</body>
</html>
