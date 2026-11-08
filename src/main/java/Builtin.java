import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class Builtin {
    private final static List<String> builtins = List.of("exit", "echo", "type", "pwd", "cd");

    public static Output executeInput(Input input, Path currentDirectory, boolean redirect) {
        String command = input.command();
        String outputString = "";
        switch (command) {
            case "exit" -> executeExit();
            case "echo" -> {
                outputString = executeEcho(input);
            }
            case "type" -> {
                outputString = executeType(input);
            }
            case "pwd" -> {
                outputString = executePwd(currentDirectory);
            }
            case "cd" -> {
                return executeCd(input, currentDirectory);
            }
            default -> {
                if (Environment.locateExecutableFileInPATH(command) != null) {
                    outputString = executeExecutableFile(input, currentDirectory, redirect);
                }
                else {
                    outputString = null;
                    System.out.println(command + ": command not found");
                }
            }
        }
        return new Output(currentDirectory, outputString);
    }

    private static void executeExit() {
        System.exit(0);
    }

    private static String executeEcho(Input input) {
        List<String> stringsToEcho = input.args();
        StringBuilder sb = new StringBuilder();
        for (String s: stringsToEcho) {
            sb.append(s).append(" ");
        }
        return sb.toString();
    }

    private static String executeType(Input input) {
        if (input.args().isEmpty()) {
            return null;
        }
        String fileToCheck = input.args().getFirst();
        String executableFile = Environment.locateExecutableFileInPATH(fileToCheck);
        StringBuilder sb = new StringBuilder();
        if (builtins.contains(fileToCheck)) {
            sb.append(fileToCheck).append(" is a shell builtin");
        }
        else if (executableFile != null) {
            sb.append(fileToCheck).append(" is ").append(executableFile);
        }
        else {
            System.out.println(fileToCheck + ": not found");
            return null;
        }
        return sb.toString();
    }

    private static String executePwd(Path currentDirectory) {
        return currentDirectory.toString();
    }

    private static Output executeCd(Input input, Path currentDirectory) {
        if (input.args().isEmpty()) {
            return new Output(currentDirectory, null);
        }
        Path newDirectory = Parser.parseNewDirectory(input.args().getFirst(), currentDirectory);
        if (Files.isDirectory(newDirectory)) {
            return new Output(newDirectory, null);
        } else {
            System.out.println("cd: " + newDirectory + ": No such file or directory");
            return new Output(currentDirectory, null);
        }
    }

    private static String executeExecutableFile(Input input, Path dir, boolean redirect) {
        return Executer.runExecutableFile(input, dir, redirect);
    }

    public static List<String> getBuiltins() {
        return builtins;
    }
}
