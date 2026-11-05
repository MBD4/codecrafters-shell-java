import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        while (true) {
            System.out.print("$ ");
            Scanner scanner = new Scanner(System.in);
            String[] input = scanner.nextLine().trim().split(" ");
            String command = input[0];
            String[] inputArgs = Arrays.copyOfRange(input, 1, input.length);
            if (command.equals("exit")) {
                return;
            }
            else if (command.equals("echo")) {
                for (String s: inputArgs) {
                    System.out.print(s + " ");
                }
                System.out.println();
            }
            else {
                System.out.println(command + ": command not found");
            }
        }

    }
}
