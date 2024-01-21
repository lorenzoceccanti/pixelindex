package it.unipi.largescale.pixelindex.model;

public class Moderator extends RegisteredUser{
    public Moderator() {}
    public Moderator(String language){
        String role = "moderator";
        super.setLanguage(language);
        super.setRole(role);
    }
}
