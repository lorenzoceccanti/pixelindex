package it.unipi.largescale.pixelindex.model;

public class UnregisteredUser extends User{
    public UnregisteredUser(){

    }

    public UnregisteredUser(String language)
    {
        String role = "unregistered";
        super.setLanguage(language);
        super.setRole(role);
    }
}
