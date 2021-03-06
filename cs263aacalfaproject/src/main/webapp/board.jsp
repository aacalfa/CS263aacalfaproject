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
		<title>Strategy Board</title>
		<meta http-equiv="refresh" content="60" >
</head>

<body>


<%
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user == null)
			response.sendRedirect("/");

		// Check if we have a valid blob-key for the map image
		String blobKeystr = request.getParameter( "blob-key" );
		if ( blobKeystr == null )
			response.sendRedirect("/");

		String radioValue = (String)request.getAttribute("attrname");
%>

<script language="JavaScript1.2">
<!--

// Detect if the browser is IE or not.
// If it is not IE, we assume that the browser is NS.
var IE = document.all?true:false

// If NS -- that is, !IE -- then set up for mouse capture
if (!IE) document.captureEvents(Event.MOUSEMOVE)

// Set-up to use addMarkerXY function on double click
document.ondblclick = addMarkerXY;
document.onclick = removeMarkerXY;

// Temporary variables to hold mouse x-y pos.s
var tempX = 0
var tempY = 0

function removeMarkerXY(e) {
	if (IE) { // grab the x-y pos.s if browser is IE
		tempX = event.clientX + document.body.scrollLeft
		tempY = event.clientY + document.body.scrollTop
	} else {  // grab the x-y pos.s if browser is NS
		tempX = e.pageX
		tempY = e.pageY
	}  
	// catch possible negative values in NS4
	if (tempX < 0){tempX = 0}
	if (tempY < 0){tempY = 0}  

	// show the position values in the form named Show
	// in the text fields named MouseX and MouseY
	// Image starts with an offset of 10 pixels in x and y dimensions.
	tempX -= 10 
	tempY -= 10 

	// Call board servlet to remove specific marker
	var blobkey ="<%=blobKeystr%>";  
	var mapimage = document.getElementById("map");
	var imgsrc = "boardshow?blob-key="+ blobkey +"&attrname=DELETE" + "&xcoord=" + tempX + "&ycoord=" + tempY;
	mapimage.src = imgsrc;
	return true
}

// Add marker at current XY mouse pointer position
function addMarkerXY(e) {
	if (IE) { // grab the x-y pos.s if browser is IE
		tempX = event.clientX + document.body.scrollLeft
		tempY = event.clientY + document.body.scrollTop
	} else {  // grab the x-y pos.s if browser is NS
		tempX = e.pageX
		tempY = e.pageY
	}  
	// catch possible negative values in NS4
	if (tempX < 0){tempX = 0}
	if (tempY < 0){tempY = 0}  

	// show the position values in the form named Show
	// in the text fields named MouseX and MouseY
	// Image starts with an offset of 10 pixels in x and y dimensions.
	tempX -= 10
	tempY -= 10

	// Get selected radio value
	var blobkey ="<%=blobKeystr%>";
	var attribute = "sword";
	var radios = document.getElementsByName('radios');

	// Find out which radio is checked
	for (var i = 0, length = radios.length; i < length; i++) {
		if (radios[i].checked) {
				// Found the checked radio
				attribute = radios[i].value;
				// only one radio can be logically checked, don't check the rest
				break;
		}
	}
	// If the squad radio is checked, need to get value from dropdown list
	if(attribute == "squad") {
	 var ddData = $('#squadDropdown').data('ddslick');
		attribute = ddData.selectedData.text;
		var correctAttrStr = "letter" + attribute;
		attribute = correctAttrStr;
	}
	// Update image with new attribute
	var mapimage = document.getElementById("map");
	var imgsrc = "boardshow?blob-key="+ blobkey +"&attrname=" + attribute + "&xcoord=" + tempX + "&ycoord=" + tempY;
	mapimage.src = imgsrc;

	return true
}

//-->
</script>
<img src="boardshow?blob-key=<%= blobKeystr%>&attrname=<%= radioValue%>" id="map" />


<H2>Select one of the available markers and double click on the map to add it (Single click on it to remove).</H2>
				<FORM ACTION="boardshow?blob-key=<%= blobKeystr%>" METHOD="post">
						 <INPUT TYPE="radio" NAME="radios" VALUE="sword" id="att" CHECKED>
						 Attack
						 <label for="att"><img src="sword.png" /></label>
						<tab>
						<INPUT TYPE="radio" NAME="radios" VALUE="shield" id="def">
						 Defend
						 <label for="def"><img src="shield.png" /></label>
						<tab>
						<INPUT TYPE="radio" NAME="radios" VALUE="evac" id="evac">
						 Evac Order
						 <label for="evac"><img src="evac.png" /></label>
						<tab>
						<INPUT TYPE="radio" NAME="radios" VALUE="flag" id="flag">
						 Objective
						 <label for="flag"><img src="flag.png" /></label>
						<tab>
						<INPUT TYPE="radio" NAME="radios" VALUE="squad" id="squad">
						 Squad
						 <label for="squad"></label>
				</FORM>

<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
<script type="text/javascript" src="jquery.ddslick.min.js"></script>

<div id="squadDropdown" style="display:none"></div>

<script language="JavaScript1.2">
var ddData = [
		{
				text: "A",
				value: 1,
				selected: true,
				description: "Squad A",
				imageSrc: "https://dl.dropboxusercontent.com/u/25990563/letterA.png"
		},
		{
				text: "B",
				value: 2,
				selected: false,
				description: "Squad B",
				imageSrc: "https://dl.dropboxusercontent.com/u/25990563/letterB.png"
		},
		{
				text: "C",
				value: 3,
				selected: false,
				description: "Squad C",
				imageSrc: "https://dl.dropboxusercontent.com/u/25990563/letterC.png"
		},
		{
				text: "D",
				value: 4,
				selected: false,
				description: "Squad D",
				imageSrc: "https://dl.dropboxusercontent.com/u/25990563/letterD.png"
		},
		{
				text: "E",
				value: 5,
				selected: false,
				description: "Squad E",
				imageSrc: "https://dl.dropboxusercontent.com/u/25990563/letterE.png"
		},
		{
				text: "F",
				value: 6,
				selected: false,
				description: "Squad F",
				imageSrc: "https://dl.dropboxusercontent.com/u/25990563/letterF.png"
		},
		{
				text: "G",
				value: 7,
				selected: false,
				description: "Squad G",
				imageSrc: "https://dl.dropboxusercontent.com/u/25990563/letterG.png"
		},
];

$('#squadDropdown').ddslick({
		data: ddData,
		width: 300,
		imagePosition: "left",
		selectText: "Select the squad",
		onSelected: function (data) {
				//console.log(data);
		}
});
</script>
</br>
<p>Click the button below to delete all markers.</p>
<form action="context/jerseyws/deleteall?currMap=<%= blobKeystr%>" method="post">
<button type="submit">Delete</button>
</form>

</br>
<p>Click the link below to go back to main menu.</p>
<a class="btn" href="menu.jsp">Back</a>
</body>

</html>

