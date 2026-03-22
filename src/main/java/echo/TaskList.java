package echo;

import java.time.LocalDate;

/**
 * Stores all tasks and their details in arrays.
 */
public class TaskList {

    /**
     * Represents the type of a task: To-do, Deadline, or Event.
     */
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