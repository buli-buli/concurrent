package chapter2.daemon;

/**
 * @ClassNAME SimpleThread
 * @Description 使用直接从Thread类继承的方式新建线程
 * @Author yu
 * @Date 2023/8/23 23:33
 * @Version 1.0
 */
public class SimpleThread extends Thread {
    private int countDown = 5;
    private static int threadCount = 0;

    public SimpleThread() {
        super(Integer.toString(++threadCount));
        this.setName("线程_" + threadCount);
        start();
    }

    @Override
    public String toString() {
        return "#" + getName() + "(" + countDown + ")";
    }

    public void run() {
        while (true) {
            System.out.println(this);
            if (--countDown == 0) {
                return;
            }
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            new SimpleThread();
        }
    }
}
