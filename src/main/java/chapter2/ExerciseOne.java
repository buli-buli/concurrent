package chapter2;

/**
 * @ClassNAME exerciseOne
 * @Description 21.2.2 练习 1 实现一个Runnale。在run()内部打印一个消息，然后调用yield()。 重复这个操作三次，然后从run()中返回在构造器中放一条启动消息，并且放置一条在任务终止时的关闭消息。
 *              使用线程创建大量的这种任务并驱动他们
 * @Author yu
 * @Date 2023/8/23 15:39
 * @Version 1.0
 */
class exerciseOne {
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Thread(new PrintTask()).start();
        }
    }
}

class PrintTask implements Runnable {
    protected int count = 3;
    private static int taskCount = 0;
    private final int id = taskCount++;

    public PrintTask() {
        System.out.println("#" + id + "-------------->启动");
    }

    @Override
    public void run() {
        while (count-- > 0) {
            System.out.println("#" + id + "(第" + (3 - count) + "次打印)");
        }
        System.out.println("#" + id + "-------------->结束");
    }
}
