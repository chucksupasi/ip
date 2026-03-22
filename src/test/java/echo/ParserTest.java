package echo;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {
    Parser parser = new Parser();
    TaskList list = new TaskList();
    Ui ui = new Ui();
    Storage storage = new Storage();

    @Test
    void todo_validInput_addsTask() {
        parser.handleCommand("todo read book", list, ui, storage);

        assertEquals(1, list.taskCount);
        assertEquals("read book", list.tasks[0]);
        assertEquals(TaskList.TaskType.T, list.taskType[0]);
        assertFalse(list.done[0]);
    }

    @Test
    void todo_emptyDescription_doesNotAddTask() {
        parser.handleCommand("todo   ", list, ui, storage);

        assertEquals(0, list.taskCount);
    }

    @Test
    void mark_validIndex_marksTaskDone() {
        list.tasks[0] = "task";
        list.taskType[0] = TaskList.TaskType.T;
        list.done[0] = false;
        list.taskCount = 1;

        parser.handleCommand("mark 1", list, ui, storage);

        assertTrue(list.done[0]);
    }

    @Test
    void deadline_validDate_setsDate() {
        parser.handleCommand("deadline submit /by 2025-12-01", list, ui, storage);

        assertEquals(1, list.taskCount);
        assertEquals(TaskList.TaskType.D, list.taskType[0]);
        assertNotNull(list.taskDates[0]);
    }

    @Test
    void delete_removesTask() {
        list.tasks[0] = "A";
        list.taskType[0] = TaskList.TaskType.T;
        list.done[0] = false;

        list.tasks[1] = "B";
        list.taskType[1] = TaskList.TaskType.T;
        list.done[1] = false;

        list.taskCount = 2;

        parser.handleCommand("delete 1", list, ui, storage);

        assertEquals(1, list.taskCount);
        assertEquals("B", list.tasks[0]);
    }

    @Test
    void event_missingTo_notAdded() {
        parser.handleCommand("event camp /from 2025-01-01", list, ui, storage);

        assertEquals(0, list.taskCount);
    }
}