import java.util.Scanner;

public class Echo {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Hey there! I'm Echo, your personal assistant");
        System.out.println("What can I do for you?");

        while (true) {
            String input = sc.nextLine();
            if (input.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                break;
            }
            System.out.println(input);
        }
        sc.close();
    }
}