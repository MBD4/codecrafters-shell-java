import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;

public class Executer {

    /**
     * Runs an executable command with its arguments in the specified working directory.
     * The process standard error stream is merged with the standard output stream, and
     * all output produced by the process is printed to the standard output. This method
     * waits for the process to finish before returning. Any exception that occurs while
     * starting, reading from, or waiting for the process is caught and printed.
     *
     * @param input the command and its arguments, where the first element is the executable name or path
     * @param dir the working directory in which the executable process should run
     */
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
