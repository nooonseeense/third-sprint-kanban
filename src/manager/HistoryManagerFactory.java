package manager;

public class HistoryManagerFactory { // утилитарный класс, он будет нашим API между методами

    public static InMemoryTaskManager getHistoryManagerMemory() {
        return new InMemoryTaskManager();
    }
}
