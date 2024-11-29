import java.util.Scanner;

public class Interface {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Connecting to database...");
        DatabaseManager dbm = new DatabaseManager("auth.cfg");
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
                case "tta":
                    dbm.queryTopTenMovies();
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
        System.out.println("\tta : Top 10 Actors by most credits");
        System.out.println("\tmr : Top 10 Movies by highest Rating");
        System.out.println("\tmd : Movies directed by a given Person");
        System.out.println("\tmk : Movies a Person is known for");
        System.out.println("\ttk : TV Shows a Person is known for");
        System.out.println("\tml : People associated with a Movie");
        System.out.println("\tpt : Titles a Person was a part of");
    }

}