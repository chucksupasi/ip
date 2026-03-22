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
        LocalDate[] taskDates = new LocalDate[100]; // For deadlines
        LocalDate[] taskFromDates = new LocalDate[100]; // For event start
        LocalDate[] taskToDates = new LocalDate[100]; // For event end

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
                        // If the task is an event, load both from and to dates
                        if (taskType[taskCount] == TaskType.E) {
                            // part[3] is the event start date
                            try {
                                taskFromDates[taskCount] = LocalDate.parse(parts[3], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                            }
                            catch (DateTimeParseException e) {
                                taskFromDates[taskCount] = null;
                            }
                            // part[4] is the event end date. Check if an end date exists for the event
                            if (parts.length > 4) {
                                try {
                                    taskToDates[taskCount] = LocalDate.parse(parts[4], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                                }
                                catch (DateTimeParseException e) {
                                    taskToDates[taskCount] = null;
                                }
                            }
                            else {
                                taskToDates[taskCount] = null;
                            }
                            String toPart = (parts.length > 4) ? parts[4] : "";
                            timeInfo[taskCount] = "from: " + parts[3] + " to: " + toPart;
                            taskDates[taskCount] = null;
                        }
                        // If the task is a deadline, load the do-by date
                        else {
                            timeInfo[taskCount] = parts[3];
                            try {
                                taskDates[taskCount] = LocalDate.parse(parts[3], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                            }
                            catch (DateTimeParseException e) {
                                taskDates[taskCount] = null;
                            }
                            taskFromDates[taskCount] = null;
                            taskToDates[taskCount] = null;
                        }
                    }
                    // If the event is a to-do, there is no need for dates
                    else {
                        timeInfo[taskCount] = "";
                        taskDates[taskCount] = null;
                        taskFromDates[taskCount] = null;
                        taskToDates[taskCount] = null;
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
                        // If it is an event, put both start and end time
                        if (taskType[i] == TaskType.E) {
                            String fromStr = taskFromDates[i] != null ? taskFromDates[i].format(DateTimeFormatter.ofPattern("MMM dd yyyy")) : "unknown";
                            String toStr = taskToDates[i] != null ? taskToDates[i].format(DateTimeFormatter.ofPattern("MMM dd yyyy")) : "unknown";
                            formattedTime = "from: " + fromStr + " to: " + toStr;
                        }
                        // If it is a deadline, put do-by date
                        else if (taskDates[i] != null) {
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
                saveTasks(tasks, taskType, timeInfo, done, taskDates, taskFromDates, taskToDates, taskCount);
                System.out.println("Nice! I've marked this task as done:");
                printTask(num, tasks, taskType, timeInfo, done, taskDates, taskFromDates, taskToDates);
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
                saveTasks(tasks, taskType, timeInfo, done, taskDates, taskFromDates, taskToDates, taskCount);
                System.out.println("OK, I've marked this task as not done yet:");
                printTask(num, tasks, taskType, timeInfo, done, taskDates, taskFromDates, taskToDates);
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
                printTask(taskCount, tasks, taskType, timeInfo, done, taskDates, taskFromDates, taskToDates);
                taskCount++;
                saveTasks(tasks, taskType, timeInfo, done, taskDates, taskFromDates, taskToDates, taskCount);
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
                printTask(taskCount, tasks, taskType, timeInfo, done, taskDates, taskFromDates, taskToDates);
                taskCount++;
                saveTasks(tasks, taskType, timeInfo, done, taskDates, taskFromDates, taskToDates, taskCount);
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

                // Store start and end as Java-recognised dates if possible
                try { taskFromDates[taskCount] = LocalDate.parse(from, DateTimeFormatter.ofPattern("yyyy-MM-dd")); }
                catch (DateTimeParseException e) { taskFromDates[taskCount] = null; System.out.println("Warning: Could not parse /from date, storing as plain text."); }
                try { taskToDates[taskCount] = LocalDate.parse(to, DateTimeFormatter.ofPattern("yyyy-MM-dd")); }
                catch (DateTimeParseException e) { taskToDates[taskCount] = null; System.out.println("Warning: Could not parse /to date, storing as plain text."); }

                done[taskCount] = false;
                System.out.println("Got it. I've added this task:");
                printTask(taskCount, tasks, taskType, timeInfo, done, taskDates, taskFromDates, taskToDates);
                taskCount++;
                saveTasks(tasks, taskType, timeInfo, done, taskDates, taskFromDates, taskToDates, taskCount);
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
                printTask(num, tasks, taskType, timeInfo, done, taskDates, taskFromDates, taskToDates);

                // Shift the remaining tasks if necessary
                for (int i = num; i < taskCount - 1; i++) {
                    tasks[i] = tasks[i + 1];
                    taskType[i] = taskType[i + 1];
                    timeInfo[i] = timeInfo[i + 1];
                    taskDates[i] = taskDates[i + 1];
                    taskFromDates[i] = taskFromDates[i + 1];
                    taskToDates[i] = taskToDates[i + 1];
                    done[i] = done[i + 1];
                }
                taskCount--;
                saveTasks(tasks, taskType, timeInfo, done, taskDates, taskFromDates, taskToDates, taskCount);
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
    public static void printTask(int i, String[] tasks, TaskType[] type, String[] time, boolean[] done, LocalDate[] dates, LocalDate[] fromDates, LocalDate[] toDates) {
        String status = done[i] ? "[X]" : "[ ]";
        String typeTag = "[" + type[i] + "]";
        String timeStr = time[i];
        // If it is an event, put both start and end time
        if (type[i] == TaskType.E) {
            String fromStr = fromDates[i] != null ? fromDates[i].format(DateTimeFormatter.ofPattern("MMM dd yyyy")) : "unknown";
            String toStr = toDates[i] != null ? toDates[i].format(DateTimeFormatter.ofPattern("MMM dd yyyy")) : "unknown";
            timeStr = "from: " + fromStr + " to: " + toStr;
        }
        // If it is a deadline, put the do-by date
        else if (dates[i] != null) {
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
    public static void saveTasks(String[] tasks, TaskType[] type, String[] time, boolean[] done, LocalDate[] dates, LocalDate[] fromDates, LocalDate[] toDates, int numOfTasks) {
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
                // Depending on the type of task, add the date
                if (type[i] == TaskType.E) {
                    String from = fromDates[i] != null ? fromDates[i].format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "";
                    String to = toDates[i] != null ? toDates[i].format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "";
                    line += " | " + from + " | " + to;
                }
                else if (dates[i] != null) {
                    line += " | " + dates[i].format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                }
                else {
                    line += " | " + time[i];
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