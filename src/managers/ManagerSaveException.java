package managers;

public class ManagerSaveException extends RuntimeException {
    public String message;

    public ManagerSaveException(String message) {
        this.message = message;
    }
}
