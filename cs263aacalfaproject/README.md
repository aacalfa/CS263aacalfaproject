App Engine Java Strategy Board

## UC Santa Barbara Fall 2014
## CS263 Modern Programming Language Runtime Systems Project

Requires [Apache Maven](http://maven.apache.org) 3.1 or greater, and JDK 7+ in order to run.

To build, run

    mvn package

Building will run the tests, but to explicitly run tests you can use the test target

    mvn test

To start the app, use the [App Engine Maven Plugin](http://code.google.com/p/appengine-maven-plugin/) that is already included in this demo.  Just run the command.

    mvn appengine:devserver

To see all the available goals for the App Engine plugin, run

    mvn help:describe -Dplugin=appengine

After logging in to the application, if you do not wish to upload any map images to the application, just click the submit/View uploaded maps button.

In menu.jsp, select one of the available maps to start your strategy. After loading the map image, you can add a marker by double clicking in the desired location on the map. To change which marker you wish to add, just select one of the radio values below the map. If you select the "squad" value, you can choose which letter you want by clicking on the dropdown list below it. To delete a specific marker, just do a single click on it. To delete all markers of a map, click on the "delete" button at the bottom of the page.

The selenium tests are located in the selenium folder. In order to run them, you must be logged in the app and in the main menu page (menu.jsp). Please be sure to run the tests in the slowest speed.

The available tests are:

InputTest: opens a map and adds several markers to it.

ErrorsTest: opens a map and tries to add invalid markers (invalid coordinates)

FeedbackTest: Tests the feedback email feature. Writes a message and sends it.

The Javadocs are available in doc/index.html
