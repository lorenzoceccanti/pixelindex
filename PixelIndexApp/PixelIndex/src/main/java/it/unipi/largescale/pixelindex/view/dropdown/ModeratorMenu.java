package it.unipi.largescale.pixelindex.view.dropdown;

import it.unipi.largescale.pixelindex.utils.Utils;
import it.unipi.largescale.pixelindex.view.impl.ListSelector;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class ModeratorMenu {
    ArrayList<String> options;
    ListSelector listSelector;
    AtomicBoolean displayed;
    int selection = -1;
    public ModeratorMenu(){
        displayed = new AtomicBoolean(true);
        options = new ArrayList<>();
        options.add("View reports");
        options.add("Add game");
        options.add("Exit app");
    }
    public int displayMenu(){
        listSelector = new ListSelector("*** Moderator Area ***");
        listSelector.addOptions(options, "moderator_menu", "Make your choice");
        selection = listSelector.askUserInteraction("moderator_menu");
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
