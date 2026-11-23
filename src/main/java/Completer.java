import java.io.IOException;
import java.io.PushbackInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class Completer {

    // TODO dont print from this method. the result should be return to main and printed from there
    public static boolean commandCompleter(StringBuilder buffer, PushbackInputStream in) throws IOException {
        List<String> externalExecutable;
        if (buffer.toString().equals("ech")) {
            buffer.append("o ");
            System.out.print("o ");
        }
        else if (buffer.toString().equals("exi")) {
            buffer.append("t ");
            System.out.print("t ");
        }
        else if ((externalExecutable = Environment.locateExecutableFileInPATHFromPrefix(buffer.toString())) != null) {
            if (externalExecutable.size() == 1) {
                String singleExternalExecutable = externalExecutable.getFirst();
                buffer.append(singleExternalExecutable).append(" ");
                System.out.print(singleExternalExecutable + " ");
                return false;
            }
            String currentBuffer = buffer.toString();
            int longestPrefix = buffer.toString().length();
            for (int i = 0; i < externalExecutable.getFirst().length(); i++) {
                if (i < currentBuffer.length()) {
                    continue;
                }
                int copy = i + 1;
                if (externalExecutable.stream().allMatch(s -> s.startsWith(externalExecutable.getFirst().substring(0, copy)))) {
                    longestPrefix = i + 1;
                }
                else {
                    break;
                }

            }
            if (longestPrefix > currentBuffer.length()) {
                String commonPrefix = externalExecutable.getFirst().substring(0, longestPrefix);
                String substring = commonPrefix.substring(buffer.toString().length());
                buffer.append(substring);
                System.out.print(substring);
                return false;
            }
            System.out.print("\u0007");
            int c = in.read();
            if (c == -1) {
                return true;
            }
            if (c == '\t') {
                System.out.println();
                for (String temp : externalExecutable) {
                    System.out.print(temp + "  ");
                }
                System.out.println();
                System.out.print("$ " + buffer);
            }
            else {
                in.unread(c);
            }

        }
        else {
            System.out.print("\u0007");
        }
        return false;
    }

    // TODO dont print from this method. the result should be return to main and printed from there
    public static String fileCompleter(String prefix, PushbackInputStream in, StringBuilder buffer, Path currentDirectory) {
        try {
            List<String> files;
            String directoryPath;
            String filePrefix;
            if (prefix.endsWith("/")) {
                directoryPath = prefix;
                filePrefix = "";
            }
            else if (prefix.contains("/")) {
                List<String> prefixSplit = new java.util.ArrayList<>(List.of(prefix.split("/")));
                if (prefixSplit.size() < 2) {
                    return null;
                }
                filePrefix = prefixSplit.removeLast();
                directoryPath = String.join("/", prefixSplit);
            }
            else {
                directoryPath = "";
                filePrefix = prefix;
            }
            try (Stream<Path> paths = Files.list(currentDirectory.resolve(directoryPath))) {
                files = paths
                        .map(p -> {
                            if (Files.isDirectory(p)) {
                                return p.getFileName().toString() + "/";
                            }
                            return p.getFileName().toString();
                        })
                        .filter(string -> string.startsWith(filePrefix))
                        .sorted()
                        .toList();
            }
            if (files.size() < 2) {
                Optional<String> file = files.stream().findFirst();
                if (file.isPresent()) {
                    if (file.orElseThrow().endsWith("/")) {
                        return file.orElseThrow().substring(filePrefix.length());
                    }
                    return file.orElseThrow().substring(filePrefix.length()) + " ";
                }
            }
            else {
                String firstItem = files.getFirst();
                int longestPrefix = filePrefix.length();
                for (int i = 0; i < firstItem.length(); i++) {
                    if (i < filePrefix.length()) {
                        continue;
                    }
                    int copy = i + 1;
                    if (files.stream().allMatch(s -> s.startsWith(firstItem.substring(0, copy)))) {
                        longestPrefix = i + 1;
                    }
                    else {
                        break;
                    }

                }
                if (longestPrefix > filePrefix.length()) {
                    String commonPrefix = firstItem.substring(0, longestPrefix);
                    return commonPrefix.substring(filePrefix.length());
                }
                System.out.print("\u0007");
                int c = in.read();
                if (c == -1) {
                    return "";
                }
                if (c == '\t') {
                    System.out.println();
                    for (String temp : files) {
                        System.out.print(temp + "  ");
                    }
                    System.out.println();
                    System.out.print("$ " + buffer);
                }
                else {
                    in.unread(c);
                }
            }
        }

        catch (IOException e) {
            return null;
        }
        return null;
    }
}
