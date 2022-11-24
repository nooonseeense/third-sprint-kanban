package manager;

import org.junit.jupiter.api.Test;

public class InMemoryHistoryManagerTest extends HistoryManagerTest<InMemoryHistoryManager> {

    public InMemoryHistoryManagerTest() {
        super(new InMemoryHistoryManager());
    }

    @Override
    @Test
    public void addTaskAndRemoveInHistoryTest() {
        super.addTaskAndRemoveInHistoryTest();
    }

    @Override
    @Test
    public void getHistoryTest() {
        super.getHistoryTest();
    }
}