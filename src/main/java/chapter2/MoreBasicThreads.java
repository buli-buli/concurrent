package chapter2;

/**
 * @ClassNAME MoreBasicThreads
 * @Description 多个线程驱动多个任务
 * @Author yu
 * @Date 2023/8/23 15:34
 * @Version 1.0
 */
public class MoreBasicThreads {
    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            new Thread(new LiftOff()).start();
        }
        System.out.println("waiting for LiftOff");
    }
}
