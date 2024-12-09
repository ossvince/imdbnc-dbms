import java.util.Scanner;

public class Interface {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Connecting to database...");
        DatabaseManager dbm = new DatabaseManager("auth.cfg");
        System.out.println(dbm.connect());
        showCommands();
        boolean promptLoop = true;
        while(promptLoop) {
            System.out.print("\nInput a command... > ");
            String cmd = in.nextLine().trim().toLowerCase();
            switch(cmd) {
                case "h":
                    showCommands();
                    break;
                case "q":
                    promptLoop = false;
                    in.close();
                    System.out.println("Closing SQL connection and exiting program...");
                    System.out.println(dbm.closeConnection());
                    System.out.println("End of processing.\n\n");
                    System.exit(0);
                    break;
                case "dbc":
                    System.out.print("WARNING: All existing data will be removed from the database. Enter 'y' to continue, or 'n' to return to the menu... > ");
                    if (in.nextLine().trim().toLowerCase().equals("y")) {
                        System.out.println("Cleaning and rebuilding database...");
                        System.out.println(dbm.updateDatabaseByLine("sql/dropSchema.sql"));
                        System.out.println("Creating database...");
                        System.out.println(dbm.createDatabase("sql/schema.sql"));
                    }
                    break;
                case "dbr":
                    System.out.println("Please wait. Repopulating database...");
                    //System.out.println(dbm.updateDatabaseByLine("sql/insert-test.sql"));
                    System.out.println(dbm.updateDatabaseByLine("sql/insert1.sql"));
                    System.out.println(dbm.updateDatabaseByLine("sql/insert2.sql"));
                    System.out.println(dbm.updateDatabaseByLine("sql/insert3.sql"));
                    System.out.println("Database has been successfully populated.");
                    break;
                case "dd":
                    System.out.println(dbm.updateDatabaseByLine("sql/deleteData.sql"));
                case "dbd":
                    System.out.println(dbm.updateDatabaseByLine("sql/dropSchema.sql"));
                    break;
                case "p":
                    System.out.print("\nFind a Person by name... > ");
                    System.out.println(dbm.query.findPerson(in.nextLine()));
                    break;
                case "t":
                    System.out.print("\nFind a Title by name... > ");
                    System.out.println(dbm.query.findTitle(in.nextLine()));
                    break;
                case "tr":
                    System.out.print("\nEnter a titles name to get its rating... > ");
                    System.out.println(dbm.query.getRatings(in.nextLine()));
                    break;
                case "ta":
                    System.out.println("Listing top 10 Actors with the most acting credits...");
                    System.out.println(dbm.query.topTenActingCredits());
                    break;
                case "mr":
                    System.out.println("Listing top 10 Movies by aggregated user ratings...");
                    System.out.println(dbm.query.topTenMovies());
                    break;
                case "td":
                    System.out.print("\nEnter the Person's name to retrieve titles they have directed... > ");
                    System.out.println(dbm.query.directedTitles(in.nextLine()));
                    break;
                case "ma":
                    System.out.print("\nEnter a movie's name to find associated people... > ");
                    System.out.println(dbm.query.movieAssociates(in.nextLine()));
                    break;
                case "mk":
                    System.out.print("\nEnter a person's name to find movies that they are known for... > ");
                    System.out.println(dbm.query.moviesKnownFor(in.nextLine()));
                    break;
                case "tk":
                    System.out.print("\nEnter a person's name to find TV series that they are known for... > ");
                    System.out.println(dbm.query.seriesKnownFor(in.nextLine()));
                    break;
                case "pr":
                    System.out.println("\nListing all professions...");
                    System.out.println(dbm.query.listAllProfessions());
                    break;
                case "pro":
                    System.out.print("\nEnter a Profession name to list people with that profession... > ");
                    System.out.println(dbm.query.getProfessionals(in.nextLine()));
                    break;
                case "eps":
                    System.out.print("\nEnter a Series name to list its episodes... > ");
                    System.out.println(dbm.query.listSeriesEpisodes(in.nextLine()));
                    break;
                case "am":
                    System.out.print("\nEnter a Series name to list its people who have appeared in all episodes... > ");
                    System.out.println(dbm.query.seriesMainCast(in.nextLine()));
                    break;
                case "at":
                    System.out.print("\nEnter a titles name to list its actors... > ");
                    System.out.println(dbm.query.listCastAndRoles(in.nextLine()));
                    break;
                case "ti":
                    System.out.print("\nEnter a persons name to list all titles they have been a part of... > ");
                    System.out.println(dbm.query.titlesIn(in.nextLine()));
            }
        }
        in.close();
        System.out.println("End of processing.\n\n");
    }

    private static void showCommands() {
        System.out.println();
        System.out.println("Internet Movie Database (IMDb) Management Interface");
        System.out.println("\n\tWelcome! Here are the available commands:");
        System.out.println("\n\th : View this help menu");
        System.out.println("\tq : Exit this program \n");
        System.out.println("\t[Database management]");
        System.out.println("\t- dbc : Create tables in database");
        System.out.println("\t- dd : Delete data from database");
        System.out.println("\t- dbd : Fully delete database schema and data");
        System.out.println("\t- dbr : Repopulate database\n");
        System.out.println("\t[Database query commands]");
        System.out.println("\t- p : Find a Person by name");
        System.out.println("\t- t : Find a Title by name");
        System.out.println("\t- tr : Find a Title's rating");
        System.out.println("\t- ta : List Top 10 Actors by most credits");
        System.out.println("\t- mr : List Top 10 Movies by highest Rating");
        System.out.println("\t- td : Find Titles directed by a given Person");
        System.out.println("\t- ma : Find people associated with a given Movie");
        System.out.println("\t- mk : Find Movies a given Person is known for");
        System.out.println("\t- tk : Find TV Shows a given Person is known for");
        System.out.println("\t- pr : List all Professions");
        System.out.println("\t- pro : List all Professionals (People with professions)");
        System.out.println("\t- eps : List all episodes of a TV series");
        System.out.println("\t- am : List People who have appeared in all episodes of a given TV series");
        System.out.println("\t- at : List all actors in a title, and the characters they played");
        System.out.println("\t- ti : List all titles a person is in");
    }

}