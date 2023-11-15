public class Subtask extends Task {
    int epicId;

    public Subtask(String name, String description, Epic epic) {
        super(name, description);
        epicId = epic.id;

        epic.subtasks.add(this);
    }

    @Override
    public String toString() {
        return "Название: " + name
                + ". Описание: " + description
                + ". Статус: " + status + " ";
    }
}
