package echo;

import java.time.format.DateTimeFormatter;

/**
 * Handles all messages displayed to the user.
 */
public class Ui {

    /**
     * Prints a welcome message to the user.
     */
    public void showWelcome() {
        System.out.println("Hey there! I'm echo.Echo, your personal assistant");
        System.out.println("What can I do for you?");
    }

    /**
     * Prints a goodbye message to the user.
     */
    public void showBye() {
        System.out.println("Bye. Hope to see you again soon!");
    }

    /**
     * Prints a single task with its status and time information.
     * @param i the index of the task in the task list
     * @param list the task list containing the task
     */
    public void printTask(int i, TaskList list) {
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
            System.out.println(typeTag + status + " " + list.tasks[i]);
        }
        else {
            System.out.println(typeTag + status + " " + list.tasks[i] + " (" + timeStr + ")");
        }
    }

    /**
     * Prints all tasks in the task list.
     * @param list the task list to display
     */
    public void showList(TaskList list) {
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < list.taskCount; i++) {
            System.out.print((i + 1) + ".");
            printTask(i, list);
        }
    }
}