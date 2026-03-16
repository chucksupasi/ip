import java.util.Scanner;

public class Echo {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String[] tasks = new String[100];
        int taskCount = 0;
        boolean[] done = new boolean[100];

        System.out.println("Hey there! I'm Echo, your personal assistant");
        System.out.println("What can I do for you?");

        while (true) {
            String input = sc.nextLine();
            if (input.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                break;
            }
            else if (input.equals("list")) {
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    String status = done[i] ? "[X]" : "[ ]";
                    System.out.println((i + 1) + ". " + status + " " + tasks[i]);
                }
            }
            else if (input.startsWith("mark ")) {
                int num = Integer.parseInt(input.substring(5)) - 1;
                done[num] = true;
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("[X] " + tasks[num]);
            }
            else if (input.startsWith("unmark ")) { // ADDED: mark task as not done
                int num = Integer.parseInt(input.substring(7)) - 1;
                done[num] = false;
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println("[ ] " + tasks[num]);
            }
            else {
                tasks[taskCount] = input;
                done[taskCount] = false;
                taskCount++;
                System.out.println("added: " + input);
            }
        }
        sc.close();
    }
}