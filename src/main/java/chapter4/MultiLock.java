package chapter4;

/**
 * @ClassNAME MultiLock
 * @Description 同一个互斥能被一个任务多次获得
 * @Author yu
 * @Date 2023/8/27 20:27
 * @Version 1.0
 */
public class MultiLock {
    public synchronized void f1(int count) {
        if (count-- > 0) {
            System.out.println("f1() calling f2() with count " + count);
            f2(count);
        }
    }

    private synchronized void f2(int count) {
        if (count-- > 0) {
            System.out.println("f2() calling f1() with count " + count);
            f1(count);
        }
    }

    public static void main(String[] args) {
        final MultiLock multiLock = new MultiLock();
        new Thread() {
            public void run() {
                multiLock.f1(10);
            }
        }.start();
    }
}
