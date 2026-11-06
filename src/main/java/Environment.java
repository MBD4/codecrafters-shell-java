import java.nio.file.Files;
import java.nio.file.Path;

public class Environment {
    /**
     Checks if file is in PATH and is executable
     @param file the file to check
     @return the executable files full path or null if the file doesn't exist in path or isn't executable
     */
    public static String locateExecutableFileInPATH(String file) {
        String PATH = System.getenv("PATH");
        if (PATH == null) {
            return null;
        }
        String[] PATHList = PATH.split(":");
        for (String s: PATHList) {
            if (Files.isExecutable(Path.of(s, file))) {
                return s + "/" + file;
            }
        }
        return null;
    }
}
