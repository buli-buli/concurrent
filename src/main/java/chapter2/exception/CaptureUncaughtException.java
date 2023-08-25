package chapter2.exception;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @ClassNAME CaptureUncaughtException
 * @Description Thread.UncaughtException-Handler是Java SE5中的新接口 ，它允许你在每个Thread对象上都附着一个异常处理器。
 *              Thread.UncaughtException-Handler.uncaughtException()会在线程因未捕获的异常
 *              而临近死亡时被调用。为了使用它，我们创建一个新类型的ThreadFactory，他将在每个新创建的
 *              Thread对象上附着一个Thread.UncaughtExceptionHandler。我们将这个工厂传递给Executors 创建新的ExecutorService的方法
 * @Author yu
 * @Date 2023/8/24 13:49
 * @Version 1.0
 */
public class CaptureUncaughtException {
    public static void main(String[] args) {
        ExecutorService exec = Executors.newCachedThreadPool(new HandlerThreadFactory());
        exec.execute(new ExceptionThread2());
        exec.shutdown();
    }
}

class ExceptionThread2 implements Runnable {

    @Override
    public void run() {
        Thread t = Thread.currentThread();
        System.out.println("run() by " + t);
        System.out.println("eh = " + t.getUncaughtExceptionHandler());
        throw new RuntimeException();
    }
}

class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.println("caught " + e);
    }
}

class HandlerThreadFactory implements ThreadFactory {
    @Override
    public Thread newThread(Runnable r) {
        System.out.println(this + " creating new Thread");
        Thread t = new Thread(r);
        System.out.println("created " + t);
        t.setUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
        System.out.println("eh = " + t.getUncaughtExceptionHandler());
        return t;
    }
}
