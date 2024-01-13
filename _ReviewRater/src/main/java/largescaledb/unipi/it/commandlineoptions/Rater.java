// Documentation, Java KeyLogger part 1 on YT
// https://www.youtube.com/watch?app=desktop&v=SM2hurOBvgM

package largescaledb.unipi.it.commandlineoptions;
import java.io.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

/**
 * @author Lorenzo Ceccanti
 * Global Keyboard Listener
 */
public class Rater implements NativeKeyListener {

    private static int currentSelection = 0;
    private static List<Review> recensioni;

    @Override
    public void nativeKeyPressed(NativeKeyEvent e)
    {
        if(e.getKeyCode() == NativeKeyEvent.VC_W)
        {
            currentSelection = (currentSelection - 1) % recensioni.size();
            if(currentSelection < 0)
                currentSelection = 0;
        }
        if(e.getKeyCode() == NativeKeyEvent.VC_S)
        {
            currentSelection = (currentSelection + 1) % recensioni.size();
            if(currentSelection > recensioni.size())
                currentSelection = recensioni.size();
        }
        if(e.getKeyCode() == NativeKeyEvent.VC_L)
        {
            recensioni.get(currentSelection).setLike(true);
            recensioni.get(currentSelection).setDislike(false);
        }
        if(e.getKeyCode() == NativeKeyEvent.VC_D)
        {
            recensioni.get(currentSelection).setLike(false);
            recensioni.get(currentSelection).setDislike(true);
        }
        if(e.getKeyCode() == NativeKeyEvent.VC_U)
        {
            recensioni.get(currentSelection).setLike(false);
            recensioni.get(currentSelection).setDislike(false);
        }
        if(e.getKeyCode() == NativeKeyEvent.VC_Q)
        {
            System.out.println("Exiting..");
            System.out.println("Review status: \n |Name|Like|Dislike");
            for(int i=0; i<recensioni.size();i++)
                System.out.println(recensioni.get(i).visualizeReview());
            cleanupAndExit();
            System.exit(0);
        }
        
    }
    
    @Override
    public void nativeKeyReleased(NativeKeyEvent e)
    {
        if(e.getKeyCode() == NativeKeyEvent.VC_W || e.getKeyCode() == NativeKeyEvent.VC_S
        || e.getKeyCode() == NativeKeyEvent.VC_L || e.getKeyCode() == NativeKeyEvent.VC_D || e.getKeyCode() == NativeKeyEvent.VC_U)
        {
            clearConsole();
            displayReview(recensioni, currentSelection);
            System.out.println("Press W/S to go up/down");
            System.out.println("Press L to like, D to dislike, U to undo selection, Q to quit");
        }
    }
    
    @Override
    public void nativeKeyTyped(NativeKeyEvent arg0)
    {
        Console console = System.console();
        // In order to disable the echo of the key pressed, like we have for passwords
        char[] buf = console.readPassword();
    }
    
    public static void startListening(Rater clo)
    {
        GlobalScreen.addNativeKeyListener(clo);
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
    
    private static void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static void displayReview(List<Review> recensioni, int currSel) {
        for (int i = 0; i < recensioni.size(); i++) {
            System.out.print(i == currSel ? "> " : "  ");
            System.out.println(recensioni.get(i));
        }
    }

    public static void main(String[] args) throws Exception{
        
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

        recensioni = new ArrayList<>();
        recensioni.add(new Review("Recensione n.1"));
        recensioni.add(new Review("Recensione n.2"));
        recensioni.add(new Review("Recensione n.3"));
        
        clearConsole();
        displayReview(recensioni, currentSelection);
        System.out.println("Press W/S to go up/down");
        System.out.println("Press L to like, D to dislike, U to undo selection, Q to quit");
        

        startListening(new Rater());
        removeEcho();
        
        
    }
}