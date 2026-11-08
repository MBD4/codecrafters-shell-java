import java.util.List;

public record Input(String command, List<String> args, Redirect redirect) {
}