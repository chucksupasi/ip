package echo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Parses user input and executes commands for the chatbot.
 */
public class Parser {

    /**
     * Handles a user command and updates the task list accordingly.
     * @param input the command entered by the user
     * @param list the current task list
     * @param ui the user interface for printing messages
     * @param storage the storage handler for saving tasks
     */
    public void handleCommand(String input, TaskList list, Ui ui, Storage storage) {

        // List all tasks
        if (input.equals("list")) {
            ui.showList(list);
        }

        // Mark a task as done
        else if (input.startsWith("mark ")) {
            int num = parseIndex(input.substring(5), list);
            if (num == -1) return;

            list.done[num] = true;
            storage.save(list);

            System.out.println("Nice! I've marked this task as done:");
            ui.printTask(num, list);
        }

        // Unmark a task
        else if (input.startsWith("unmark ")) {
            int num = parseIndex(input.substring(7), list);
            if (num == -1) return;

            list.done[num] = false;
            storage.save(list);

            System.out.println("OK, I've marked this task as not done yet:");
            ui.printTask(num, list);
        }

        // create todo
        else if (input.startsWith("todo ")) {
            String desc = input.substring(5).trim();

            // Error if no description
            if (desc.isEmpty()) {
                System.out.println("Error: The description of a todo cannot be empty.");
                return;
            }

            int i = list.taskCount;
            list.tasks[i] = desc;
            list.taskType[i] = TaskList.TaskType.T;
            list.timeInfo[i] = "";
            list.done[i] = false;

            System.out.println("Got it. I've added this task:");
            ui.printTask(i, list);

            list.taskCount++;
            storage.save(list);

            System.out.println("Now you have " + list.taskCount + " tasks in the list.");
        }

        // Create deadline
        else if (input.startsWith("deadline ")) {
            String[] parts = input.substring(9).split(" /by ");

            // No description or date
            if (parts.length < 2) {
                System.out.println("Error: Deadline must have a description and /by date.");
                return;
            }

            int i = list.taskCount;
            String desc = parts[0].trim();
            String by = parts[1].trim();

            list.tasks[i] = desc;
            list.taskType[i] = TaskList.TaskType.D;
            list.timeInfo[i] = "by: " + by;

            // Check if deadline date is recognisable, else store as plain text
            try {
                list.taskDates[i] = LocalDate.parse(by, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
            catch (DateTimeParseException e) {
                list.taskDates[i] = null;
                System.out.println("Warning: Could not parse date, storing as plain text.");
            }

            list.done[i] = false;

            System.out.println("Got it. I've added this task:");
            ui.printTask(i, list);

            list.taskCount++;
            storage.save(list);

            System.out.println("Now you have " + list.taskCount + " tasks in the list.");
        }

        // Create event
        else if (input.startsWith("event ")) {
            String[] firstSplit = input.substring(6).split(" /from ");

            if (firstSplit.length < 2) {
                System.out.println("Error: Event must have a description and /from time.");
                return;
            }

            String desc = firstSplit[0].trim();
            String[] secondSplit = firstSplit[1].split(" /to ");

            if (secondSplit.length < 2) {
                System.out.println("Error: Event must have /from and /to times.");
                return;
            }

            int i = list.taskCount;
            String from = secondSplit[0].trim();
            String to = secondSplit[1].trim();

            list.tasks[i] = desc;
            list.taskType[i] = TaskList.TaskType.E;
            list.timeInfo[i] = "from: " + from + " to: " + to;

            // Check if event starting date is recognisable, else store as plain text
            try {
                list.taskFromDates[i] = LocalDate.parse(from, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
            catch (DateTimeParseException e) {
                list.taskFromDates[i] = null;
                System.out.println("Warning: Could not parse /from date, storing as plain text.");
            }

            // Check if event ending date is recognisable, else store as plain text
            try {
                list.taskToDates[i] = LocalDate.parse(to, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
            catch (DateTimeParseException e) {
                list.taskToDates[i] = null;
                System.out.println("Warning: Could not parse /to date, storing as plain text.");
            }

            list.done[i] = false;

            System.out.println("Got it. I've added this task:");
            ui.printTask(i, list);

            list.taskCount++;
            storage.save(list);

            System.out.println("Now you have " + list.taskCount + " tasks in the list.");
        }

        // Delete a task
        else if (input.startsWith("delete ")) {
            int num = parseIndex(input.substring(7), list);
            if (num == -1) return;

            System.out.println("Noted. I've removed this task:");
            ui.printTask(num, list);

            for (int i = num; i < list.taskCount - 1; i++) {
                list.tasks[i] = list.tasks[i + 1];
                list.taskType[i] = list.taskType[i + 1];
                list.timeInfo[i] = list.timeInfo[i + 1];
                list.taskDates[i] = list.taskDates[i + 1];
                list.taskFromDates[i] = list.taskFromDates[i + 1];
                list.taskToDates[i] = list.taskToDates[i + 1];
                list.done[i] = list.done[i + 1];
            }

            list.taskCount--;
            storage.save(list);

            System.out.println("Now you have " + list.taskCount + " tasks in the list.");
        }

        // Find tasks by keyword
        else if (input.startsWith("find ")) {
            String keyword = input.substring(5).trim();
            if (keyword.isEmpty()) {
                System.out.println("Error: Please provide a keyword to search for.");
                return;
            }

            System.out.println("Here are the matching tasks in your list:");
            int count = 0;
            for (int i = 0; i < list.taskCount; i++) {
                if (list.tasks[i].contains(keyword)) {
                    count++;
                    System.out.print(count + ".");
                    ui.printTask(i, list);
                }
            }

            if (count == 0) {
                System.out.println("No matching tasks found.");
            }
        }

        // Show a random motivational quote
        else if (input.equals("cheer")) {
            String quote = storage.getRandomCheer();
            System.out.println(quote);
        }

        // Command is not understood at all
        else {
            System.out.println("Error: I don't understand that command.");
        }
    }

    /**
     * Converts a string input into a task index for the task list.
     * @param input the string containing the task number
     * @param list the current task list
     * @return the zero-based index of the task, or -1 if invalid
     */
    private int parseIndex(String input, TaskList list) {
        int num;
        try {
            num = Integer.parseInt(input.trim()) - 1;
        }
        catch (NumberFormatException e) {
            System.out.println("Error: Task number must be a number!");
            return -1;
        }

        if (num < 0 || num >= list.taskCount) {
            System.out.println("Error: That task number does not exist!");
            return -1;
        }

        return num;
    }
}