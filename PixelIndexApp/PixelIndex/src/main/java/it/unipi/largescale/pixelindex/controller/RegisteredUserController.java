package it.unipi.largescale.pixelindex.controller;

import it.unipi.largescale.pixelindex.view.dropdown.RegisteredMenu;

public class RegisteredUserController {
    private RegisteredMenu registeredMenu;
    private Runnable[] functions;
    private Runnable[] functionsRegistered;
    private String sessionUsername;
    private int actualFun;
    private int previousFun;

    private int showRegisteredDropdown()
    {
        return registeredMenu.displayMenu(sessionUsername);
    }
    public RegisteredUserController(int previousFun, String username)
    {
        this.previousFun = previousFun;
        this.actualFun = -1;
        this.sessionUsername = username;
        registeredMenu = new RegisteredMenu();
        functions = new Runnable[]{
                () -> {
                    actualFun = showRegisteredDropdown();

                },
                () -> {
                    actualFun = showRegisteredDropdown();
                },
                () -> {
                    /* Showing the menu of search by game */
                },
                () -> {
                    /* Showing the menu of search by company */
                },
                () -> {},
                () -> {System.exit(0);}
        };
    }

    public void execute(){
        functions[previousFun].run();
        System.out.println(actualFun);
    }

}
