package echo;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Handles loading, saving tasks, and retrieving motivational quotes.
 */
public class Storage {

    private static final String FILE_PATH = "./data/tasks.txt"; // File tasks.txt is in folder named data
    private static final String CHEER_PATH = "./data/cheer.txt"; // File cheer.txt is in folder named data

    /**
     * Loads tasks from the storage file into the given task list.
     * @param list the task list to populate with saved tasks
     */
    public void load(TaskList list) {
        assert list != null : "Internal Error: TaskList cannot be null";
        File file = new File(FILE_PATH);

        // Check if the file is there. If not, the data starts empty.
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;

                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(" \\| ");
                    if (parts.length < 3) continue; // Skip corrupted lines but others are still read

                    int i = list.taskCount;
                    assert i >= 0 && i < TaskList.MAX_TASKS : "Internal Error: index out of bounds while loading";

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

    /**
     * Saves tasks from the task list into the storage file.
     * @param list the task list to save to disk
     */
    public void save(TaskList list) {
        assert list != null : "Internal Error: TaskList cannot be null";
        assert list.taskCount >= 0 && list.taskCount <= TaskList.MAX_TASKS : "Internal Error: taskCount out of bounds while saving";

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

    /**
     * Returns a random motivational quote from the cheer file.
     * @return a random cheer quote or an error message if unavailable
     */
    public String getRandomCheer() {
        List<String> quotes = new ArrayList<>();

        File file = new File(CHEER_PATH);

        if (!file.exists()) {
            return "No cheer file found.";
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    quotes.add(line);
                }
            }
        }
        catch (IOException e) {
            return "Error reading cheer file.";
        }

        assert quotes != null : "Internal Error: quotes list should never be null";
        if (quotes.isEmpty()) {
            return "No quotes available.";
        }

        Random rand = new Random();
        return quotes.get(rand.nextInt(quotes.size()));
    }
}