import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

// Stores data on hard disk using file
public class Storage {

    private static final String FILE_PATH = "./data/tasks.txt"; // File tasks.txt is in folder named data

    // Load data from existing file at the start of programme
    public void load(TaskList list) {
        File file = new File(FILE_PATH);

        // Check if the file is there. If not, the data starts empty.
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;

                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(" \\| ");
                    if (parts.length < 3) continue; // Skip corrupted lines but others are still read

                    int i = list.taskCount;

                    list.taskType[i] = TaskList.TaskType.valueOf(parts[0]);
                    list.done[i] = parts[1].equals("1");
                    list.tasks[i] = parts[2];

                    if (parts.length > 3) {
                        // If the task is an event, load both from and to dates
                        if (list.taskType[i] == TaskList.TaskType.E) {
                            // parts[3] is the event start date
                            try {
                                list.taskFromDates[i] = LocalDate.parse(parts[3], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                            }
                            catch (DateTimeParseException e) {
                                list.taskFromDates[i] = null;
                            }
                            // parts[4] is the event end date
                            if (parts.length > 4) {
                                try {
                                    list.taskToDates[i] = LocalDate.parse(parts[4], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                                }
                                catch (DateTimeParseException e) {
                                    list.taskToDates[i] = null;
                                }
                            }
                            else {
                                list.taskToDates[i] = null;
                            }

                            String toPart = (parts.length > 4) ? parts[4] : "";
                            list.timeInfo[i] = "from: " + parts[3] + " to: " + toPart;
                            list.taskDates[i] = null;
                        }
                        // If the task is a deadline, load the do-by date
                        else {
                            list.timeInfo[i] = parts[3];
                            try {
                                list.taskDates[i] = LocalDate.parse(parts[3], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                            }
                            catch (DateTimeParseException e) {
                                list.taskDates[i] = null;
                            }
                            list.taskFromDates[i] = null;
                            list.taskToDates[i] = null;
                        }
                    }
                    // If the event is a to-do, there is no need for dates
                    else {
                        list.timeInfo[i] = "";
                        list.taskDates[i] = null;
                        list.taskFromDates[i] = null;
                        list.taskToDates[i] = null;
                    }

                    list.taskCount++;
                }
            }
            // Error message prints if tasks in file cannot be recognised
            catch (IOException e) {
                System.out.println("Error loading tasks.");
            }
        }
    }

    // Function to save task into file each time one is added
    public void save(TaskList list) {
        try {
            File dir = new File("./data");

            // Create folder called data if it doesn't exist
            if (!dir.exists()) {
                dir.mkdir();
            }

            BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH));

            for (int i = 0; i < list.taskCount; i++) {
                String line = list.taskType[i] + " | " + (list.done[i] ? "1" : "0") + " | " + list.tasks[i];

                // Depending on the type of task, add the date
                if (list.taskType[i] == TaskList.TaskType.E) {
                    String from = list.taskFromDates[i] != null ? list.taskFromDates[i].format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "";
                    String to = list.taskToDates[i] != null ? list.taskToDates[i].format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "";
                    line += " | " + from + " | " + to;
                }
                else if (list.taskDates[i] != null) {
                    line += " | " + list.taskDates[i].format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                }
                else {
                    line += " | " + list.timeInfo[i];
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