package echo;

import java.time.format.DateTimeFormatter;

/**
 * Handles all user-facing messages and formatting of task output.
 */
public class Ui {

    public String printingMessage = "";
    public String listingMessage = "";

    private static final String DISPLAY_DATE_FORMAT = "MMM dd yyyy";

    /**
     * Returns the welcome message shown to the user at startup.
     * @return welcome message string
     */
    public static String showWelcome() {
        return "Hey there! I'm Echo, your personal assistant\n" + "What can I do for you?\n";
    }

    /**
     * Returns the goodbye message shown when exiting the program.
     * @return goodbye message string
     */
    public static String showBye() {
        return "Bye. Hope to see you again soon!\n";
    }

    /**
     * Formats and returns a single task's display string with its status and date info.
     * @param i index of the task in the task list
     * @param list the task list containing the task
     * @return formatted task string
     */
    public String printTask(int i, TaskList list) {
        assert i >= 0 && i <= list.taskCount : "Internal Error: index out of bounds in printTask";
        assert list.tasks[i] != null : "Internal Error: task description is null";

        printingMessage = "";

        String status = list.done[i] ? "[X]" : "[ ]";
        String typeTag = "[" + list.taskType[i] + "]";
        String timeStr = list.timeInfo[i];

        // If it is an event, put both start and end time
        if (list.taskType[i] == TaskList.TaskType.E) {
            String fromStr = list.taskFromDates[i] != null ? list.taskFromDates[i].format(DateTimeFormatter.ofPattern(DISPLAY_DATE_FORMAT)) : "unknown";
            String toStr = list.taskToDates[i] != null ? list.taskToDates[i].format(DateTimeFormatter.ofPattern(DISPLAY_DATE_FORMAT)) : "unknown";
            timeStr = "from: " + fromStr + " to: " + toStr;
        }
        // If it is a deadline, put the do-by date
        else if (list.taskDates[i] != null) {
            timeStr = list.taskDates[i].format(DateTimeFormatter.ofPattern(DISPLAY_DATE_FORMAT));
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
     * Formats and returns a list of all tasks in the task list.
     * @param list the task list to display
     * @return formatted string of all tasks
     */
    public String showList(TaskList list) {
        assert list.taskCount >= 0 && list.taskCount <= TaskList.MAX_TASKS : "Internal Error: taskCount out of bounds in showList";

        listingMessage = "";
        listingMessage = listingMessage + "Here are the tasks in your list:\n";
        for (int i = 0; i < list.taskCount; i++) {
            listingMessage = listingMessage + (i + 1) + "." + printTask(i, list) + "\n";
        }
        return listingMessage;
    }
}