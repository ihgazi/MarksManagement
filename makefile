main: MarksMgmt.java slf4j-api-1.7.36.jar sqlite-jdbc-3.45.2.0.jar marksmgmt.db
	javac MarksMgmt.java
	java -classpath ".:sqlite-jdbc-3.45.2.0.jar:slf4j-api-1.7.36.jar" MarksMgmt

