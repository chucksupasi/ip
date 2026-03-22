import java.time.LocalDate;

// Manages all data using different data structures
public class TaskList {

    // Enum for the 3 types of tasks
    enum TaskType {
        T, D, E
    }

    String[] tasks = new String[100];
    int taskCount = 0;
    boolean[] done = new boolean[100];
    String[] timeInfo = new String[100]; // Raw test for date that user inputs
    TaskType[] taskType = new TaskType[100]; // Only T, D, E
    LocalDate[] taskDates = new LocalDate[100]; // Deadline do-by date
    LocalDate[] taskFromDates = new LocalDate[100]; // Event start date
    LocalDate[] taskToDates = new LocalDate[100]; // Event end date
}