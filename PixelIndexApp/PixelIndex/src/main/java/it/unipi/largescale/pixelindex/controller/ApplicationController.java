package it.unipi.largescale.pixelindex.controller;

public class ApplicationController {

    public ApplicationController() {

        UnregisteredUserController unregisteredUserController = new UnregisteredUserController();
        int fun = unregisteredUserController.showUnregisteredDropdown();
        if(fun == 0 || fun == 5)
        {
            RegisteredUserController registeredUserController = new RegisteredUserController(unregisteredUserController.getUsername(), unregisteredUserController.getDateOfBirth(), false);
            registeredUserController.execute();
        } else if(fun == 6){
            ModeratorController moderatorController = new ModeratorController(unregisteredUserController.getUsername(), unregisteredUserController.getDateOfBirth());
            moderatorController.execute();
        }
    }
}
