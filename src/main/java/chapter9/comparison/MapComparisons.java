package chapter9.comparison;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import util.CountingGenerator;
import util.MapData;

/**
 * @ClassNAME MapComparisons
 * @Description synchronizedHashMap 和 ConcurrentHshMap在性能方面的比较结果示例
 * @Author yu
 * @Date 2023/9/4 16:00
 * @Version 1.0
 */
public class MapComparisons {
    public static void main(String[] args) {
        Tester.initMain(args);
        new SynchronizedHashMapTest(10, 0);
        new SynchronizedHashMapTest(9, 1);
        new SynchronizedHashMapTest(5, 5);

        new ConcurrentHashMapTest(10, 0);
        new ConcurrentHashMapTest(9, 1);
        new ConcurrentHashMapTest(5, 5);

        Tester.exec.shutdown();
    }
}

abstract class MapTest extends Tester<Map<Integer, Integer>> {

    public MapTest(String testId, int nReaders, int nWriters) {
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
                    testContainer.put(index, writeData[index]);
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

class SynchronizedHashMapTest extends MapTest {

    public SynchronizedHashMapTest(int nReaders, int nWriters) {
        super("Synched HashMap", nReaders, nWriters);
    }

    @Override
    Map<Integer, Integer> containerInitializer() {
        return Collections.synchronizedMap(new HashMap<>(
            MapData.map(new CountingGenerator.Integer(), new CountingGenerator.Integer(), containerSize)));
    }
}

class ConcurrentHashMapTest extends MapTest {

    public ConcurrentHashMapTest(int nReaders, int nWriters) {
        super("Concurrent HashMap", nReaders, nWriters);
    }

    @Override
    Map<Integer, Integer> containerInitializer() {
        return new ConcurrentHashMap<>(
            MapData.map(new CountingGenerator.Integer(), new CountingGenerator.Integer(), containerSize));
    }
}
