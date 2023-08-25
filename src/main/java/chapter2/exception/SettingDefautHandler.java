package chapter2.exception;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ClassNAME SettingDefautHandler
 * @Description 在Thread类中设置一个静态域，并将这个处理器设置为默认的未捕获异常处理器
 * @Author yu
 * @Date 2023/8/24 14:08
 * @Version 1.0
 */
public class SettingDefautHandler {
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
        ExecutorService exec = Executors.newCachedThreadPool();
        exec.execute(new ExceptionThread());
        exec.shutdown();
    }
}
