package chapter9.comparison;

import java.util.ArrayList;
import java.util.List;

import util.CountingIntegerList;

/**
 * @ClassNAME ListComparisons
 * @Description SynchronizedArratList 和 CoptOnWriteArrayList对比示例
 * @Author yu
 * @Date 2023/9/4 14:34
 * @Version 1.0
 */
public class ListComparisons {
    public static void main(String[] args) {
        Tester.initMain(args);

        // new SynchronizedArrayListTest(10, 0);
        // new SynchronizedArrayListTest(9, 1);
        new SynchronizedArrayListTest(5, 5);

        // new CopyOnWriteArrayList(10, 0);
        // new CopyOnWriteArrayList(9, 1);
        new CopyOnWriteArrayList(5, 5);
        Tester.exec.shutdown();
    }
}

abstract class ListTest extends Tester<List<Integer>> {

    public ListTest(String testId, int nReaders, int nWriters) {
        super(testId, nReaders, nWriters);
    }

    class Reader extends TestTask {
        long result = 0;

        @Override
        void test() {
            for (long i = 0; i < testCycles; i++) {
                for (int index = 0; index < containerSize; index++) {
                    result += testContainer.get(index);
                }
            }
        }

        @Override
        void putResult() {
            readResult += result;
            readTime += duration;
        }
    }

    class Writer extends TestTask {

        @Override
        void test() {
            for (long i = 0; i < testCycles; i++) {
                for (int index = 0; index < containerSize; index++) {
                    testContainer.set(index, writeData[index]);
                }
            }
        }

        @Override
        void putResult() {
            writeTime += duration;
        }
    }

    @Override
    void startReaderAndWriters() {
        for (int i = 0; i < nReaders; i++) {
            exec.execute(new Reader());
        }

        for (int i = 0; i < nWriters; i++) {
            exec.execute(new Writer());
        }
    }
}

class SynchronizedArrayListTest extends ListTest {

    SynchronizedArrayListTest(int nReaders, int nWriters) {
        super("Synched ArrayList", nReaders, nWriters);
    }

    @Override
    List<Integer> containerInitializer() {
        return new ArrayList<>(new CountingIntegerList(containerSize));
    }
}

class CopyOnWriteArrayList extends ListTest {

    public CopyOnWriteArrayList(int nReaders, int nWriters) {
        super("CopyOnWriteArrayList", nReaders, nWriters);
    }

    @Override
    List<Integer> containerInitializer() {
        return new java.util.concurrent.CopyOnWriteArrayList<>(new CountingIntegerList(containerSize));
    }
}
