package it.unipi.largescale.pixelindex.controller;

import it.unipi.largescale.pixelindex.view.dropdown.RegisteredMenu;

public class ApplicationController {
    private UnregisteredUserController unregisteredUserController;
    private RegisteredUserController registeredUserController;
    private ModeratorController moderatorController;


    public ApplicationController() {

        unregisteredUserController = new UnregisteredUserController();
        int fun = unregisteredUserController.showUnregisteredDropdown();
        if(fun == 0 || fun == 5)
        {
            registeredUserController = new RegisteredUserController(unregisteredUserController.getUsername(), unregisteredUserController.getDateOfBirth(), false);
            registeredUserController.execute();
        } else if(fun == 6){
            moderatorController = new ModeratorController(unregisteredUserController.getUsername(), unregisteredUserController.getDateOfBirth());
            moderatorController.execute();
        }
    }
}
