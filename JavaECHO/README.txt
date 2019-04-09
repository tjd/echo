
Note about the directory contents and running JavaECHO:

JavaECHO is written using Java's packaging convention.
The following folders:

JavaECHO
debug
iterator
predicate
utils

contain the source code for the model. I have written a batch file called
makeJar.bat that adds all the *.class files from the source code directories
into a Java JAR file called javaEcho.jar. When you run the echoApplet.html
web page (assuming you have a browser capable of running JAVA), the program is
read from the javaEcho.jar file. 

NOTE: if you are making no changes to the source code, all you should need to
do is open echoApplet.html, and the JavaECHO model will load from the
javaEcho.jar file and run in your browser.
