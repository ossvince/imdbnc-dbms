import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseManager {
    private Connection connection;
    private FileInputStream authFile;
    private String authPath;
    private Properties authProp;

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
            out = "ERR: Could not find config file.";
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
            } catch (SQLException e) {
                e.printStackTrace();
                out = "ERR: Could not connect to SQL database.";
                System.out.println(out);
            }
        }

        return out;
    }

    // TODO: population and deletion of tables with .csv
    // might also move this monstrosity to its own class file
    private void createPersonTable() throws SQLException, IOException {
        try {
            String update = "CREATE TABLE Person("
                + "personID INT IDENTITY(1,1) PRIMARY KEY,"
                + "name VARCHAR NOT NULL,"
                + "age INT,"
                + "birthYear INT,"
                + "deathYear INT);";
            this.connection.createStatement().executeUpdate(update);
            // TODO: .csv files should be in a directory
            // TODO: reading from .csv
            BufferedReader br = new BufferedReader(new FileReader("data/person.csv"));
            PreparedStatement pstmt;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new SQLException("ERR: Could not create or insert into Person table"); 
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new IOException("ERR: Could not find data file");
        }
    }

    private void createTitleTable() throws SQLException, IOException {
        try {
            String update = "CREATE TABLE Title("
                + "titleID INT IDENTITY(1,1) PRIMARY KEY,"
                + "primaryTitle VARCHAR NOT NULL,"
                + "originalTitle VARCHAR NOT NULL,"
                + "startYear INT,"
                + "runTime NUMERIC NOT NULL,"
                + "isAdult BIT NOT NULL);";
            this.connection.createStatement().executeUpdate(update);
            BufferedReader br = new BufferedReader(new FileReader("data/person.csv"));
            PreparedStatement pstmt;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new SQLException("ERR: Could not create or insert into Title table"); 
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new IOException("ERR: Could not find data file");
        }
    }

    private void createProfessionTable() throws SQLException, IOException {
        try {
            String update = "CREATE TABLE Profession("
                + "professionName VARCHAR PRIMARY KEY);";
            this.connection.createStatement().executeUpdate(update);
            BufferedReader br = new BufferedReader(new FileReader("data/person.csv"));
            PreparedStatement pstmt;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new SQLException("ERR: Could not create or insert into Profession table"); 
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new IOException("ERR: Could not find data file");
        }
    }

    private void createHasProfessionTable() throws SQLException {
        try {
            String update = "CREATE TABLE HasProfession("
                + "personID INT REFERENCES Person ON DELETE CASCADE,"
                + "professionName VARCHAR FOREIGN KEY REFERENCES Profession ON DELETE CASCADE,"
                + "PRIMARY KEY (personID));";
            this.connection.createStatement().executeUpdate(update);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new SQLException("ERR: Could not create or insert into HasProfession table");
        }
    }

    private void createAssociatedWithTable() throws SQLException {
        try {
            String update = "CREATE TABLE AssociatedWith("
                + "personID INT REFERENCES Person ON DELETE CASCADE,"
                + "titleID INT REFERENCES Title ON DELETE CASCADE,"
                + "flag VARCHAR,"
                + "CHECK (flag in ('KnownFor', 'Directed', 'Wrote')),"
                + "PRIMARY KEY (personID, titleID, flag));";
            this.connection.createStatement().executeUpdate(update);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new SQLException("ERR: Could not create or insert into table");
        }
    }

    private void createWorksOnTable() throws SQLException {
        try {
            String update = "CREATE TABLE WorksOn("
                + "personID INT REFERENCES Person ON DELETE CASCADE,"
                + "titleID INT REFERENCES Title ON DELETE CASCADE,"
                + "jobCategory VARCHAR NOT NULL,"
                + "jobName VARCHAR NOT NULL,"
                + "PRIMARY KEY (personID, titleID));";
            this.connection.createStatement().executeUpdate(update);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new SQLException("ERR: Could not create or insert into table");
        }
    }

    private void createActsInTable() throws SQLException {
        try {
            String update = "CREATE TABLE ActsIn("
                + "personID INT REFERENCES Person ON DELETE CASCADE,"
                + "titleID INT REFERENCES Title ON DELETE CASCADE,"
                + "character VARCHAR NOT NULL unique,"
                + "PRIMARY KEY (personID, titleID));";
            this.connection.createStatement().executeUpdate(update);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new SQLException("ERR: Could not create or insert into table");
        }
    }

    private void createTitleRatingTable() throws SQLException {
        try {
            String update = "CREATE TABLE TitleRating("
                + "titleID INT REFERENCES Title ON DELETE CASCADE,"
                + "avgRating NUMERIC NOT NULL,"
                + "numVotes INT NOT NULL,"
                + "PRIMARY KEY (titleID, avgRating, numVotes));";
            this.connection.createStatement().executeUpdate(update);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new SQLException("ERR: Could not create or insert into table");
        }
    }

    private void createTVSeriesTable() throws SQLException {
        try {
            String update = "CREATE TABLE TVSeries("
                + "seriesID INT IDENTITY(1,1),"
                + "titleID INT FOREIGN KEY REFERENCES Title ON DELETE CASCADE,"
                + "PRIMARY KEY (seriesID));";
            this.connection.createStatement().executeUpdate(update);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new SQLException("ERR: Could not create or insert into table");
        }
    }

    private void createTVEpisodeTable() throws SQLException {
        try {
            String update = "CREATE TABLE TVEpisode("
                + "episodeID INT IDENTITY(1,1),"
                + "titleID INT FOREIGN KEY REFERENCES Title ON DELETE CASCADE,"
                + "seriesID INT FOREIGN KEY REFERENCES TVSeries,"
                + "PRIMARY KEY (episodeID));";
            this.connection.createStatement().executeUpdate(update);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new SQLException("ERR: Could not create or insert into table");
        }
    }

    private void createMovieTable() throws SQLException {
        try {
            String update = "CREATE TABLE Movie("
                + "movieID INT IDENTITY(1,1),"
                + "titleID INT FOREIGN KEY REFERENCES Title ON DELETE CASCADE,"
                + "PRIMARY KEY (movieID));";
            this.connection.createStatement().executeUpdate(update);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new SQLException("ERR: Could not create or insert into table");
        }
    }

    // Queries

    public String queryTopTenMovies() {
        String out = "";
        ResultSet results = null;
        try (Statement stmt = this.connection.createStatement();) {
            String query = "SELECT m.primaryTitle, tr.avgRating, tr.numVotes "
                + "FROM Title AS t "
                + "JOIN TitleRating AS tr ON t.titleID = tr.titleID "
                + "JOIN Movie AS m ON m.titleID = tr.titleID "
                + "WHERE tr.numVotes >= 5000 "
                + "ORDER BY tr.avgRating DESC "
                + "LIMIT 10;";
            results = stmt.executeQuery(query);
            while(results.next()) {
                System.out.println(results.getString(1) 
                    + ", " + results.getString(2) 
                    + ", " + results.getString(3));
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            out = "ERR: SQL/Database connection failed.";
        }
        return out;
    }

}