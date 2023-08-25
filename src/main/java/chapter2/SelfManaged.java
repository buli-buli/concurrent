package chapter2;

/**
 * @ClassNAME SelfManaged
 * @Description 自管理的Runnable。 这与从Thread类继承并没有什么特别的差异，只是语法稍微晦涩一点。 但是，实现接口使得你可以继承另一个不同的类，而从Thread继承将无法做到
 * @Author yu
 * @Date 2023/8/23 23:45
 * @Version 1.0
 */
public class SelfManaged implements Runnable {
    private int countDown = 5;
    private Thread t = new Thread(this);

    public SelfManaged() {
        t.start();
    }

    @Override
    public String toString() {
        return Thread.currentThread().getName() + "(" + countDown + ")";
    }

    @Override
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
            new SelfManaged();
        }
    }
}
