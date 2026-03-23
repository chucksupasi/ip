package echo;

import java.time.LocalDate;

/**
 * Stores all tasks and their details in fixed-size arrays.
 */
public class TaskList {

    /**
     * Represents the type of a task: To-do, Deadline, or Event.
     */
    enum TaskType {
        T, D, E
    }

    private static final int MAX_TASKS = 100;

    String[] tasks = new String[MAX_TASKS];
    int taskCount = 0;
    boolean[] done = new boolean[MAX_TASKS];
    String[] timeInfo = new String[MAX_TASKS]; // Raw test for date that user inputs
    TaskType[] taskType = new TaskType[MAX_TASKS]; // Only T, D, E
    LocalDate[] taskDates = new LocalDate[MAX_TASKS]; // Deadline do-by date
    LocalDate[] taskFromDates = new LocalDate[MAX_TASKS]; // Event start date
    LocalDate[] taskToDates = new LocalDate[MAX_TASKS]; // Event end date
}