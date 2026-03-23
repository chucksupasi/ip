package echo;

import java.util.Scanner;

/**
 * Main class for the Echo chatbot program.
 */
public class Echo {


    /**
     * Runs the Echo chatbot program and handles user input.
     * @param args command line arguments
     **/

    private TaskList taskList;
    private Storage storage;
    private Ui ui;
    private Parser parser;

    public Echo() {
        taskList = new TaskList();
        storage = new Storage();
        ui = new Ui();
        parser = new Parser();

        storage.load(taskList); // load saved tasks
    }

    public String getResponse(String input) {
        if (input.equals("bye")) {
            return "Bye!";
        }

        // Instead of printing, return output
        return parser.handleCommand(input, taskList, ui, storage);
    }
}