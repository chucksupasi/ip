package echo;

/**
 * Main class that combines different Echo chatbot classes.
 */
public class Echo {

    private TaskList taskList;
    private Storage storage;
    private Ui ui;
    private Parser parser;

    /**
     * Constructs a new Echo chatbot and loads saved tasks.
     */
    public Echo() {
        taskList = new TaskList();
        storage = new Storage();
        ui = new Ui();
        parser = new Parser();

        storage.load(taskList); // load saved tasks
    }

    /**
     * Processes user input and returns the chatbot's response.
     * @param input user input string
     * @return chatbot response
     */
    public String getResponse(String input) {
        if (input.equals("bye")) {
            return "Bye!";
        }

        // Instead of printing, return output
        return parser.handleCommand(input, taskList, ui, storage);
    }
}