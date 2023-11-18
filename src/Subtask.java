public class Subtask extends Task {
    int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Название: " + name
                + ". ID: " + id
                + ". Описание: " + description
                + ". Статус: " + status
                + ". Связанный эпик: " + epicId + " ";
    }
}
