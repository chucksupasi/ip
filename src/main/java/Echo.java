import java.util.Scanner;
import java.io.*;

// Echo chatbot
public class Echo {
    // Enum for the 3 types of tasks
    enum TaskType {
        T, D, E
    }
    // Main programme
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String[] tasks = new String[100];
        int taskCount = 0;
        boolean[] done = new boolean[100];
        String[] timeInfo = new String[100];
        TaskType[] taskType = new TaskType[100];

        // Introduction
        System.out.println("Hey there! I'm Echo, your personal assistant");
        System.out.println("What can I do for you?");

        // Main loop where users can chat continuously
        while (true) {
            String input = sc.nextLine();
            if (input.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                break;
            }

            // List all tasks
            else if (input.equals("list")) {
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    String status = done[i] ? "[X]" : "[ ]";
                    String typeTag = "[" + taskType[i] + "]";
                    if (taskType[i] == TaskType.T) {
                        System.out.println((i + 1) + "." + typeTag + status + " " + tasks[i]);
                    } else {
                        System.out.println((i + 1) + "." + typeTag + status + " "
                                + tasks[i] + " (" + timeInfo[i] + ")");
                    }
                }
            }

            // Mark a task as done
            else if (input.startsWith("mark ")) {
                int num = -1;
                try {
                    String numStr = input.substring(5).trim();
                    num = Integer.parseInt(numStr) - 1;
                }
                catch (NumberFormatException e) {
                    System.out.println("Error: Task number must be a number!");
                    continue;
                }
                if (num < 0 || num >= taskCount) {
                    System.out.println("Error: That task number does not exist!");
                    continue;
                }
                done[num] = true;
                System.out.println("Nice! I've marked this task as done:");
                printTask(num, tasks, taskType, timeInfo, done);
            }

            // Unmark a task
            else if (input.startsWith("unmark ")) {
                int num = -1;
                // Check if task is a number and get index
                try {
                    String numStr = input.substring(7).trim();
                    num = Integer.parseInt(numStr) - 1;
                }
                catch (NumberFormatException e) {
                    System.out.println("Error: Task number must be a number!");
                    continue;
                }
                // Check if task number exists
                if (num < 0 || num >= taskCount) {
                    System.out.println("Error: That task number does not exist!");
                    continue;
                }
                // Implement the unmarking
                done[num] = false;
                System.out.println("OK, I've marked this task as not done yet:");
                printTask(num, tasks, taskType, timeInfo, done);
            }

            // Create a task without any deadline (to-do)
            else if (input.startsWith("todo ")) {
                String desc = input.substring(5).trim();
                // Check if a description exists
                if (desc.isEmpty()) {
                    System.out.println("Error: The description of a todo cannot be empty.");
                    continue;
                }
                // Implement the creation of to-do and store the data
                tasks[taskCount] = desc;
                taskType[taskCount] = TaskType.T;
                timeInfo[taskCount] = "";
                done[taskCount] = false;
                System.out.println("Got it. I've added this task:");
                printTask(taskCount, tasks, taskType, timeInfo, done);
                taskCount++;
                System.out.println("Now you have " + taskCount + " tasks in the list.");
            }

            // Create a task with a deadline
            else if (input.startsWith("deadline ")) {
                String[] parts = input.substring(9).split(" /by ");
                // Check if a description and deadline exists
                if (parts.length < 2) {
                    System.out.println("Error: Deadline must have a description and /by date.");
                    continue;
                }
                // Implement the creation of a task with deadline and store the data
                String desc = parts[0].trim();
                String by = parts[1].trim();
                tasks[taskCount] = desc;
                taskType[taskCount] = TaskType.D;
                timeInfo[taskCount] = "by: " + by;
                done[taskCount] = false;
                System.out.println("Got it. I've added this task:");
                printTask(taskCount, tasks, taskType, timeInfo, done);
                taskCount++;
                System.out.println("Now you have " + taskCount + " tasks in the list.");
            }

            // Create an event
            else if (input.startsWith("event ")) {
                String[] firstSplit = input.substring(6).split(" /from ");
                // Check if there is a description and start time
                if (firstSplit.length < 2) {
                    System.out.println("Error: Event must have a description and /from time.");
                    continue;
                }
                String desc = firstSplit[0].trim();
                String[] secondSplit = firstSplit[1].split(" /to ");
                // Check if there is an event start and end time
                if (secondSplit.length < 2) {
                    System.out.println("Error: Event must have /from and /to times.");
                    continue;
                }
                String from = secondSplit[0].trim();
                String to = secondSplit[1].trim();
                // Implement the
                tasks[taskCount] = desc;
                taskType[taskCount] = TaskType.E;
                timeInfo[taskCount] = "from: " + from + " to: " + to;
                done[taskCount] = false;
                System.out.println("Got it. I've added this task:");
                printTask(taskCount, tasks, taskType, timeInfo, done);
                taskCount++;
                System.out.println("Now you have " + taskCount + " tasks in the list.");
            }

            // Delete any type of task
            else if (input.startsWith("delete ")) {
                int num = -1;
                // Check if the task is a number
                try {
                    String numStr = input.substring(7).trim();
                    num = Integer.parseInt(numStr) - 1;
                }
                catch (NumberFormatException e) {
                    System.out.println("Error: Task number must be a number!");
                    continue;
                }
                // Check if the task number exists
                if (num < 0 || num >= taskCount) {
                    System.out.println("Error: That task number does not exist!");
                    continue;
                }
                // Implement the deletion of the task
                System.out.println("Noted. I've removed this task:");
                printTask(num, tasks, taskType, timeInfo, done);
                for (int i = num; i < taskCount - 1; i++) {
                    tasks[i] = tasks[i + 1];
                    taskType[i] = taskType[i + 1];
                    timeInfo[i] = timeInfo[i + 1];
                    done[i] = done[i + 1];
                }
                taskCount--;
                System.out.println("Now you have " + taskCount + " tasks in the list.");
            }

            // If none of the above commands match, an error message appears
            else {
                System.out.println("Error: I don't understand that command.");
            }
        }
        sc.close();
    }

    // Function to print the task
    public static void printTask(int i, String[] tasks, TaskType[] type, String[] time, boolean[] done) {
        String status = done[i] ? "[X]" : "[ ]";
        String typeTag = "[" + type[i] + "]";
        if (type[i] == TaskType.T) {
            System.out.println(typeTag + status + " " + tasks[i]);
        }
        else {
            System.out.println(typeTag + status + " " + tasks[i] + " (" + time[i] + ")");
        }
    }
}