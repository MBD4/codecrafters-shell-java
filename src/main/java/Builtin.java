import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class Builtin {
    private final static List<String> builtins = List.of("exit", "echo", "type", "pwd", "cd");

    public static Path executeInput(List<String> input, Path currentDirectory) {
        String command = input.getFirst();
        switch (command) {
            case "exit" -> executeExit();
            case "echo" -> executeEcho(input);
            case "type" -> executeType(input);
            case "pwd" -> executePwd(currentDirectory);
            case "cd" -> {
                return executeCd(input, currentDirectory);
            }
            default -> {
                if (Environment.locateExecutableFileInPATH(input.getFirst()) != null) {
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

    private static void executeEcho(List<String> input) {
        List<String> stringsToEcho = input.subList(1, input.size());
        for (String s: stringsToEcho) {
            System.out.print(s + " ");
        }
        System.out.println();
    }

    private static void executeType(List<String> input) {
        if (input.size() == 1) {
            return;
        }
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

    private static void executePwd(Path currentDirectory) {
        System.out.println(currentDirectory);
    }

    private static Path executeCd(List<String> input, Path currentDirectory) {
        if (input.size() == 1) {
            return currentDirectory;
        }
        Path newDirectory = parseNewDirectory(input.get(1), currentDirectory);
        if (Files.isDirectory(newDirectory)) {
            return newDirectory;
        } else {
            System.out.println("cd: " + newDirectory + ": No such file or directory");
            return currentDirectory;
        }
    }

    private static Path parseNewDirectory(String input, Path currentDirectory) {
        String[] inputSplit = input.split("");
        Path newDirectory = currentDirectory;
        StringBuilder buffer = new StringBuilder();
        for (String s : inputSplit) {
            buffer.append(s);
            if (!buffer.toString().equals(".") && !buffer.toString().equals("/") && !buffer.toString().equals("./") && !buffer.toString().equals("..") &&!buffer.toString().equals("../")) {
                newDirectory = Path.of(newDirectory.toString() + "/" + String.join("", inputSplit));
                break;
            }
            else if (buffer.toString().equals("/")) {
                newDirectory = Path.of(input);
                break;
            }
            else if (buffer.toString().equals("./")) {
                inputSplit = Arrays.stream(inputSplit).skip(2).toArray(String[]::new);
                buffer = new StringBuilder();
            }
            else if (buffer.toString().equals("../")) {
                Path parrentPath = newDirectory.getParent();
                if (parrentPath == null) {
                    System.out.println("cd: " + input + ": No such file or directory");
                    return currentDirectory;
                }
                inputSplit = Arrays.stream(inputSplit).skip(3).toArray(String[]::new);
                newDirectory = Path.of(parrentPath.toString());
                buffer = new StringBuilder();
            }
        }
        return newDirectory;
    }
//    private static Path doesDirectoryExist(Path oldDirectory, Path newDirectory) {
//        if (Files.isDirectory(newDirectory)) {
//            return newDirectory;
//        } else {
//            System.out.println("cd: " + newDirectory + ": No such file or directory");
//            return oldDirectory;
//        }
//    }

    private static void executeExecutableFile(List<String> input, Path dir) {
        Executer.runExecutableFile(input, dir);
    }

    public List<String> getBuiltins() {
        return builtins;
    }
}
