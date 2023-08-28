package chapter4.interruprt;

import java.util.concurrent.TimeUnit;

/**
 * @ClassNAME InterrruptingIdiom
 * @Description 通过在阻塞调用上抛出异常来退出。 如果interrupt()在注释point2之后（即在非阻塞的操作过程中）被调用，那么首先循环将结束
 *              ，然后所有的本地对象将被销毁，最后循环会经由while语句的顶部退出。但是，如果interrupt()在point1和point2
 *              之间（在while语句之后，但是在阻塞操作sleep()之前或其过程中）被调用，那么这个任务就会在第一次试图调用阻塞操作 之前经由InterruptedException退出。
 * 
 *              args[0] 设置为1001时从while语句顶部退出，设置为1500时从InterruptedException退出。
 * @Author yu
 * @Date 2023/8/28 14:26
 * @Version 1.0
 */
public class InterrruptingIdiom {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("usage: java InterruptingIdiom delay-in-ms");
            System.exit(1);
        }
        Thread t = new Thread(new Blocked3());
        t.start();
        TimeUnit.MILLISECONDS.sleep(new Integer(args[0]));
        t.interrupt();
    }
}

class NeedsCleanup {
    private final int id;

    public NeedsCleanup(int id) {
        this.id = id;
        System.out.println("NeedsCleanup " + id);
    }

    public void cleanup() {
        System.out.println("Cleaning up " + id);
    }
}

class Blocked3 implements Runnable {
    private volatile double d = 0.0;

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                // point1
                NeedsCleanup n1 = new NeedsCleanup(1);
                try {
                    System.out.println("Sleeping");
                    TimeUnit.SECONDS.sleep(1);

                    // point2
                    NeedsCleanup n2 = new NeedsCleanup(2);
                    try {
                        System.out.println("Calculating");
                        for (int i = 0; i < 2500000; i++) {
                            d = d + (Math.PI + Math.E) / d;
                        }
                        System.out.println("Finished time-consuming operation");
                    } finally {
                        n2.cleanup();
                    }
                } finally {
                    n1.cleanup();
                }
            }
            System.out.println("Exiting via while() test");
        } catch (InterruptedException e) {
            System.out.println("Exiting via InterruptedException");
        }
    }
}
