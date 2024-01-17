package it.unipi.largescale.pixelindex;

import it.unipi.largescale.pixelindex.dto.ReviewDTO;
import it.unipi.largescale.pixelindex.dao.ReviewDAO;
import it.unipi.largescale.pixelindex.model.Review;
import it.unipi.largescale.pixelindex.view.ListSelector;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.io.Console;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App implements NativeKeyListener
{
    private static boolean inMenu = true;
    private static int currentSelection;
    private static ArrayList<ReviewDTO> listOfReviews;
    private static ReviewDAO reviewDAO;

    private static ListSelector listSelector;
    private static App app1;
    private static void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static void showOptions()
    {
        while(inMenu)
        {
            // Asking an user interaction
            String index = listSelector.askUserInteraction("firstSelection");
            int selection = Integer.parseInt(index) + 1;
            if(selection == 1)
            {
                System.out.println("Not implemented");
            }
            if(selection == 2)
            {
                System.out.println("All the reviews [Experimental]:");
                createDAO();
                inMenu = false;
            }
            if(selection == 3)
            {
                cleanupAndExit();

            }
        }
    }
    private static void createDAO(){
        app1 = new App();
        startListening(app1);
        // removeEcho();
        reviewDAO = new ReviewDAO();
        // Creating some reviews
        Review r1 = new Review(0, 3, "This is a good game, although it's repetitive",
                "lorenzocecc", "Buckshot Roulette");
        ArrayList<String> like1 = new ArrayList<>();
        like1.add("juri99");
        like1.add("leonardo33");
        like1.add("lorenzocecc");
        r1.setListLikeUser(like1);
        reviewDAO.addReview(r1);

        Review r2 = new Review(1, 5, "The game I have preferred the most so far", "spaccatutto77",
                "Fortnite: Lego Mode");
        ArrayList<String> like2 = new ArrayList<>();
        like2.add("spaccatutto77");
        r2.setListLikeUser(like2);
        reviewDAO.addReview(r2);

        Review r3 = new Review(2, 1, "The jumpscare made be sleepwalker, now I'm scared to sleep", "crazygamer08", "" +
                "Five Night's at Freddy's");
        ArrayList<String> dislike3 = new ArrayList<>();
        dislike3.add("crazygamer08");
        r3.setListDislikeUser(dislike3);
        reviewDAO.addReview(r3);

        Review r4 = new Review(3, 4, "Beautiful", "purpleaz", "Fortnite: Lego Mode");
        ArrayList<String> like4 = new ArrayList<>();
        like4.add("purpleaz");
        like4.add("spaccatutto77");
        reviewDAO.addReview(r4);

        // Visualizing the reviews
        clearConsole();
        int start = 0; int end = 3;
        listOfReviews = reviewDAO.listReviews(start,end);
        currentSelection = start;
        listOfReviews.get(currentSelection).setCursorSelection(true);
        displayReviews();
    }
    private static void removeEcho()
    {
        Console console = System.console();
        // In order to disable the echo of the key pressed, like we have for passwords
        char[] buf = console.readPassword();
    }

    private static void cleanupAndExit() {
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
    private static void displayReviews()
    {
        listOfReviews.stream().forEach(reviewDTO -> {
            System.out.println("----------");
            System.out.println(reviewDTO);
            System.out.println("----------");
        });
        System.out.println("Press W/S to go up/down");
        System.out.println("Press L to like, D to dislike, U to undo selection, Q to quit");
    }

    public static void startListening(App a)
    {

        GlobalScreen.addNativeKeyListener(a);
    }
    @Override
    public void nativeKeyPressed(NativeKeyEvent e){
        if(e.getKeyCode() == NativeKeyEvent.VC_W)
        {
            if(currentSelection > 0)
                currentSelection--;
        }
        if(e.getKeyCode() == NativeKeyEvent.VC_S){
            if(currentSelection < (listOfReviews.size()-1))
                currentSelection++;
        }
        String CONSIDERED_USER = "lorenzocecc";
        if(e.getKeyCode() == NativeKeyEvent.VC_L)
        {
            int ident = listOfReviews.get(currentSelection).getId();
            reviewDAO.addLike(ident, CONSIDERED_USER);
            reviewDAO.removeDislike(ident, CONSIDERED_USER);
        }
        if(e.getKeyCode() == NativeKeyEvent.VC_D){
            int ident = listOfReviews.get(currentSelection).getId();
            reviewDAO.addDislike(ident, CONSIDERED_USER);
            reviewDAO.removeLike(ident, CONSIDERED_USER);
        }
        if(e.getKeyCode() == NativeKeyEvent.VC_U){
            int ident = listOfReviews.get(currentSelection).getId();
            reviewDAO.removeLike(ident, CONSIDERED_USER);
            reviewDAO.removeDislike(ident, CONSIDERED_USER);
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e)
    {
        if(e.getKeyCode() == NativeKeyEvent.VC_W)
        {
            listOfReviews.get(currentSelection).setCursorSelection(true);
            listOfReviews.get(currentSelection+1).setCursorSelection(false);
            clearConsole();
            displayReviews();
        }
        if(e.getKeyCode() == NativeKeyEvent.VC_S){
            listOfReviews.get(currentSelection).setCursorSelection(true);
            listOfReviews.get(currentSelection-1).setCursorSelection(false);
            clearConsole();
            displayReviews();
        }
        if(e.getKeyCode() == NativeKeyEvent.VC_L || e.getKeyCode() == NativeKeyEvent.VC_D
        || e.getKeyCode() == NativeKeyEvent.VC_U){
            // Refreshing
            clearConsole();
            int start = 0; int end = 3;
            listOfReviews = reviewDAO.listReviews(start, end);
            // Current selection remains equal
            listOfReviews.get(currentSelection).setCursorSelection(true);
            displayReviews();
        }
        if(e.getKeyCode() == NativeKeyEvent.VC_Q){
            System.out.println("Exiting");
            inMenu = true;
            GlobalScreen.removeNativeKeyListener(app1);
            showOptions();
        }
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent arg0)
    {
        Console console = System.console();
        // In order to disable the echo of the key pressed, like we have for passwords
        char[] buf = console.readPassword();
    }

    public static void main( String[] args )
    {
         /* This is used to disable all the log information, such as the undesired
        output of the movements of the mouse*/
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);
        try {
            /* Register jNativeHook */
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            ex.printStackTrace();
        }

        listSelector = new ListSelector();
        listSelector.createListSelector();

        // Creating the list of selectable options
        ArrayList<String> optList = new ArrayList<>();
        optList.add("Find reviews by game");
        optList.add("View all reviews");
        optList.add("Quit program");

        listSelector.addOptions(optList, "firstSelection", "Command");

        showOptions();

    }
}
