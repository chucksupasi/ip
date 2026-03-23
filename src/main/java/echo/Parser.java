package echo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Parses user input and executes commands for the chatbot.
 */
public class Parser {

    public String printingMessage = "";

    /**
     * Handles a user command and updates the task list accordingly.
     * @param input the command entered by the user
     * @param list the current task list
     * @param ui the user interface for printing messages
     * @param storage the storage handler for saving tasks
     */
    public String handleCommand(String input, TaskList list, Ui ui, Storage storage) {
        // Clear printing message
        printingMessage = "";

        // List all tasks
        if (input.equals("list")) {
            printingMessage = printingMessage + ui.showList(list) + "\n";
        }

        // Mark a task as done
        else if (input.startsWith("mark ")) {
            int num = parseIndex(input.substring(5), list);
            if (num == -1) return printingMessage;

            list.done[num] = true;
            storage.save(list);

            printingMessage = printingMessage + "Nice! I've marked this task as done:\n" + ui.printTask(num, list) + "\n";
        }

        // Unmark a task
        else if (input.startsWith("unmark ")) {
            int num = parseIndex(input.substring(7), list);
            if (num == -1) return printingMessage;

            list.done[num] = false;
            storage.save(list);

            printingMessage = printingMessage + "OK, I've marked this task as not done yet:\n" + ui.printTask(num, list) + "\n";
        }

        // create todo
        else if (input.startsWith("todo ")) {
            String desc = input.substring(5).trim();

            // Error if no description
            if (desc.isEmpty()) {
                printingMessage = printingMessage + "Error: The description of a todo cannot be empty.\n";
                return printingMessage;
            }

            int i = list.taskCount;
            list.tasks[i] = desc;
            list.taskType[i] = TaskList.TaskType.T;
            list.timeInfo[i] = "";
            list.done[i] = false;

            list.taskCount++;
            storage.save(list);

            printingMessage = printingMessage + "Got it. I've added this task:\n" + ui.printTask(i, list) + "\n" + "Now you have " + list.taskCount + " tasks in the list.\n";
        }

        // Create deadline
        else if (input.startsWith("deadline ")) {
            String[] parts = input.substring(9).split(" /by ");

            // No description or date
            if (parts.length < 2) {
                printingMessage = printingMessage + "Error: Deadline must have a description and /by date.\n";
                return printingMessage;
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
                printingMessage = printingMessage + "Warning: Could not parse date, storing as plain text.\n";
            }

            list.done[i] = false;
            list.taskCount++;
            storage.save(list);

            printingMessage = printingMessage + "Got it. I've added this task:\n" + ui.printTask(i, list) + "\n" + "Now you have " + list.taskCount + " tasks in the list.\n";
        }

        // Create event
        else if (input.startsWith("event ")) {
            String[] firstSplit = input.substring(6).split(" /from ");

            if (firstSplit.length < 2) {
                printingMessage = printingMessage + "Error: Event must have a description and /from time.\n";
                return printingMessage;
            }

            String desc = firstSplit[0].trim();
            String[] secondSplit = firstSplit[1].split(" /to ");

            if (secondSplit.length < 2) {
                printingMessage = printingMessage + "Error: Event must have /from and /to times.\n";
                return printingMessage;
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
                printingMessage = printingMessage + "Warning: Could not parse /from date, storing as plain text.\n";
            }

            // Check if event ending date is recognisable, else store as plain text
            try {
                list.taskToDates[i] = LocalDate.parse(to, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
            catch (DateTimeParseException e) {
                list.taskToDates[i] = null;
                printingMessage = printingMessage + "Warning: Could not parse /to date, storing as plain text.\n";
            }

            list.done[i] = false;

            printingMessage = printingMessage + "Got it. I've added this task:\n" + ui.printTask(i, list) + "\n";

            list.taskCount++;
            storage.save(list);

            printingMessage = printingMessage + "Now you have " + list.taskCount + " tasks in the list.\n";
        }

        // Delete a task
        else if (input.startsWith("delete ")) {
            int num = parseIndex(input.substring(7), list);
            if (num == -1) return printingMessage;

            printingMessage = printingMessage + "Noted. I've removed this task:\n" + ui.printTask(num, list) + "\n";

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

            printingMessage = printingMessage + "Now you have " + list.taskCount + " tasks in the list.\n";
        }

        // Find tasks by keyword
        else if (input.startsWith("find ")) {
            String keyword = input.substring(5).trim();
            if (keyword.isEmpty()) {
                printingMessage = printingMessage + "Error: Please provide a keyword to search for.\n";
                return printingMessage;
            }

            printingMessage = printingMessage + "Here are the matching tasks in your list:\n";
            int count = 0;
            for (int i = 0; i < list.taskCount; i++) {
                if (list.tasks[i].contains(keyword)) {
                    count++;
                    printingMessage = printingMessage + count + "." + ui.printTask(i, list) + "\n";
                }
            }

            if (count == 0) {
                printingMessage = printingMessage + "No matching tasks found.\n";
            }
        }

        // Show a random motivational quote
        else if (input.equals("cheer")) {
            String quote = storage.getRandomCheer();
            printingMessage = printingMessage + quote + "\n";
        }

        // Command is not understood at all
        else {
            printingMessage = printingMessage + "Error: I don't understand that command.\n";
        }
        return printingMessage;
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
            printingMessage = printingMessage + "Error: Task number must be a number!\n";
            return -1;
        }

        if (num < 0 || num >= list.taskCount) {
            printingMessage = printingMessage + "Error: That task number does not exist!\n";
            return -1;
        }

        return num;
    }
}