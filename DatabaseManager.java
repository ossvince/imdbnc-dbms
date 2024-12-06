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
        } catch (FileNotFoundException e) {
            out = "ERR: Could not find config file. Does auth.cfg exist?";
            System.out.println(out);
            System.exit(1);
        } catch (IOException e) {
            out = "ERR: Could not read config file.";
            System.out.println(out);
        }
        return out;
    }

    public String connect() {
        String out = null;
        this.readConfig();
        String dbuser = this.authProp.getProperty("username");
        String dbpass = this.authProp.getProperty("password");
        if (dbuser == null || dbpass == null) {
            out = "ERR: Username or password is empty.";
            System.out.println(out);
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
                this.query = new DatabaseQuerier(this.connection, "queries.sql");
            } catch (SQLException e) {
                e.printStackTrace();
                out = "ERR: Could not connect to SQL database.";
                System.out.println(out);
            }
        }

        return out;
    }

    public String createDatabase(String path) {
        String out = "";
        // read the create_database.sql 
        this.schema = this.query.readSQL(path);
        for (String s : schema) {
            try {
                this.connection.createStatement().executeUpdate(s);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        // match by "TableName(" to get the first line of the string
        return out;
    }

    public String populateDatabase(String path) {
        String out = "";
        // read the PopulateDatabase.sql line by line 
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