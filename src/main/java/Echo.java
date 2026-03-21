import java.util.Scanner;

public class Echo {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String[] tasks = new String[100];
        int taskCount = 0;
        boolean[] done = new boolean[100];
        String[] timeInfo = new String[100];
        String[] taskType = new String[100];

        System.out.println("Hey there! I'm Echo, your personal assistant");
        System.out.println("What can I do for you?");

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
                    if (taskType[i].equals("T")) {
                        System.out.println((i + 1) + ". " + typeTag + status + " " + tasks[i]);
                    } else {
                        System.out.println((i + 1) + ". " + typeTag + status + " "
                                + tasks[i] + " (" + timeInfo[i] + ")");
                    }
                }
            }

            // Mark a task as done
            else if (input.startsWith("mark ")) {
                int num = Integer.parseInt(input.substring(5)) - 1;
                done[num] = true;
                System.out.println("Nice! I've marked this task as done:");
                printTask(num, tasks, taskType, timeInfo, done);
            }

            // Unmark a task
            else if (input.startsWith("unmark ")) {
                int num = Integer.parseInt(input.substring(7)) - 1;
                done[num] = false;
                System.out.println("OK, I've marked this task as not done yet:");
                printTask(num, tasks, taskType, timeInfo, done);
            }

            // Create a todo
            else if (input.startsWith("todo ")) {
                String desc = input.substring(5);

                tasks[taskCount] = desc;
                taskType[taskCount] = "T";
                timeInfo[taskCount] = "";
                done[taskCount] = false;

                System.out.println("Got it. I've added this task:");
                printTask(taskCount, tasks, taskType, timeInfo, done);

                taskCount++;
                System.out.println("Now you have " + taskCount + " tasks in the list.");
            }

            // Create a deadline
            else if (input.startsWith("deadline ")) {
                String[] parts = input.substring(9).split(" /by ");

                String desc = parts[0];
                String by = parts[1];

                tasks[taskCount] = desc;
                taskType[taskCount] = "D";
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
                String desc = firstSplit[0];

                String[] secondSplit = firstSplit[1].split(" /to ");
                String from = secondSplit[0];
                String to = secondSplit[1];

                tasks[taskCount] = desc;
                taskType[taskCount] = "E";
                timeInfo[taskCount] = "from: " + from + " to: " + to;
                done[taskCount] = false;

                System.out.println("Got it. I've added this task:");
                printTask(taskCount, tasks, taskType, timeInfo, done);

                taskCount++;
                System.out.println("Now you have " + taskCount + " tasks in the list.");
            }
        }
        sc.close();
    }

    // Function to print the task
    public static void printTask(int i, String[] tasks, String[] type, String[] time, boolean[] done) {
        String status = done[i] ? "[X]" : "[ ]";
        String typeTag = "[" + type[i] + "]";
        if (type[i].equals("T")) {
            System.out.println(typeTag + status + " " + tasks[i]);
        }
        else {
            System.out.println(typeTag + status + " " + tasks[i] + " (" + time[i] + ")");
        }
    }
}