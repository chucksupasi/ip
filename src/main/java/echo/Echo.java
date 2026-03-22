package echo;

import java.util.Scanner;

/**
 * Main class for the Echo chatbot program.
 */
public class Echo {
    /**
     * Runs the Echo chatbot program and handles user input.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        TaskList taskList = new TaskList();
        Storage storage = new Storage();
        Ui ui = new Ui();
        Parser parser = new Parser();

        // Load data from existing file at the start of programme
        storage.load(taskList);

        // Introduction
        ui.showWelcome();

        // Main loop where users can chat continuously
        while (true) {
            System.out.print("> ");
            String input = sc.nextLine();

            if (input.equals("bye")) {
                ui.showBye();
                break;
            }

            parser.handleCommand(input, taskList, ui, storage);
        }
        sc.close();
    }
}