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

        assert taskList != null : "TaskList should be initialized";
        assert storage != null : "Storage should be initialized";
        assert ui != null : "UI should be initialized";
        assert parser != null : "Parser should be initialized";

        storage.load(taskList); // load saved tasks

        assert taskList.taskCount >= 0 && taskList.taskCount <= TaskList.MAX_TASKS : "Task count out of bounds after loading";
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