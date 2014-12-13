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

If you do not wish to upload any map images to the application, just click the submit/View uploaded maps button.

The selenium tests are located in the selenium folder. In order to run them, you must be logged in the app and in the main menu page (menu.jsp). Please be sure to run the tests in the slowest speed.
