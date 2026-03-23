package echo;

import java.time.format.DateTimeFormatter;

/**
 * Handles all messages displayed to the user.
 */
public class Ui {

    public String printingMessage = "";
    public String listingMessage = "";

    /**
     * Prints a welcome message to the user.
     */
    public static String showWelcome() {
        return "Hey there! I'm Echo, your personal assistant\n" + "What can I do for you?\n";
    }

    /**
     * Prints a goodbye message to the user.
     */
    public static String showBye() {
        return "Bye. Hope to see you again soon!\n";
    }

    /**
     * Prints a single task with its status and time information.
     * @param i the index of the task in the task list
     * @param list the task list containing the task
     */
    public String printTask(int i, TaskList list) {
        printingMessage = "";

        String status = list.done[i] ? "[X]" : "[ ]";
        String typeTag = "[" + list.taskType[i] + "]";
        String timeStr = list.timeInfo[i];

        // If it is an event, put both start and end time
        if (list.taskType[i] == TaskList.TaskType.E) {
            String fromStr = list.taskFromDates[i] != null ? list.taskFromDates[i].format(DateTimeFormatter.ofPattern("MMM dd yyyy")) : "unknown";
            String toStr = list.taskToDates[i] != null ? list.taskToDates[i].format(DateTimeFormatter.ofPattern("MMM dd yyyy")) : "unknown";
            timeStr = "from: " + fromStr + " to: " + toStr;
        }
        // If it is a deadline, put the do-by date
        else if (list.taskDates[i] != null) {
            timeStr = list.taskDates[i].format(DateTimeFormatter.ofPattern("MMM dd yyyy"));
        }

        // If it is a to-do, there is no date
        if (list.taskType[i] == TaskList.TaskType.T) {
            printingMessage = printingMessage + typeTag + status + " " + list.tasks[i] + "\n";
        }
        else {
            printingMessage = printingMessage + typeTag + status + " " + list.tasks[i] + " (" + timeStr + ")" + "\n";
        }
        return printingMessage;
    }

    /**
     * Prints all tasks in the task list.
     * @param list the task list to display
     */
    public String showList(TaskList list) {
        listingMessage = "";
        listingMessage = listingMessage + "Here are the tasks in your list:\n";
        for (int i = 0; i < list.taskCount; i++) {
            listingMessage = listingMessage + (i + 1) + "." + printTask(i, list) + "\n";
        }
        return listingMessage;
    }
}