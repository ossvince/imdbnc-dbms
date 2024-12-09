import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

public class DatabaseManager {
    private Connection connection;
    private FileInputStream authFile;
    private String authPath;
    private Properties authProp;
    DatabaseQuerier query;
    private ArrayList<String> schema;

    public DatabaseManager(String path) {
        this.authPath = path;
    }

    private String readConfig() {
        String out = null;
        try {
            this.authFile = new FileInputStream(authPath);
            this.authProp = new Properties();
            authProp.load(this.authFile);
            this.authFile.close();
            out = "...successfully read config file...";
        } catch (FileNotFoundException e) {
            out = "ERR: Could not find config file. Does auth.cfg exist?";
            System.exit(1);
        } catch (IOException e) {
            out = "ERR: Could not read config file.";
        }
        return out;
    }

    public String connect() {
        String out = null;
        System.out.println(this.readConfig());
        String dbuser = this.authProp.getProperty("username");
        String dbpass = this.authProp.getProperty("password");
        if (dbuser == null || dbpass == null) {
            System.out.println("ERR: Username and/or password is empty in auth.cfg file. Ending processing...");
            System.exit(1);
        } else {
            String connectionUrl = "jdbc:sqlserver://uranium.cs.umanitoba.ca:1433;"
                + "database=cs3380;"
                + "user=" + dbuser + ";"
                + "password="+ dbpass +";"
                + "encrypt=false;"
                + "trustServerCertificate=false;"
                + "loginTimeout=30;";
            try { 
                this.connection = DriverManager.getConnection(connectionUrl);
                // set up DatabaseQuerier
                this.query = new DatabaseQuerier(this.connection, "sql/queries.sql");
                out = "...successfully connected to SQL database.";
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("ERR: Could not connect to SQL database.");
                System.exit(1);
            }
        }
        return out;
    }

    public String closeConnection() {
        String out = "";
        Boolean isClosed = false;
        try {
            this.connection.commit();
            this.connection.close();
            isClosed = this.connection.isClosed();
            out = "Disconnect successful? " + Boolean.toString(isClosed);
        } catch (SQLException e) {
            e.printStackTrace();
            out = "ERR: SQL error when trying to close the connection.";
        }
        return out; 
    }

    public String createDatabase(String path) {
        String out = "Reading schema...";
        this.schema = this.query.readSQL(path);
        for (String s : schema) {
            try {
                this.connection.createStatement().executeUpdate(s);
            } catch (SQLException e) {
                e.printStackTrace();
                out = "ERR: Database creation failed executing update!";
            }
        }
        out += " complete.";
        return out;
    }

    public String updateDatabaseByLine(String path) {
        String out = "Executing database updates...";
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String currLine = br.readLine();
            String statement = "";
            // batch into one statement
            this.connection.setAutoCommit(false);
            String currSchema = "";
            while(currLine != null) {
                // System.out.println("Reading update " + currLine);
                if (currLine.startsWith("INSERT INTO") && !currSchema.equalsIgnoreCase(currLine.substring(11, currLine.indexOf("(")).trim())) {
                    currSchema = currLine.substring(11, currLine.indexOf("(")).trim(); 
                    out += "\n...inserting into " + currSchema + " table...";
                }
                statement += currLine;
                currLine = br.readLine();
            }
            this.connection.prepareStatement(statement).executeUpdate();
            this.connection.commit();
            this.connection.setAutoCommit(true);
        } catch (IOException e) {
            e.printStackTrace();
            out = "ERR: Could not find required .sql file to execute updates.";
        } catch (SQLException e) {
        }
        out += " complete.";
        return out;   
    }

}