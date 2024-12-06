import java.util.Scanner;

public class Interface {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Connecting to database...");
        DatabaseManager dbm = new DatabaseManager("auth.cfg");
        dbm.connect();
        showCommands();
        boolean promptLoop = true;
        while(promptLoop) {
            System.out.print("\nInput a command... > ");
            String cmd = in.nextLine().toLowerCase();
            switch(cmd) {
                case "h": // help
                    showCommands();
                    break;
                case "q": // quit
                    promptLoop = false;
                    in.close();
                    System.out.println("Exiting program...");
                    System.out.println("End of processing.\n\n");
                    System.exit(1);
                    break;
                case "mr":
                    dbm.query.topTenMovies();
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
        System.out.println("\tq : Exit this program");
        System.out.println("\tta : List Top 10 Actors by most credits");
        System.out.println("\tmr : List Top 10 Movies by highest Rating");
        System.out.println("\ttd : Find Titles directed by a given Person");
        System.out.println("\tma : Find people associated with a given Movie");
        System.out.println("\ttva : Find people associated with a given TV Series");
        System.out.println("\tmk : Find Movies a given Person is known for");
        System.out.println("\ttk : Find TV Shows a given Person is known for");
        System.out.println("\tp : Find a Person by name");
        System.out.println("\tt : Find a Title by name");
        System.out.println("\ttr : Find a Title's rating");
        System.out.println("\tpro : List all Professionals (People with professions)");
        System.out.println("\tpro : List all episodes of a TV series");
        System.out.println("\tam : List People who have appeared in all episodes of a given TV series");
    }

}