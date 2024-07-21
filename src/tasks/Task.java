package tasks;

import managers.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class Task {
    protected int id;
    protected String name;
    protected Status status;
    protected String description;
    protected TaskType type = TaskType.TASK;
    protected LocalDateTime startTime;
    protected Duration duration;

    public Task(String name, String description) {
        this.name = name;
        this.status = Status.NEW;
        this.description = description;
    }

    public Task(Task another) {
        this.id = another.id;
        this.name = another.name;
        this.description = another.description;
        this.status = another.status;
    }

    public void setTemporal(LocalDateTime startTime, Duration duration) {
        this.startTime = startTime;
        this.duration = duration;
    }

    public Optional<LocalDateTime> getEndTime() {
        if (getStartTime().isPresent()) {
            return Optional.of(startTime.plus(duration));
        }
        return Optional.empty();
    }

    public Optional<LocalDateTime> getStartTime() {
        return Optional.ofNullable(startTime);
    }

    public Optional<Duration> getDuration() {
        return Optional.ofNullable(duration);
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

    public TaskType getType() {
        return type;
    }

    @Override
    public String toString() {
        return id
                + ",TASK,"
                + name + ","
                + status + ","
                + description + ","
                + startTime + ","
                + duration + "\n";
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
