package chapter3;

/**
 * @ClassNAME SyncObject
 * @Description 在其他对象上同步 两个任务可以同时进入同一个对象，只要这个对象上的方法使在不同的锁上同步的即可
 * @Author yu
 * @Date 2023/8/25 17:10
 * @Version 1.0
 */
public class SyncObject {
    public static void main(String[] args) {
        final DualSynch ds = new DualSynch();
        new Thread() {
            public void run() {
                ds.f();
            }
        }.start();

        ds.g();
    }
}

class DualSynch {
    private Object syncObject = new Object();

    public synchronized void f() {
        for (int i = 0; i < 5; i++) {
            System.out.println("f()");
            Thread.yield();
        }
    }

    public void g() {
        synchronized (syncObject) {
            for (int i = 0; i < 5; i++) {
                System.out.println("g()");
                Thread.yield();
            }
        }
    }
}
