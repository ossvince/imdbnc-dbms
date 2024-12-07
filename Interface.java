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
                    System.out.println("Exiting program...");
                    System.out.println("End of processing.\n\n");
                    System.exit(0);
                    break;
                case "dbc":
                    System.out.println("Creating database...");
                    System.out.println(dbm.createDatabase("sql/schema.sql"));
                    break;
                case "dbr":
                    System.out.println("Repopulating database. Please wait...");
                    System.out.println(dbm.updateDatabaseByLine("sql/insert.sql"));
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
                    System.out.print("\n(Get a Title's rating) Enter a titleID... > ");
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
                    System.out.print("\nEnter a movie's title ID to find associated people... > ");
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
                case "pro":
                    System.out.print("\nEnter a Profession name to list people with that profession... > ");
                    System.out.println(dbm.query.getProfessionals(in.nextLine()));
                    break;
                case "ep":
                    System.out.print("\nEnter a Series name to list its episodes... > ");
                    System.out.println(dbm.query.listSeriesEpisodes(in.nextLine()));
                    break;
                case "am":
                    System.out.print("\nEnter a Series name to list its people who have appeared in all episodes... > ");
                    System.out.println(dbm.query.seriesMainCast(in.nextLine()));
                    break;
                case "at":
                    System.out.print("\nEnter a Series name to list its episodes... > ");
                    System.out.println(dbm.query.listCastAndRoles(in.nextLine()));
                    break;
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
        System.out.println("\t\tdbc : Create tables in database");
        System.out.println("\t\tdd : Delete data from database");
        System.out.println("\t\tdbd : Fully Delete database");
        System.out.println("\t\tdbr : Repopulate database");
        System.out.println("\t[Database query commands]");
        System.out.println("\t\tp : Find a Person by name");
        System.out.println("\t\tt : Find a Title by name");
        System.out.println("\t\ttr : Find a Title's rating");
        System.out.println("\t\tta : List Top 10 Actors by most credits");
        System.out.println("\t\tmr : List Top 10 Movies by highest Rating");
        System.out.println("\t\ttd : Find Titles directed by a given Person");
        System.out.println("\t\tma : Find people associated with a given Movie");
        System.out.println("\t\tmk : Find Movies a given Person is known for");
        System.out.println("\t\ttk : Find TV Shows a given Person is known for");
        System.out.println("\t\tpr : List all Professionals (People with professions)");
        System.out.println("\t\tpro : List all Professionals (People with professions)");
        System.out.println("\t\teps : List all episodes of a TV series");
        System.out.println("\t\tam : List People who have appeared in all episodes of a given TV series");
        System.out.println("\t\tat : List all actors in a title, and the characters they played");
    }

}