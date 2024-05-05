package managers;

public class TemporalException extends RuntimeException {
    public TemporalException(String message, Exception e) {
        super(message, e);
    }
}
