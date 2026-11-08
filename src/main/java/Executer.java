import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Executer {

    /**
     * Runs the executable command described by the given input in the specified working directory,
     * optionally capturing either standard output or standard error according to the redirect value.
     * Streams that are not captured are inherited by the current process. The method waits for the
     * launched process to complete before returning.
     *
     * @param input the command and arguments used to start the executable
     * @param currentDirectory the working directory in which the executable is run
     * @param redirect determines whether standard output, standard error, or neither stream is captured
     * @return an output record containing the working directory and any captured standard output or standard error
     */
    public static Output runExecutableFile(Input input, Path currentDirectory, Redirect redirect) {
        try {
            List<String> pbArgs = new ArrayList<>(input.args());
            pbArgs.addFirst(input.command());
            ProcessBuilder pb = new ProcessBuilder(pbArgs);
            pb.directory(currentDirectory.toFile());
            if (redirect != Redirect.STDERR) {
                pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            }
            if (redirect != Redirect.STDOUT) {
                pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            }
            try (Process process = pb.start()) {

                String stdOut = null;
                String stdErr = null;
                String rString = null;
                if (redirect == Redirect.STDOUT) {
                    BufferedReader readerStd = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;

                    StringBuilder sb = new StringBuilder();
                    while ((line = readerStd.readLine()) != null) {
                        sb.append(line).append(System.lineSeparator());
                    }
                    stdOut = sb.toString();

                    process.waitFor();

                    return new Output(currentDirectory, stdOut, stdErr);
                }
                else if (redirect == Redirect.STDERR) {
                    BufferedReader readerStd = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    String line;

                    StringBuilder sb = new StringBuilder();
                    while ((line = readerStd.readLine()) != null) {
                        sb.append(line).append(System.lineSeparator());
                    }
                    stdErr = sb.toString();

                    process.waitFor();

                    return new Output(currentDirectory, stdOut, stdErr);
                }
                else {
                    process.waitFor();
                    return new Output(currentDirectory, stdOut, stdErr);
                }

            }

        } catch (Exception e) {
            throw new RuntimeException("this line in Executer should never be reached");
        }
    }
}
