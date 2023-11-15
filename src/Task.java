import java.util.Objects;
public class Task {
    protected String name;
    protected String status;
    protected String description;
    protected int id;

    public Task(String name, String description) {
        this.name = name;
        this.status = "NEW";
        this.description = description;
    }


    @Override
    public String toString() {
        return "Название: " + name
                + ". Описание: " + description
                + ". Статус: " + status;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;

        Task otherTask = (Task) obj;
        return name.equals(otherTask.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
