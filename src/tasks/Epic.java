package tasks;

import managers.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Epic extends Task {
    private final List<Subtask> subtasks = new ArrayList<>();
    protected LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        this.type = TaskType.EPIC;
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void checkStatus() {
        if (!subtasks.isEmpty()) {
            int counterNew = 0;
            int counterDone = 0;

            for (Subtask subtask : subtasks) {
                switch (subtask.status) {
                    case NEW:
                        ++counterNew;
                        break;
                    case DONE:
                        ++counterDone;
                        break;
                }

                if (counterDone == subtasks.size()) {
                    this.status = Status.DONE;
                } else if (counterNew == subtasks.size()) {
                    this.status = Status.NEW;
                } else {
                    this.status = Status.IN_PROGRESS;
                }
            }

        } else {
            this.status = Status.NEW;
        }
    }

    public void checkTemporal() {
        List<Subtask> temporalSubtasks = subtasks.stream()
                .filter(subtask -> subtask.getStartTime().isPresent())
                .collect(Collectors.toList());

        if (!temporalSubtasks.isEmpty()) {
            Optional<LocalDateTime> startTime = temporalSubtasks.stream()
                    .map(subtask -> subtask.startTime)
                    .min(Comparator.naturalOrder());

            Optional<LocalDateTime> endTime = temporalSubtasks.stream()
                    .map(subtask -> subtask.getEndTime())
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .max(Comparator.naturalOrder());


            Long durationOfMinutes = temporalSubtasks.stream()
                    .map(subtask -> subtask.duration)
                    .map(Duration::toMinutes)
                    .reduce(0L, Long::sum);

            this.duration = Duration.ofMinutes(durationOfMinutes);

            if (startTime.isPresent() && endTime.isPresent()) {
                this.startTime = startTime.get();
                this.endTime = endTime.get();
            }
        }
    }

    @Override
    public void setTemporal(LocalDateTime startTime, Duration duration) {
        this.startTime = startTime;
        this.duration = duration;
        this.endTime = startTime.plus(duration);
    }

    @Override
    public Optional<LocalDateTime> getEndTime() {
        return Optional.ofNullable(endTime);
    }

    @Override
    public String toString() {
        return id
                + ",EPIC,"
                + name + ","
                + status + ","
                + description + ","
                + startTime + ","
                + duration + ",\n";
    }
}
