package it.unipi.largescale.pixelindex.view.dropdown;

import it.unipi.largescale.pixelindex.utils.Utils;
import it.unipi.largescale.pixelindex.view.impl.ListSelector;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class RegisteredMenu {
    ArrayList<String> options;
    ListSelector listSelector;
    AtomicBoolean displayed;
    int selection = -1;

    public RegisteredMenu() {
        displayed = new AtomicBoolean(true);
        options = new ArrayList<>();
        options.add("Search games");
        options.add("Search users");
        options.add("View your library");
        options.add("View your wishlist");
        options.add("Users you might follow");
        options.add("Most active reviewers");
        options.add("Top rated games");
        options.add("Trending games chart");
        options.add("Show suggested games");
        options.add("Exit app");
    }

    /**
     *  @return The selection that the user made
     */
    public int displayMenu(String username){
        listSelector = new ListSelector("Welcome " + username);
        listSelector.addOptions(options, "registered_menu", "Make your choice");
        selection = listSelector.askUserInteraction("registered_menu");
        Utils.clearConsole();
        return selection;
    }
    public AtomicBoolean getDisplayed() {
        return displayed;
    }

    public void setDisplayed(AtomicBoolean displayed) {
        this.displayed = displayed;
    }

}
