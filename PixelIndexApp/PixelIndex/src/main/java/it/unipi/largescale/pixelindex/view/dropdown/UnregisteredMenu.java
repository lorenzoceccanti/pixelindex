package it.unipi.largescale.pixelindex.view.dropdown;

import it.unipi.largescale.pixelindex.utils.Utils;
import it.unipi.largescale.pixelindex.view.impl.ListSelector;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class UnregisteredMenu
{
    ArrayList<String> options;
    ListSelector listSelector;
    int selection = -1;
    AtomicBoolean displayed;

    public UnregisteredMenu() {

        displayed = new AtomicBoolean(true);

        options = new ArrayList<>();
        options.add("Login");
        options.add("Register");
        options.add("Search games");
        options.add("Exit app");
    }

    /**
     * Display the drop-down menù
     * @param text The text to be displayed before the selection
     * @return The index corresponding to the selected option
     */
    public int displayMenu(String text){
        listSelector = new ListSelector(text);
        listSelector.addOptions(options, "unregistered_menu", "Make your choice");
        selection = listSelector.askUserInteraction("unregistered_menu");
        Utils.clearConsole();
        return selection;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public int optionNumber() {
        return options.size();
    }

    public AtomicBoolean getDisplayed() {
        return displayed;
    }

    public void setDisplayed(AtomicBoolean displayed) {
        this.displayed = displayed;
    }
}
