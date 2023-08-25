package chapter3;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * @ClassNAME ExerciseFourteen
 * @Description 21章 练习14 新建一个程序，他可以生成许多Timer对象，这些对象在定时时间到达后将执行某个简单的任务
 * @Author yu
 * @Date 2023/8/25 15:11
 * @Version 1.0
 */
public class ExerciseFourteen {
    public static void main(String[] args) {
        Random r = new Random();
        Timer t = new Timer();
        for (int i = 0; i < 20; i++) {
            // 随机生成每个任务在多少毫秒之后开始执行
            int sleepTime = r.nextInt(3500) + 1000;
            t.schedule(new MyTimerTask(i, UUID.randomUUID().toString()), sleepTime);
        }

        // 十秒后关闭timer，结束程序运行
        t.schedule(new exitTask(t), 10000);
    }
}

class MyTimerTask extends TimerTask {
    // 随机生成一个UUID并在任务开始执行时打印出生成的UUID，对应题目提到的完成某个简单的任务
    private String msg;
    // 每个定时任务的id编号
    private int id;

    public MyTimerTask(int id, String msg) {
        this.msg = msg;
        this.id = id;
    }

    @Override
    public void run() {
        System.out.println("定时任务-" + id + " 开始执行");
        System.out.println("定时任务-" + id + " 的msg = " + msg);
        System.out.println("定时任务-" + id + " 执行结束");
    }
}

// 定时任务，执行后调用Timer类的cancel()方法，结束定时任务
class exitTask extends TimerTask {
    private Timer t;

    public exitTask(Timer t) {
        this.t = t;
    }

    @Override
    public void run() {
        System.out.println("时间到了，结束所有定时任务------------");
        t.cancel();
    }
}
