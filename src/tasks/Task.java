package tasks;

import java.util.Objects;
public class Task {
    protected String name;
    protected Status status;
    protected String description;
    protected int id;

    public Task(String name, String description) {
        this.name = name;
        this.status = Status.NEW;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static Task copyOf(Task original) {
        Task copy = new Task(original.getName(), original.getDescription());
        copy.setStatus(original.getStatus());
        copy.setId(original.getId());

        return copy;
    }

    @Override
    public String toString() {
        return "Название: " + name
                + ". ID: " + id
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
