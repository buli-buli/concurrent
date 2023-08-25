package chapter2.ui;

/**
 * @ClassNAME ResponsiveUI
 * @Description 有响应的用户界面
 * @Author yu
 * @Date 2023/8/24 13:25
 * @Version 1.0
 */
class UnresponsiveUI {
    private volatile double d = 1;

    public UnresponsiveUI() throws Exception {
        // while循环的中止条件永远不可能达到，永远不会执行读取控制台输入的那一行代码
        while (d > 0) {
            d = d + (Math.PI + Math.E) / d;
        }
        System.in.read();// 永远不会执行
    }
}

public class ResponsiveUI extends Thread {
    private static volatile double d = 1;

    public ResponsiveUI() {
        setDaemon(true);
        start();
    }

    public void run() {
        while (true) {
            d = d + (Math.PI + Math.E) / d;
        }
    }

    public static void main(String[] args) throws Exception {
        // new UnresponsiveUI();
        new ResponsiveUI();
        System.in.read();
        System.out.println(d);
    }
}
