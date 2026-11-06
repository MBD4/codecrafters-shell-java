import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;

public class Executer {
    public static void runExecutableFile(List<String> input, Path dir) {
        try {
            ProcessBuilder pb = new ProcessBuilder(input);
            pb.directory(dir.toFile());
            pb.redirectErrorStream(true);
            try (Process process = pb.start()) {

                // Read the program's output
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }

                // Wait for the program to finish and get the exit code
                int exitCode = process.waitFor();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
