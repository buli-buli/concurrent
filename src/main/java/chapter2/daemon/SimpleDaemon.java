package chapter2.daemon;

import java.util.concurrent.TimeUnit;

/**
 * @ClassNAME SimpleDaemon
 * @Description 后台线程示例，所谓后台线程，是指在程序运行的时候在后台提供一种通用服务的线程 ，并且这种线程并不属于程序中不可或缺的部分。因此，当所有非后台线程结束时，程序也就终止了，同时会杀死进程中的所有后台线程。
 * @Author yu
 * @Date 2023/8/23 22:21
 * @Version 1.0
 */
public class SimpleDaemon implements Runnable {

    @Override
    public void run() {
        try {
            while (true) {
                TimeUnit.MILLISECONDS.sleep(100);
                System.out.println(Thread.currentThread() + " " + this);
            }
        } catch (InterruptedException e) {
            System.out.println("sleep() interrupted");
        }
    }

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 10; i++) {
            Thread daemon = new Thread(new SimpleDaemon());
            daemon.setDaemon(true);
            daemon.start();
        }
        System.out.println("All daemon started");
        TimeUnit.MILLISECONDS.sleep(5000);
        System.out.println("main thread end");
    }
}
