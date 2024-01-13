// Documentation on ConsoleUI libary: 
// https://github.com/awegmann/consoleui/blob/master/doc/howto.md

package largescaledb.unipi.it.commandline;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("Welcome to PixelIndex");
        Terminal terminal = new Terminal();
        terminal.createTerminal();
        
        System.out.println("How many element?");
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        
        // Creating the list of the possible options
        ArrayList<String> optList = new ArrayList<>();
        for(int i = 0; i<n; i++)
            optList.add("Option "+(i+1));
        terminal.addOptions(optList, "Unregistered user selection", "What do you want to do?");
        
        // Asking an user interaction
        String index = terminal.askUserInteraction(); // Returns a 0-based index as a string
        int selection = Integer.parseInt(index) + 1;
        System.out.println("Selection: " + selection);
        sc.close();
        
    }
}