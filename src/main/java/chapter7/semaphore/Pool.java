package chapter7.semaphore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * @ClassNAME Pool
 * @Description 泛型类，引用对象池的概念。管理数量有限的对象，当要使用对象时可以 签出它们，而在用户使用完毕时，可以将它们迁回
 * @Author yu
 * @Date 2023/8/30 14:54
 * @Version 1.0
 */
public class Pool<T> {
    private int size;
    private List<T> items = new ArrayList<>();
    private volatile boolean[] checkedOut;
    private Semaphore available;

    public Pool(Class<T> classObj, int size) {
        this.size = size;
        checkedOut = new boolean[size];
        available = new Semaphore(size, true);
        for (int i = 0; i < size; i++) {
            try {
                items.add(classObj.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public T checkOut() throws InterruptedException {
        available.acquire();
        return getItem();
    }

    public void checkIn(T x) {
        if (releaseItem(x)) {
            available.release();
        }
    }

    private synchronized T getItem() {
        for (int i = 0; i < size; ++i) {
            if (!checkedOut[i]) {
                checkedOut[i] = true;
                return items.get(i);
            }
        }

        return null;
    }

    private synchronized boolean releaseItem(T item) {
        int index = items.indexOf(item);
        if (index == -1) {
            return false;
        }
        if (checkedOut[index]) {
            checkedOut[index] = false;
            return true;
        }

        return false;
    }
}
