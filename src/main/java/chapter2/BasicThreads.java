package chapter2;

/**
 * @ClassNAME BasicThreads
 * @Description 用Thread构造器驱动LiftOff对象
 * @Author yu
 * @Date 2023/8/23 15:30
 * @Version 1.0
 */
public class BasicThreads {
    public static void main(String[] args) {
        Thread t = new Thread(new LiftOff());
        t.start();
        System.out.println("waiting for LiftOff");
    }
}
