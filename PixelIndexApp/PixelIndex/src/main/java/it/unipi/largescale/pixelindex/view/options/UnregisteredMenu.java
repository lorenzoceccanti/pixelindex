package it.unipi.largescale.pixelindex.view.options;

import java.util.ArrayList;

public class UnregisteredMenu {
    ArrayList<String> options;

    public UnregisteredMenu() {
        options = new ArrayList<>();
        options.add("Login");
        options.add("Register");
        options.add("Search by game");
        options.add("Search by company");
        options.add("Advanced search");
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public int optionNumber() {
        return options.size();
    }
}
