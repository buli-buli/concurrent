package chapter2.exception;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ClassNAME ExceptionThread
 * @Description 下面的任务总是会抛出一个异常，该异常会传播到其run()方法的外部 ，并且main()展示了当你运行它时所发生的事情
 * @Author yu
 * @Date 2023/8/24 13:40
 * @Version 1.0
 */
public class ExceptionThread implements Runnable {

    @Override
    public void run() {
        throw new RuntimeException();
    }

    public static void main(String[] args) {
        try {
            ExecutorService exec = Executors.newCachedThreadPool();
            exec.execute(new ExceptionThread());
        } catch (RuntimeException ue) {
            // 这段代码不会被执行
            System.out.println("Exception has been handled!");
        }
    }
}
