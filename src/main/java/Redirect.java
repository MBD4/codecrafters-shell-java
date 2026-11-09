public record Redirect(Target target, String file, boolean append) {
    public enum Target { NONE, STDOUT, STDERR }
    public static final Redirect NONE = new Redirect(Target.NONE, null, false);
}
