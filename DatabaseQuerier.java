
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
                    System.out.println("added query: "+ currQuery);
                    currQuery = "";
                }
                currLine = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return statements;
    }

    // Queries

    public String topTenMovies() {
        String out = "";
        ResultSet results = null;
        try (Statement stmt = this.connection.createStatement();) {
            int queryIndex = this.queries.indexOf("topTenMovies");
            if (queryIndex > -1) {
                results = stmt.executeQuery(this.queries.get(queryIndex));
                
                while(results.next()) {
                    System.out.println(results.getString(1) 
                        + ", " + results.getString(2) 
                        + ", " + results.getString(3));
                }
                out = "Query completed.";
            } else {
                out = "ERR: could not find query in filesystem!";
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            out = "ERR: SQL/Database connection failed.";
        }
        return out;
    }

    public String topTenActingCredits() {
        String out = "";
        ResultSet results = null;
        try (Statement stmt = this.connection.createStatement();) {
            int queryIndex = this.queries.indexOf("topTenActors");
            if (queryIndex > -1) {
                results = stmt.executeQuery(this.queries.get(queryIndex));
                while(results.next()) {
                    System.out.println(results.getString(1) 
                        + ", " + results.getString(2));
                }
                out = "Query completed.";
            } else {
                out = "ERR: could not find query in filesystem!";
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            out = "ERR: SQL/Database connection failed.";
        }
        return out;
    }

    public String directedTitles(String name) {
        String out = "";
        ResultSet results = null;
        try  {
            int queryIndex = this.queries.indexOf("directedTitles");
            if (queryIndex > -1) {
                PreparedStatement pstmt = this.connection.prepareStatement(this.queries.get(queryIndex));
                pstmt.setString(1, name);
                results = pstmt.executeQuery();
                if(results.next()) {
                    out += name + " has directed the following titles:\n ";
                } 
                while(results.next()) {
                    out += results.getString(1) + "\n";
                }
            } else {
                out = "ERR: could not find query in filesystem!";
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            out = "ERR: SQL/Database connection failed.";
        }
        return out;
    }

    public String movieAssociates(int titleID) {
        String out = "";
        try {
            ResultSet results = null;
            int queryIndex = this.queries.indexOf("movieAssociates");
            if (queryIndex > -1) {
                PreparedStatement pstmt = this.connection.prepareStatement(this.queries.get(queryIndex));
                pstmt.setInt(1, titleID);
                results = pstmt.executeQuery();
                if (results.next()) {
                    out += "Listing associated people with title " + Integer.toString(titleID) + ":\n";
                }
                while(results.next()) {
                    out += results.getInt(1) + " " + results.getString(2) + "\n";
                }
            } else {
                out = "ERR: could not find query in filesystem!";
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            out = "ERR: SQL/Database connection failed.";
        }
        return out;
    }

    // TODO: use person's name instead of personID (suggested from S5 feedback)
    public String titlesIn(String name) {
        String out = "";
        try {
            ResultSet results = null;
            int queryIndex = this.queries.indexOf("titlesIn");
            if (queryIndex > -1) {
                PreparedStatement pstmt = this.connection.prepareStatement(this.queries.get(queryIndex));
                pstmt.setString(1, name);
                results = pstmt.executeQuery();
                if (results.next()) {
                    out += "Listing all titles " + name + " was a part of: \n";
                }
                while(results.next()) {
                    out += "ID: " + results.getInt(3) + results.getString(1) + " (" + results.getString(2) + ")\n";
                }
            } else {
                out = "ERR: could not find query in filesystem!";
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            out = "ERR: SQL/Database connection failed.";
        }
        return out;
    }

    // TODO: use person's name instead of personID (suggested from S5 feedback)
    public String moviesKnownFor(String name) {
        String out = "";
        try {
            ResultSet results = null;
            int queryIndex = this.queries.indexOf("moviesKnownFor");
            if (queryIndex > -1) {
                PreparedStatement pstmt = this.connection.prepareStatement(this.queries.get(queryIndex));
                pstmt.setString(1, name);
                results = pstmt.executeQuery();
                if (results.next()) {
                    out += "Listing movies that '"+ name + "' is known for: \n";
                }
                while (results.next()) {
                    out += "";
                }
            } else {
                out = "ERR: could not find query in filesystem!";
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            out = "ERR: SQL/Database connection failed."; 
        }
        return out;
    }

    // TODO: use person's name instead of personID (suggested from S5 feedback)
    public String seriesKnownFor(String name) {
        String out = "";
        try {
            ResultSet results = null;
            int queryIndex = this.queries.indexOf("seriesKnownFor");
            if (queryIndex > -1) {
                PreparedStatement pstmt = this.connection.prepareStatement(this.queries.get(queryIndex));
                pstmt.setString(1, name);
                results = pstmt.executeQuery();
                if (results.next()) {
                    out += "Listing TV series that '" + name + "' is known for: \n";
                }
                while (results.next()) {
                    out += "";
                }
            } else {
                out = "ERR: could not find query in filesystem!";
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            out = "ERR: SQL/Database connection failed."; 
        }
        return out;
    }
    public String findPerson(String name) {
        String out = "";
        try {
            ResultSet results = null;
            int queryIndex = this.queries.indexOf("findPerson");
            if (queryIndex > -1) {
                PreparedStatement pstmt = this.connection.prepareStatement(this.queries.get(queryIndex));
                pstmt.setString(1, name);
                results = pstmt.executeQuery();
                if (results.next()) {
                    out += "Searching for '"+name+"'\n";
                }
                while (results.next()) {
                    out += "";
                }
            } else {
                out = "ERR: could not find query in filesystem!";
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            out = "ERR: SQL/Database connection failed."; 
        }
        return out;
    }
    public String findTitle(String name) {
        String out = "";
        try {
            ResultSet results = null;
            int queryIndex = this.queries.indexOf("findTitle");
            if (queryIndex > -1) {
                PreparedStatement pstmt = this.connection.prepareStatement(this.queries.get(queryIndex));
                pstmt.setString(1, name);
                results = pstmt.executeQuery();
                if (results.next()) {
                    out += "Searching for title '"+ name +"' \n";
                }
                while (results.next()) {
                    out += "";
                }
            } else {
                out = "ERR: could not find query in filesystem!";
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            out = "ERR: SQL/Database connection failed."; 
        }
        return out;
    }
    public String getRatings(int titleID) {
        String out = "";
        try {
            ResultSet results = null;
            int queryIndex = this.queries.indexOf("getRatings");
            if (queryIndex > -1) {
                PreparedStatement pstmt = this.connection.prepareStatement(this.queries.get(queryIndex));
                pstmt.setInt(1, titleID);
                results = pstmt.executeQuery();
                if (results.next()) {
                    out += "Listing user ratings and total votes for title '"+Integer.toString(titleID)+"' \n";
                }
                while (results.next()) {
                    out += "";
                }
            } else {
                out = "ERR: could not find query in filesystem!";
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            out = "ERR: SQL/Database connection failed."; 
        }
        return out;
    }
    public String getProfessionals(String profName) {
        String out = "";
        try {
            ResultSet results = null;
            int queryIndex = this.queries.indexOf("getProfessionals");
            if (queryIndex > -1) {
                PreparedStatement pstmt = this.connection.prepareStatement(this.queries.get(queryIndex));
                pstmt.setString(1, profName);
                results = pstmt.executeQuery();
                if (results.next()) {
                    out += "Listing all people with '"+profName+"': \n";
                }
                while (results.next()) {
                    out += "";
                }
            } else {
                out = "ERR: could not find query in filesystem!";
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            out = "ERR: SQL/Database connection failed."; 
        }
        return out;
    }
    public String listSeriesEpisodes(int seriesID) {
        String out = "";
        try {
            ResultSet results = null;
            int queryIndex = this.queries.indexOf("listSeriesEpisodes");
            if (queryIndex > -1) {
                PreparedStatement pstmt = this.connection.prepareStatement(this.queries.get(queryIndex));
                pstmt.setInt(1, seriesID);
                results = pstmt.executeQuery();
                if (results.next()) {
                    out += "Listing all episodes in series '"+ Integer.toString(seriesID)+"' \n";
                }
                while (results.next()) {
                    out += "";
                }
            } else {
                out = "ERR: could not find query in filesystem!";
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            out = "ERR: SQL/Database connection failed."; 
        }
        return out;
    }
    public String seriesMainCast(int seriesID) {
        String out = "";
        try {
            ResultSet results = null;
            int queryIndex = this.queries.indexOf("seriesMainCast");
            if (queryIndex > -1) {
                PreparedStatement pstmt = this.connection.prepareStatement(this.queries.get(queryIndex));
                pstmt.setInt(1, seriesID);
                results = pstmt.executeQuery();
                if (results.next()) {
                    out += "Listing people who have appeared in every episode of series "+ Integer.toString(seriesID) + "\n";
                }
                while (results.next()) {
                    out += "";
                }
            } else {
                out = "ERR: could not find query in filesystem!";
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            out = "ERR: SQL/Database connection failed."; 
        }
        return out;
    }

    public String listCastAndRoles(int titleID) {
        String out = "";
        try {
            ResultSet results = null;
            int queryIndex = this.queries.indexOf("listCastAndRoles");
            if (queryIndex > -1) {
                PreparedStatement pstmt = this.connection.prepareStatement(this.queries.get(queryIndex));
                pstmt.setInt(1, titleID);
                results = pstmt.executeQuery();
                if (results.next()) {
                    out += "Listing cast and roles for title "+Integer.toString(titleID)+"\n";
                }
                while (results.next()) {
                    out += "";
                }
            } else {
                out = "ERR: could not find query in filesystem!";
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            out = "ERR: SQL/Database connection failed."; 
        }
        return out;

    }

}