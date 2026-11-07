import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class Builtin {
    private final static List<String> builtins = List.of("exit", "echo", "type", "pwd", "cd");

    public static Path executeInput(Input input, Path currentDirectory) {
        String command = input.getCommand();
        switch (command) {
            case "exit" -> executeExit();
            case "echo" -> executeEcho(input);
            case "type" -> executeType(input);
            case "pwd" -> executePwd(currentDirectory);
            case "cd" -> {
                return executeCd(input, currentDirectory);
            }
            default -> {
                if (Environment.locateExecutableFileInPATH(command) != null) {
                    executeExecutableFile(input, currentDirectory);
                }
                else {
                    System.out.println(command + ": command not found");
                }
            }
        }
        return currentDirectory;
    }

    private static void executeExit() {
        System.exit(0);
    }

    private static void executeEcho(Input input) {
        List<String> stringsToEcho = input.getArgs();
        for (String s: stringsToEcho) {
            System.out.print(s + " ");
        }
        System.out.println();
    }

    private static void executeType(Input input) {
        if (input.getArgs().isEmpty()) {
            return;
        }
        String fileToCheck = input.getArgs().getFirst();
        String executableFile = Environment.locateExecutableFileInPATH(fileToCheck);
        if (builtins.contains(fileToCheck)) {
            System.out.println(fileToCheck + " is a shell builtin");
        }
        else if (executableFile != null) {
            System.out.println(fileToCheck + " is " + executableFile);
        }
        else {
            System.out.println(fileToCheck + ": not found");
        }
    }

    private static void executePwd(Path currentDirectory) {
        System.out.println(currentDirectory);
    }

    private static Path executeCd(Input input, Path currentDirectory) {
        if (input.getArgs().isEmpty()) {
            return currentDirectory;
        }
        Path newDirectory = Parser.parseNewDirectory(input.getArgs().getFirst(), currentDirectory);
        if (Files.isDirectory(newDirectory)) {
            return newDirectory;
        } else {
            System.out.println("cd: " + newDirectory + ": No such file or directory");
            return currentDirectory;
        }
    }

    private static void executeExecutableFile(Input input, Path dir) {
        Executer.runExecutableFile(input, dir);
    }

    public List<String> getBuiltins() {
        return builtins;
    }
}
