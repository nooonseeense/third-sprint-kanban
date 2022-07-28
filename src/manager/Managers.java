package manager;

public class Managers { // Утилитарный класс

    public static HistoryManager getHistoryManagerMemory() {
        return new InMemoryHistoryManager();
    }
}
