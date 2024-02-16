package it.unipi.largescale.pixelindex.view.dropdown;

import it.unipi.largescale.pixelindex.utils.AnsiColor;
import it.unipi.largescale.pixelindex.utils.Utils;
import it.unipi.largescale.pixelindex.view.impl.ListSelector;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class ModeratorMenu {
    ArrayList<String> basicOptions;
    ArrayList<String> specialOptions;
    ListSelector listSelector;
    AtomicBoolean displayed;
    AtomicBoolean specialDisplayed;
    int selection = -1;
    public ModeratorMenu(){
        displayed = new AtomicBoolean(true);
        specialDisplayed = new AtomicBoolean(false);
        basicOptions = new ArrayList<>();
        basicOptions.add("Moderator area");
        basicOptions.add("Search games");
        basicOptions.add("Search users");
        basicOptions.add("View your library");
        basicOptions.add("View your whishlist");
        basicOptions.add("Users you might follow");
        basicOptions.add("Most active reviewers");
        basicOptions.add("Top rated games");
        basicOptions.add("Trending games chart");
        basicOptions.add("Show suggested games");

        specialOptions = new ArrayList<>();
        specialOptions.add(AnsiColor.ANSI_RED+"View reports"+AnsiColor.ANSI_RESET);
        specialOptions.add(AnsiColor.ANSI_RED+"Add game"+AnsiColor.ANSI_RESET);
        specialOptions.add(AnsiColor.ANSI_RED+"Synchronize games"+AnsiColor.ANSI_RESET);
        specialOptions.add(AnsiColor.ANSI_RED+"User Registration Stats"+AnsiColor.ANSI_RESET);
        specialOptions.add("Go back");

        basicOptions.add("Exit app");
    }
    public int displayMenu(){
        listSelector = new ListSelector("*** Moderator Dashboard ***");
        listSelector.addOptions(basicOptions, "moderator_menu", "Make your choice");
        selection = listSelector.askUserInteraction("moderator_menu");
        Utils.clearConsole();
        return selection;
    }

    public int displaySpecialMenu(){
        listSelector = new ListSelector("*** Special Area ***");
        listSelector.addOptions(specialOptions, "special_menu", "Make your choice");
        selection = listSelector.askUserInteraction("special_menu");
        return selection;
    }

    public AtomicBoolean getDisplayed() {
        return displayed;
    }

    public AtomicBoolean getSpecialDisplayed() {
        return specialDisplayed;
    }
}
