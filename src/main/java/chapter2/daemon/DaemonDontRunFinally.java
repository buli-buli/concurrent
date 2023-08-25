package chapter2.daemon;

import java.util.concurrent.TimeUnit;

/**
 * @ClassNAME ADaemon
 * @Description 后台进程在不执行finally子句的情况下就会中止其run()方法
 * @Author yu
 * @Date 2023/8/23 23:02
 * @Version 1.0
 */
public class DaemonDontRunFinally {
    public static void main(String[] args) throws Exception {
        Thread t = new Thread(new ADaemon());
        // t.setDaemon(true);
        t.start();
    }
}

class ADaemon implements Runnable {

    @Override
    public void run() {
        try {
            System.out.println("starting ADaemon");
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            System.out.println("exiting via interruprtion");
        } finally {
            System.out.println("this should always run ?");
        }
    }
}
