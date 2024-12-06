DIR_BIN = bin
DIR_LIB = lib

build:
	javac -d $(DIR_BIN) -cp $(DIR_BIN) *.java

run: build
	java -cp $(DIR_BIN):$(DIR_LIB)/mssql-jdbc-12.8.1.jre11.jar Interface

clean:
	rm -rf $(DIR_BIN)
