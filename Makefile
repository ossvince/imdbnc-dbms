build:
	javac *.java

run: build
	java -cp .:mssql-jdbc-11.2.0.jre18.jar Interface.app

clean:
	rm *.class
