package it.unipi.largescale.pixelindex.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.fusesource.jansi.AnsiConsole;

import de.codeshelf.consoleui.prompt.ConsolePrompt;
import de.codeshelf.consoleui.prompt.ListResult;
import de.codeshelf.consoleui.prompt.PromtResultItemIF;
import de.codeshelf.consoleui.prompt.builder.ListPromptBuilder;
import de.codeshelf.consoleui.prompt.builder.PromptBuilder;
import jline.TerminalFactory;

import static org.fusesource.jansi.Ansi.ansi;

public class ListSelector{

    private ConsolePrompt prompt;
    private PromptBuilder promptBuilder;
    public void createListSelector()
    {
        AnsiConsole.systemInstall();
        System.out.println(ansi().eraseScreen().render("Simple list example:"));
        prompt = new ConsolePrompt();
    }

    public void addOptions(ArrayList<String> opt, String name, String message)
    {
        PromptBuilder pb = prompt.getPromptBuilder();
        ListPromptBuilder lpb = pb.createListPrompt();
        // Specifico il nome
        lpb.name(name);
        // Specifico il messaggio
        lpb.message(message);
        // Scorro l'array list
        for(int i = 0; i < opt.size(); i++)
        {
            // Quello che specifichi dentro newItem è l'indice dell'opzione
            // Importante per poi sapere ciò che l'utente ha selezionato
            // In questo caso si tratta dello 0 index
            lpb.newItem(Integer.toString(i)).text(opt.get(i)).add();
        }
        // Aggiungo il prompt
        promptBuilder = lpb.addPrompt();
    }

    public String askUserInteraction(String parameter){
        String temp = null;
        try{
            HashMap<String, ? extends PromtResultItemIF> result = prompt.prompt(promptBuilder.build());
            temp = ((ListResult)result.get(parameter)).getSelectedId();
        }catch(IOException e)
        {
            e.printStackTrace();
        } finally{
            try{
                TerminalFactory.get().restore();
            } catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        return temp;
    }
}
