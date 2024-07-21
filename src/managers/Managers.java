package managers;

import history.HistoryManager;
import history.InMemoryHistoryManager;

import java.nio.file.Paths;

public class Managers {
    /*public static FileBackedTaskManager getDefault(String path) {
        return new FileBackedTaskManager(path);
    }*/
    public static FileBackedTaskManager getDefault(String path) {
        return FileBackedTaskManager.loadFromFile(Paths.get("file.txt").toFile());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}