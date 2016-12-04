[![Build Status](https://magnum.travis-ci.com/protowouter/FourCharm.svg?token=P6RyMRJqs6yXypzq1pt7&branch=master)](https://magnum.travis-ci.com/protowouter/FourCharm)
FourCharm
=========

FourCharm is a networked implementation of Connect4. For building our project and running our tests
Maven is required.

Building Fourcharm
------------------


    mvn install


Running unit test suite
-----------------------

    mvn test



Running integration test suite
------------------------------

    mvn verify



Running FourCharm
=================

The build of FourCharm generates a JAR file for our project. This single jar file can be used
to run the TUI, GUI and the server.


Starting the GUI
----------------

Running from commandline:

    java -jar FourCharm-vx.x.x-jar-with-dependencies.jar

The gui can also be started by starting right from your OS for instance by double clicking depending on your OS.

Starting the TUI
----------------

    java -jar FourCharm-vx.x.x-jar-with-dependencies.jar -c


Starting the server
-------------------

    java -jar FourCharm-vx.x.x-jar-with-dependencies.jar -s






