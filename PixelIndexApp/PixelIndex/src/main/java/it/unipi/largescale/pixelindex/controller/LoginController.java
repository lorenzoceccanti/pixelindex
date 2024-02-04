package it.unipi.largescale.pixelindex.controller;

import it.unipi.largescale.pixelindex.dto.AuthUserDTO;
import it.unipi.largescale.pixelindex.dto.UserLoginDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.exceptions.UserNotFoundException;
import it.unipi.largescale.pixelindex.exceptions.WrongPasswordException;
import it.unipi.largescale.pixelindex.service.RegisteredUserService;
import it.unipi.largescale.pixelindex.service.ServiceLocator;
import jline.internal.Log;

import java.io.Console;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoginController {
    UserLoginDTO userLoginDTO;
    RegisteredUserService registeredUserService;

    public LoginController()
    {
        userLoginDTO = new UserLoginDTO();
        this.registeredUserService = ServiceLocator.getRegisteredUserService();
    }

    /** Invokes the userService for making the login
     *
     * @return 1 if the login failed due to connection errors
     * 2 if the login failed due to wrong username
     * 3 if the login failed due to wrong password
     * 0 if the login succeded
     */
    private int execute(){
        try{
            registeredUserService.makeLogin(userLoginDTO.getUsername(), userLoginDTO.getPassword());
            return 0;
        }catch(ConnectionException ex)
        {
            System.out.println(ex.getMessage());
            return 1;
        }catch(UserNotFoundException ex)
        {
            System.out.println("User name does not exists. Retry");
            return 2;
        }catch(WrongPasswordException ex)
        {
            System.out.println("Wrong password. Retry");
            return 3;
        }
    }

    /** Asks credentials to the user
     *
     * @return 1 if the login failed due to connection errors
     * 2 if the login failed due to wrong username
     * 3 if the login failed due to wrong password
     * 0 if the login succeded
     */
    public int askCredentials(AtomicBoolean displayed){
        Scanner sc = new Scanner(System.in);
        System.out.println("Username?");
        String username = sc.nextLine();
        System.out.println("Password?");
        Console console = System.console();
        String password = String.valueOf(console.readPassword());

        userLoginDTO.setUsername(username);
        userLoginDTO.setPassword(password);

        int ret = execute();
        if(ret != 0)
            displayed.set(true);
        else
            displayed.set(false);
        return ret;
    }
    public String getUsername()
    {
        return userLoginDTO.getUsername();
    }

}
