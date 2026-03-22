package echo;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StorageTest {
    @Test
    void load_todo_correctlyLoaded() throws Exception {
        java.io.File dir = new java.io.File("./data");
        dir.mkdir();

        java.io.PrintWriter pw = new java.io.PrintWriter("./data/tasks.txt");
        pw.println("T | 0 | read book");
        pw.close();

        TaskList list = new TaskList();
        Storage storage = new Storage();

        storage.load(list);

        assertEquals(1, list.taskCount);
        assertEquals("read book", list.tasks[0]);
        assertEquals(TaskList.TaskType.T, list.taskType[0]);
    }

    @Test
    void load_deadline_validDate() throws Exception {
        java.io.PrintWriter pw = new java.io.PrintWriter("./data/tasks.txt");
        pw.println("D | 1 | submit | 2025-12-01");
        pw.close();

        TaskList list = new TaskList();
        new Storage().load(list);

        assertEquals(1, list.taskCount);
        assertNotNull(list.taskDates[0]);
        assertTrue(list.done[0]);
    }

    @Test
    void load_event_valid() throws Exception {
        java.io.PrintWriter pw = new java.io.PrintWriter("./data/tasks.txt");
        pw.println("E | 0 | camp | 2025-01-01 | 2025-01-02");
        pw.close();

        TaskList list = new TaskList();
        new Storage().load(list);

        assertEquals(1, list.taskCount);
        assertNotNull(list.taskFromDates[0]);
        assertNotNull(list.taskToDates[0]);
    }

    @Test
    void load_corruptedLine_skipped() throws Exception {
        java.io.PrintWriter pw = new java.io.PrintWriter("./data/tasks.txt");
        pw.println("INVALID LINE");
        pw.close();

        TaskList list = new TaskList();
        new Storage().load(list);

        assertEquals(0, list.taskCount);
    }

    @Test
    void load_noFile_startsEmpty() {
        java.io.File file = new java.io.File("./data/tasks.txt");
        file.delete();

        TaskList list = new TaskList();
        new Storage().load(list);

        assertEquals(0, list.taskCount);
    }
}