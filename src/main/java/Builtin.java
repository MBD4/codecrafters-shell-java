import java.nio.file.Path;
import java.util.List;

public class Builtin {
    private final static List<String> builtins = List.of("exit", "echo", "type");

    public static void executeInput(List<String> input, Path currentDirectory) {
        String command = input.getFirst();
        switch (command) {
            case "exit" -> executeExit();
            case "echo" -> executeEcho(input);
            case "type" -> executeType(input);
            default -> {
                if (Environment.locateExecutableFileInPATH(input.getFirst()) != null) {
                    executeExecutableFile(input, currentDirectory);
                }
                else {
                    System.out.println(command + ": command not found");
                }
            }
        }
    }

    public static void executeExit() {
        System.exit(0);
    }

    public static void executeEcho(List<String> input) {
        List<String> stringsToEcho = input.subList(1, input.size());
        for (String s: stringsToEcho) {
            System.out.print(s + " ");
        }
        System.out.println();
    }

    public static void executeType(List<String> input) {
        String fileToCheck = input.get(1);
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

    public static void executeExecutableFile(List<String> input, Path dir) {
        Executer.runExecutableFile(input, dir);
    }

    public List<String> getBuiltins() {
        return builtins;
    }
}
