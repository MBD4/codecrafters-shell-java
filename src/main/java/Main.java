import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {

        // gets the absolute PATH of the current directory the program runs in
        Path currentDirectory = Paths.get(System.getProperty("user.dir")).toAbsolutePath();

        while (true) {
            System.out.print("$ ");
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            if (line.isBlank()) {
                continue;
            }
            String[] inputArr = line.trim().split(" ");
            List<String> input = Arrays.stream(inputArr).toList();

            currentDirectory = Builtin.executeInput(input, currentDirectory);
        }

    }
}
