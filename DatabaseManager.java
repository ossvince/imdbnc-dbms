import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
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
            out = "Successfully read config file.";
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
            out = "ERR: Username and/or password is empty in auth.cfg file. Ending processing...";
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
            } catch (SQLException e) {
                e.printStackTrace();
                out = "ERR: Could not connect to SQL database.";
            }
        }
        return out;
    }

    public String createDatabase(String path) {
        String out = "";
        // check if tables exist first
        try {
            // https://docs.oracle.com/javase/8/docs/api/java/sql/DatabaseMetaData.html#getTables-java.lang.String-java.lang.String-java.lang.String-java.lang.String:A-
            DatabaseMetaData meta = this.connection.getMetaData();
            // if the Title table exists, we can assume there is existing data
            ResultSet results = meta.getTables(null, null, "Title", null);
            if (results.next()) {
                return "Tables already exist in the database. Please delete the existing database before trying to create a new one.";
            } else {
                this.schema = this.query.readSQL(path);
                for (String s : schema) {
                    try {
                        this.connection.createStatement().executeUpdate(s);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        out = "ERR: Database creation failed executing update!";
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out = "ERR: SQL error when checking JDBC connection metadata!";
        }
        return out;
    }

    public String updateDatabaseByLine(String path) {
        String out = "";
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String currLine = br.readLine();
            while(currLine != null) {
                System.out.println("Executing update " + currLine);
                this.connection.prepareStatement(currLine).executeUpdate();
                currLine = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            out = "ERR: Could not find required .sql file to execute updates.";
        } catch (SQLException e) {
            e.printStackTrace();
            out = "ERR: SQL failed while executing update!";
        }
        return out;   
    }

    public String deleteDatabase(String path) {
        String out = "";
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String currLine = br.readLine();
            while(currLine != null) {
                this.connection.prepareStatement(currLine).executeUpdate();
            }
        } catch (IOException e) {
            e.printStackTrace();
            out = "ERR: Could not find required .sql file to drop tables.";
        } catch (SQLException e) {
            e.printStackTrace();
            out = "ERR: Database deletion failed executing update!";
        }
        return out;
    }

    public String populateDatabase(String path) {
        String out = "";
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String currLine = br.readLine();
            while(currLine != null) {
                this.connection.prepareStatement(currLine).executeUpdate();
            }
        } catch (IOException e) {
            e.printStackTrace();
            out = "ERR: Could not read database population file!";
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            out = "ERR: Database repopulation failed executing update!";
        }
        return out;
    }
}