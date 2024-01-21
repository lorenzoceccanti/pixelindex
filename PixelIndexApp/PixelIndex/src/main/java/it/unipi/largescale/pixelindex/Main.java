// Questo è un main di prova

package it.unipi.largescale.pixelindex;

import it.unipi.largescale.pixelindex.model.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Main {
    public static void main(String[] args) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse("1998-02-12", formatter);
        System.out.println("Hello world!");
        System.out.println("myDOB: " + date);

        RegisteredUser registered = new RegisteredUser("ITA");
        Moderator mod = new Moderator("ENG");
        mod.setName("Lore");
        mod.setSurname("Cecca");
        mod.setUsername("ilcecco");
        mod.setDateOfBirth(date);
        mod.setEmail("cecca@lore.it");
        mod.setHashedPassword("bau");
        mod.setId("A001");

        User u = mod;
        System.out.println(u.toString());

        // Prova enumerato
        Review r = new Review();
        r.setId("R001");
        r.setAuthor("A001");
        r.setRating(RatingKind.RECOMMENDED);
        System.out.println(r.toString());

    }
}