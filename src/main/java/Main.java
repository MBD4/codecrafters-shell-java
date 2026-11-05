import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        while (true) {
            System.out.print("$ ");
            Scanner scanner = new Scanner(System.in);
            String command = scanner.nextLine().trim();
            if (command.equals("exit")) {
                return;
            }
            System.out.println(command + ": command not found");
        }

    }
}
