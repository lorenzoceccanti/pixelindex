package it.unipi.largescale.pixelindex.view.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import it.unipi.largescale.pixelindex.utils.Utils;
import org.fusesource.jansi.AnsiConsole;

import de.codeshelf.consoleui.prompt.ConsolePrompt;
import de.codeshelf.consoleui.prompt.ListResult;
import de.codeshelf.consoleui.prompt.PromtResultItemIF;
import de.codeshelf.consoleui.prompt.builder.ListPromptBuilder;
import de.codeshelf.consoleui.prompt.builder.PromptBuilder;
import jline.TerminalFactory;

import static org.fusesource.jansi.Ansi.ansi;

public class ListSelector {
    private ConsolePrompt prompt;
    private PromptBuilder promptBuilder;

    /**
     * Constructs a drop down menù for the CLI
     *
     * @param text The first message you want to display
     */
    public ListSelector(String text) {
        AnsiConsole.systemInstall();
        System.out.println(ansi().render(text));
        // System.out.println(ansi().eraseScreen().render(text));
        prompt = new ConsolePrompt();
    }

    /**
     * Adds the options to a ListSelector drop down menù object
     *
     * @param options       The list of option to add
     * @param id            The identifier which identifies a specific drop down menù among the others
     * @param choiceMessage The second message you want to display
     */
    public void addOptions(ArrayList<String> options, String id, String choiceMessage) {
        PromptBuilder pb = prompt.getPromptBuilder();
        ListPromptBuilder lpb = pb.createListPrompt();
        lpb.name(id);
        lpb.message(choiceMessage);
        for (int i = 0; i < options.size(); i++) {
            // Here you use the 0-base index of the ArrayList to map
            // the choiceText with the user selection
            lpb.newItem(Integer.toString(i)).text(options.get(i)).add();
        }
        promptBuilder = lpb.addPrompt();
    }

    /**
     * This method will block until the user has made its choice as concerns the drop down menù
     *
     * @param id The identifier of the drop down menù object
     * @return The 0-based index associated to the selection that the user made
     */
    public int askUserInteraction(String id) {
        int temp = -1;
        try {
            HashMap<String, ? extends PromtResultItemIF> result = prompt.prompt(promptBuilder.build());
            temp = Integer.parseInt(((ListResult) result.get(id)).getSelectedId());
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                TerminalFactory.get().restore();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return temp;
    }

}
