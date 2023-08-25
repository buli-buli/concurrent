package chapter2;

import java.util.concurrent.TimeUnit;

/**
 * @ClassNAME LiftOff
 * @Description 显示发射之前的倒计时
 * @Author yu
 * @Date 2023/8/23 15:21
 * @Version 1.0
 */
public class LiftOff implements Runnable {
    protected int countDown = 10;
    private static int taskCount = 0;
    private final int id = taskCount++;

    public LiftOff() {}

    public LiftOff(int countDown) {
        this.countDown = countDown;
    }

    public String status() {
        return "#" + id + "(" + (countDown > 0 ? countDown : "LiftOff!") + ").";
    }

    @Override
    public void run() {
        while (countDown-- > 0) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(status());
            Thread.yield();
        }
    }
}
