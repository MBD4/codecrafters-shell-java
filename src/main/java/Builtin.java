import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class Builtin {
    private final static List<String> builtins = List.of("exit", "echo", "type", "pwd", "cd");

    public static Output executeInput(Input input, Path currentDirectory, Redirect redirect) {
        String command = input.command();
        String outputString = "";
        new Output(currentDirectory, null, null);
        switch (command) {
            case "exit" -> executeExit();
            case "echo" -> {
                return executeEcho(input, currentDirectory);
            }
            case "type" -> {
                return executeType(input, currentDirectory);
            }
            case "pwd" -> {
                return executePwd(currentDirectory);
            }
            case "cd" -> {
                return executeCd(input, currentDirectory);
            }
            default -> {
                if (Environment.locateExecutableFileInPATH(command) != null) {
                    return executeExecutableFile(input, currentDirectory, redirect);
                }
                else {
                    return new Output(currentDirectory, null, command + ": command not found");
                }
            }
        }
        throw new RuntimeException("this line in Builtin.executeInput should never be reached");
    }

    private static void executeExit() {
        System.exit(0);
    }

    private static Output executeEcho(Input input, Path currentDirectory) {
        List<String> stringsToEcho = input.args();
        StringBuilder sb = new StringBuilder();
        for (String s: stringsToEcho) {
            sb.append(s).append(" ");
        }
        return new Output(currentDirectory, sb.toString(), null);
    }

    private static Output executeType(Input input, Path currentDirectory) {
        if (input.args().isEmpty()) {
            return null;
        }
        String fileToCheck = input.args().getFirst();
        String executableFile = Environment.locateExecutableFileInPATH(fileToCheck);
        StringBuilder sb = new StringBuilder();
        String stdOut = null;
        String stdErr = null;
        if (builtins.contains(fileToCheck)) {
            stdOut = fileToCheck + " is a shell builtin";
        }
        else if (executableFile != null) {
            stdOut = fileToCheck + " is " + executableFile;
        }
        else {
            stdErr = fileToCheck + ": not found";
        }
        return new Output(currentDirectory, stdOut, stdErr);
    }

    private static Output executePwd(Path currentDirectory) {
        return new Output(currentDirectory, currentDirectory.toString(), null);
    }

    private static Output executeCd(Input input, Path currentDirectory) {
        if (input.args().isEmpty()) {
            return new Output(currentDirectory, null, null);
        }
        Path newDirectory = Parser.parseNewDirectory(input.args().getFirst(), currentDirectory);
        if (Files.isDirectory(newDirectory)) {
            return new Output(newDirectory, null, null);
        } else {
            return new Output(currentDirectory, null, "cd: " + newDirectory + ": No such file or directory");
        }
    }

    private static Output executeExecutableFile(Input input, Path currentDirectory, Redirect redirect) {
        return Executer.runExecutableFile(input, currentDirectory, redirect);
    }

    public static List<String> getBuiltins() {
        return builtins;
    }
}
