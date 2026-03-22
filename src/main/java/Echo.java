import java.util.Scanner;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

// Echo chatbot
public class Echo {
    // Enum for the 3 types of tasks
    enum TaskType {
        T, D, E
    }

    private static final String FILE_PATH = "./data/tasks.txt"; // File tasks.txt is in folder named data

    // Main programme
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String[] tasks = new String[100];
        int taskCount = 0;
        boolean[] done = new boolean[100];
        String[] timeInfo = new String[100];
        TaskType[] taskType = new TaskType[100];
        LocalDate[] taskDates = new LocalDate[100];

        // Load data from existing file at the start of programme
        File file = new File(FILE_PATH);
        // Check if the file is there. If not, the data starts empty.
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(" \\| ");
                    if (parts.length < 3) continue; // Skip corrupted lines but others are still read
                    taskType[taskCount] = TaskType.valueOf(parts[0]);
                    done[taskCount] = parts[1].equals("1");
                    tasks[taskCount] = parts[2];
                    if (parts.length > 3) {
                        timeInfo[taskCount] = parts[3];
                        // Store as a Java-recognised date
                        try {
                            taskDates[taskCount] = LocalDate.parse(parts[3], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        }
                        // No date stored in case date cannot be recognised
                        catch (DateTimeParseException e) {
                            taskDates[taskCount] = null;
                        }
                    } else {
                        timeInfo[taskCount] = "";
                        taskDates[taskCount] = null;
                    }
                    taskCount++;
                }
            }
            // Error message prints if tasks in file cannot be recognised
            catch (IOException e) {
                System.out.println("Error loading tasks.");
            }
        }

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
                    }
                    else {
                        String formattedTime = timeInfo[i];
                        if (taskDates[i] != null) {
                            formattedTime = taskDates[i].format(DateTimeFormatter.ofPattern("MMM dd yyyy"));
                        }
                        System.out.println((i + 1) + "." + typeTag + status + " " + tasks[i] + " (" + formattedTime + ")");
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
                saveTasks(tasks, taskType, timeInfo, done, taskDates, taskCount);
                System.out.println("Nice! I've marked this task as done:");
                printTask(num, tasks, taskType, timeInfo, done, taskDates);
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
                saveTasks(tasks, taskType, timeInfo, done, taskDates, taskCount);
                System.out.println("OK, I've marked this task as not done yet:");
                printTask(num, tasks, taskType, timeInfo, done, taskDates);
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
                printTask(taskCount, tasks, taskType, timeInfo, done, taskDates);
                taskCount++;
                saveTasks(tasks, taskType, timeInfo, done, taskDates, taskCount);
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

                // Store the date as Java-recognised if possible
                try {
                    taskDates[taskCount] = LocalDate.parse(by, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                }
                // Stores nothing in case date cannot be parsed, and store as plain text
                catch (DateTimeParseException e) {
                    taskDates[taskCount] = null;
                    System.out.println("Warning: Could not parse date, storing as plain text.");
                }

                done[taskCount] = false;
                System.out.println("Got it. I've added this task:");
                printTask(taskCount, tasks, taskType, timeInfo, done, taskDates);
                taskCount++;
                saveTasks(tasks, taskType, timeInfo, done, taskDates, taskCount);
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
                // Implement the creation of an event and store the data
                tasks[taskCount] = desc;
                taskType[taskCount] = TaskType.E;
                timeInfo[taskCount] = "from: " + from + " to: " + to;

                // Store the date as Java-recognised if possible
                try {
                    taskDates[taskCount] = LocalDate.parse(from, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                }
                // Stores nothing in case date cannot be parsed, and store as plain text
                catch (DateTimeParseException e) {
                    taskDates[taskCount] = null;
                    System.out.println("Warning: Could not parse date, storing as plain text.");
                }

                done[taskCount] = false;
                System.out.println("Got it. I've added this task:");
                printTask(taskCount, tasks, taskType, timeInfo, done, taskDates);
                taskCount++;
                saveTasks(tasks, taskType, timeInfo, done, taskDates, taskCount);
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
                printTask(num, tasks, taskType, timeInfo, done, taskDates);
                for (int i = num; i < taskCount - 1; i++) {
                    tasks[i] = tasks[i + 1];
                    taskType[i] = taskType[i + 1];
                    timeInfo[i] = timeInfo[i + 1];
                    taskDates[i] = taskDates[i + 1];
                    done[i] = done[i + 1];
                }
                taskCount--;
                saveTasks(tasks, taskType, timeInfo, done, taskDates, taskCount);
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
    public static void printTask(int i, String[] tasks, TaskType[] type, String[] time, boolean[] done, LocalDate[] dates) {
        String status = done[i] ? "[X]" : "[ ]";
        String typeTag = "[" + type[i] + "]";
        String timeStr = time[i];
        // See if date can be Java-recognised to store
        if (dates[i] != null) {
            timeStr = dates[i].format(DateTimeFormatter.ofPattern("MMM dd yyyy"));
        }
        // If it is a to-do, there is no date
        if (type[i] == TaskType.T) {
            System.out.println(typeTag + status + " " + tasks[i]);
        }
        else {
            System.out.println(typeTag + status + " " + tasks[i] + " (" + timeStr + ")");
        }
    }

    // Function to save task into file each time one is added
    public static void saveTasks(String[] tasks, TaskType[] type, String[] time, boolean[] done, LocalDate[] dates, int numOfTasks) {
        try {
            File dir = new File("./data");
            // Create folder called data if it doesn't exist
            if (!dir.exists()) {
                dir.mkdir(); // ADDED: create folder if missing
            }
            // Create a file
            BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH));
            for (int i = 0; i < numOfTasks; i++) {
                String line = type[i] + " | " + (done[i] ? "1" : "0") + " | " + tasks[i];
                // If it is anything other a to-do, there is a date to add to the line
                if (type[i] != TaskType.T) {
                    // See if date can be Java-recognised to store
                    if (dates[i] != null) {
                        line += " | " + dates[i].format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    }
                    else {
                        line += " | " + time[i];
                    }
                }
                bw.write(line);
                bw.newLine();
            }
            bw.close();
        }
        // Print error message if cannot save the task
        catch (IOException e) {
            System.out.println("Error saving tasks.");
        }
    }
}