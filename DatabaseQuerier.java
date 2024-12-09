
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DatabaseQuerier {
    private Connection connection;
    private ArrayList<String> queries;

    public DatabaseQuerier(Connection connection, String querypath) {
        this.connection = connection;
        this.queries = readSQL(querypath);
    }
    
    // QueryReader
    public ArrayList<String> readSQL(String path) {
        ArrayList<String> statements = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String currLine = br.readLine();
            String currQuery = "";
            while(currLine != null) {
                if (!currLine.trim().endsWith(";")) {
                    currQuery += currLine + " ";
                } else {
                    currQuery += currLine;
                    statements.add(currQuery);
                    currQuery = "";
                }
                currLine = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            System.out.println("ERR: Could not read SQL file " + path);
        }
        return statements;
    }

    private int getQueryIndex(String queryName){
        int result = -1;
        for (int i=0; i < queries.size(); i++){
            if( queries.get(i).toUpperCase().contains(queryName.toUpperCase())){
                result = i;
                break;
            }
        }
        return result;
    }
    // Queries

    public String topTenMovies() {
        String out = "";
        ResultSet results = null;
        try (Statement stmt = this.connection.createStatement();) {
            int queryIndex = getQueryIndex("topTenMovies");
            if (queryIndex > -1) {
                results = stmt.executeQuery(this.queries.get(queryIndex));
                int idx = 0; 
                while(results.next()) {
                    idx++;
                    System.out.println("  " + Integer.toString(idx) + ". " + results.getString(1) 
                        + ", " + results.getString(2) 
                        + "/10 (" + results.getString(3) + " total user ratings)");
                }
                out = "Query completed.";
            } else {
                out = "ERR: could not find query in filesystem!";
            }
        } catch (SQLException sqle) {
            out = "ERR: SQL/Database connection failed to execute query. Does the data exist?";
        }
        return out;
    }

    public String topTenActingCredits() {
        String out = "";
        ResultSet results = null;
        try (Statement stmt = this.connection.createStatement();) {
            int queryIndex = getQueryIndex("topTenActors");
            if (queryIndex > -1) {
                results = stmt.executeQuery(this.queries.get(queryIndex));
                int idx = 0;
                while(results.next()) {
                    idx++;
                    System.out.println("  " + Integer.toString(idx) + ". " + results.getString(1) 
                        + " has acted in " + results.getString(2) + " titles");
                }
                out = "Query completed.";
            } else {
                out = "ERR: could not find query in filesystem!";
            }
        } catch (SQLException sqle) {
            out = "ERR: SQL/Database connection failed to execute query. Does the data exist?";
        }
        return out;
    }

    public String directedTitles(String name) {
        String out = "";
        ResultSet results = null;
        try  {
            int queryIndex = getQueryIndex("directedTitles");
            if (queryIndex > -1) {
                PreparedStatement pstmt = this.connection.prepareStatement(this.queries.get(queryIndex));
                pstmt.setString(1, name);
                results = pstmt.executeQuery();

                out += name + " has directed the following titles:\n";
                
                while(results.next()) {
                    out += "  " + results.getString(1) + "\n";
                }
                out += "Query completed.";

            } else {
                out = "ERR: could not find query in filesystem!";
            }
        } catch (SQLException sqle) {
            out = "ERR: SQL/Database connection failed to execute query. Does the data exist?";
        }
        return out;
    }

    public String movieAssociates(String input) {
        String out = "";
        int queryIndex = getQueryIndex("movieAssociates");
        if (queryIndex > -1) {
            try {
                PreparedStatement pstmt = this.connection.prepareStatement(this.queries.get(queryIndex));
                pstmt.setString(1, input);
                pstmt.setString(2, input);
                ResultSet results = pstmt.executeQuery();

                out += "Listing associated people with title " + input + ":\n";

                while(results.next()) {
                    out += "  " + results.getString(2) + " ID: " + results.getString(1) + "\n";
                }
                out += "Query completed. ";
            } catch (SQLException sqle) {
                out = "ERR: SQL/Database connection failed to execute query. Does the data exist?";
            }
        } else {
            out = "ERR: could not find query in filesystem!";
        }
        return out;
    }

    public String titlesIn(String name) {
        String out = "";
        try {
            ResultSet results = null;
            int queryIndex = getQueryIndex("titlesIn");
            if (queryIndex > -1) {
                PreparedStatement pstmt = this.connection.prepareStatement(this.queries.get(queryIndex));
                pstmt.setString(1, name);
                pstmt.setString(2, name);
                pstmt.setString(3, name);

                results = pstmt.executeQuery();
                out += "Listing all titles " + name + " was a part of: \n";
                while(results.next()) {
                    out += "  " + results.getString(1);
                    if(!results.getString(1).equalsIgnoreCase(results.getString(2))){
                        out += " (" + results.getString(2) + ")\n";
                    }
                    out +=" ID: " + results.getString(3) + "\n";
                }
                out += "Query completed.";
            } else {
                out = "ERR: could not find query in filesystem!";
            }
        } catch (SQLException sqle) {
            out = "ERR: SQL/Database connection failed to execute query. Does the data exist?";
        }
        return out;
    }

    public String moviesKnownFor(String name) {
        String out = "";
        try {
            ResultSet results = null;
            int queryIndex = getQueryIndex("moviesKnownFor");
            if (queryIndex > -1) {
                PreparedStatement pstmt = this.connection.prepareStatement(this.queries.get(queryIndex));
                pstmt.setString(1, name);
                results = pstmt.executeQuery();
                
                out += "Listing movies that '"+ name + "' is known for: \n";
                
                while (results.next()) {
                    out += "  " + results.getString(1);
                    if(!results.getString(1).equalsIgnoreCase(results.getString(2))){
                        out += "(" + results.getString(2) + ")";
                    }
                    out += "\n";
                }
                out += "Query completed.";
            } else {
                out = "ERR: could not find query in filesystem!";
            }
        } catch (SQLException sqle) {
            out = "ERR: SQL/Database connection failed to execute query. Does the data exist?";
        }
        return out;
    }

    public String seriesKnownFor(String name) {
        String out = "";
        try {
            ResultSet results = null;
            int queryIndex = getQueryIndex("seriesKnownFor");
            if (queryIndex > -1) {
                PreparedStatement pstmt = this.connection.prepareStatement(this.queries.get(queryIndex));
                pstmt.setString(1, name);
                results = pstmt.executeQuery();
                
                out += "Listing TV series that '"+ name + "' is known for: \n";
                
                while (results.next()) {
                    out += "  " + results.getString(1);
                    if (!results.getString(1).equalsIgnoreCase(results.getString(2))){
                        out += "(" + results.getString(2) + ")";
                    }
                    out += "\n";
                }
                out += "Query completed.";
            } else {
                out = "ERR: could not find query in filesystem!";
            }

        } catch (SQLException sqle) {
            out = "ERR: SQL/Database connection failed to execute query. Does the data exist?";
        }
        return out;
    }
    public String findPerson(String name) {
        String out = "";
        try {
            ResultSet results = null;
            int queryIndex = getQueryIndex("findPerson");
            if (queryIndex > -1) {
                PreparedStatement pstmt = this.connection.prepareStatement(this.queries.get(queryIndex));
                pstmt.setString(1, "%"+name+"%");
                results = pstmt.executeQuery();
                
                out += "Searching for person '"+ name + "': \n";
                
                while (results.next()) {
                    out += "  ID " + results.getString(2) + ", " + results.getString(1); 
                    if (results.getInt(3) != 0) { // getInt returns 0 if NULL
                        out += ", born " + Integer.toString(results.getInt(3));
                    }
                    if (results.getInt(4) != 0) { 
                        out += ", died " + Integer.toString(results.getInt(4));
                    }
                    if (results.getInt(5) != 0) {
                        out += ", age " + Integer.toString(results.getInt(5));
                    }
                    out += "\n";
                }
                out += "Query completed.";
            } else {
                out = "ERR: could not find query in filesystem!";
            }

        } catch (SQLException sqle) {
            out = "ERR: SQL/Database connection failed to execute query. Does the data exist?";
        }
        return out;
    }
    public String findTitle(String name) {
        String out = "";
        try {
            ResultSet results = null;
            int queryIndex = getQueryIndex("findTitle");
            if (queryIndex > -1) {
                PreparedStatement pstmt = this.connection.prepareStatement(this.queries.get(queryIndex));
                pstmt.setString(1, "%" + name + "%");
                pstmt.setString(2, "%" + name + "%");
                results = pstmt.executeQuery();
                
                out += "Searching for title '"+ name +"' \n";
                
                while (results.next()) {
                    out += "  Found " + results.getString(1);
                    if (!results.getString(1).equalsIgnoreCase(results.getString(2))) {
                        out += " (" + results.getString(2) + ")";
                    }
                    out += ", id: " + results.getString(3) + ", runtime: " + results.getString(4) + "min" + ", start year: "+ results.getString(5);
                    if (results.getInt(6) != 0) {
                        out += ", is an adult title";
                    }
                    out += "\n";
                }
                out += "Query completed.";
            } else {
                out = "ERR: could not find query in filesystem!";
            }

        } catch (SQLException sqle) {
            out = "ERR: SQL/Database connection failed to execute query. Does the data exist?";
        }
        return out;
    }
    public String getRatings(String input) {
        String out = "";
        int queryIndex = getQueryIndex("getRatings");
        if (queryIndex > -1) {
            try {
                PreparedStatement pstmt = this.connection.prepareStatement(this.queries.get(queryIndex));
                pstmt.setString(1, input);
                pstmt.setString(2, input);
                ResultSet results = pstmt.executeQuery();
                while (results.next()) {
                    out += "  " + input + " has an average user rating of " + results.getString(1) + " with " + results.getString(2) + " total votes\n" ;
                }
            out += "Query completed.";
            } catch (SQLException sqle) {
                out = "ERR: SQL/Database connection failed to execute query. Does the data exist?";
            }
        } else {
            out = "ERR: could not find query in filesystem!";
        }
        return out;
    }
    public String getProfessionals(String profName) {
        String out = "";
        try {
            ResultSet results = null;
            int queryIndex = getQueryIndex("getProfessionals");
            if (queryIndex > -1) {
                PreparedStatement pstmt = this.connection.prepareStatement(this.queries.get(queryIndex));
                pstmt.setString(1, profName);
                results = pstmt.executeQuery();
                
                out += "Listing all people with profession '"+profName+"': \n";
                
                while (results.next()) {
                    out += "  " + results.getString(1) + " (id "+ results.getString(2) + ") has profession " + results.getString(3) + "\n";
                }
                out += "Query completed.";
            } else {
                out = "ERR: could not find query in filesystem!";
            }

        } catch (SQLException sqle) {
            out = "ERR: SQL/Database connection failed to execute query. Does the data exist?";
        }
        return out;
    }
    public String listSeriesEpisodes(String input) {
        String out = "";
        int queryIndex = getQueryIndex("listSeriesEpisodes");
        try {
            // String seriesName = input;
            if (queryIndex > -1) {
                try {
                    PreparedStatement pstmt = this.connection.prepareStatement(this.queries.get(queryIndex));
                    pstmt.setString(1, input);
                    pstmt.setString(2, input);
                    ResultSet results = pstmt.executeQuery();

                    out += "Listing all episodes in series '"+ input +"' \n";

                    while (results.next()) {
                        out += "  Episode: " + results.getString(2);
                        if (!results.getString(2).equalsIgnoreCase(results.getString(3))) {
                            out += " (" + results.getString(3) + ")";
                        }
                        out += ", id: " + results.getString(1) + ", aired " + results.getString(4) + ", runtime: "+ results.getString(5);
                        if (results.getInt(6) != 0) {
                            out += ", is an adult title";
                        }
                        
                        out += ", episode ID: " + Integer.toString(results.getInt(7)) + "\n";
                    }
                    out += "Query completed.";
                } catch (SQLException sqle) {
                    out = "ERR: SQL/Database connection failed to execute query. Does the data exist?";
                }
            } else {
                out = "ERR: could not find query in filesystem!";
            }
        } catch (NumberFormatException e) {
            out = "ERR: Invalid input - ID must be an integer";
        }
        return out;
    }
    public String seriesMainCast(String input) {
        String out = "";
        int queryIndex = getQueryIndex("seriesMainCast");
        try {
            if (queryIndex > -1) {
                try {
                    PreparedStatement pstmt = this.connection.prepareStatement(this.queries.get(queryIndex));
                    pstmt.setString(1, input);
                    pstmt.setString(2, input);
                    pstmt.setString(3, input);
                    pstmt.setString(4, input);

                    ResultSet results = pstmt.executeQuery();
                    
                    out += "Listing people who have appeared in every episode of series "+ input + "\n";
                    
                    while (results.next()) {
                        out += "  " + results.getString(2) + ", ID " + results.getString(1); 
                        if (results.getInt(3) != 0) { // getInt returns 0 if NULL
                            out += ", born " + Integer.toString(results.getInt(3));
                        }
                        if (results.getInt(4) != 0) { 
                            out += ", died " + Integer.toString(results.getInt(4));
                        }
                        if (results.getInt(5) != 0) {
                            out += ", age " + Integer.toString(results.getInt(5)) + "\n";
                        }
                    }
                    out += "Query completed.";
                } catch (SQLException e) {
                    out = "ERR: SQL/Database connection failed to execute query. Does the data exist?";
                }
            }
        } catch (NumberFormatException e) {
            out = "ERR: Invalid input - ID must be an integer";
        }
        return out;
    }

    public String listCastAndRoles(String input) {
        String out = "";
        int queryIndex = getQueryIndex("listCastAndRoles");
        if (getQueryIndex("listCastAndRoles") > -1) {
            try {
                PreparedStatement pstmt = this.connection.prepareStatement(this.queries.get(queryIndex));
                pstmt.setString(1, input);
                pstmt.setString(2, input);

                ResultSet results = pstmt.executeQuery();
                
                out += "Listing cast and roles for title "+input+"\n";
                
                while (results.next()) {
                    out += "  " + results.getString(2) + " (personID " + results.getString(1) + ") played character " + results.getString(3) + "\n";
                }
                out += "Query completed.";
            } catch (SQLException e) {
                out = "ERR: SQL/Database connection failed to execute query.";
            }
        }
        return out;
    }

    public String listAllProfessions() {
        String out = "";
        try (Statement stmt = this.connection.createStatement();){
            ResultSet results = null;
            int queryIndex = getQueryIndex("listAllProfessions");
            if (queryIndex > -1) {
                results = stmt.executeQuery(this.queries.get(queryIndex));
                while (results.next()) {
                    out += "  Profession: " + results.getString(1) + "\n";
                }
                out += "Query completed.";                
            } else {
                out = "ERR: could not find query in filesystem!";
            }
            
        } catch (SQLException sqle) {
            out = "ERR: SQL/Database connection failed."; 
        }
        return out;
    }
}