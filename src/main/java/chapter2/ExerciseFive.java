package chapter2;

import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * @ClassNAME ExerciseFive
 * @Description 生成斐波那契数序列后返回总和
 * @Author yu
 * @Date 2023/8/23 17:08
 * @Version 1.0
 */
public class ExerciseFive {
    public static void main(String[] args) {
        ExecutorService exec = Executors.newSingleThreadExecutor();

        ArrayList<Future<String>> results = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            results.add(exec.submit(new FibonacciTaskWithSum()));
        }

        for (Future<String> fs : results) {
            try {
                System.out.println(fs.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            } catch (ExecutionException e) {
                e.printStackTrace();
            } finally {
                exec.shutdown();
            }
        }
    }
}

class FibonacciTaskWithSum implements Callable<String> {
    private int count = 0;
    private static int taskCount = 0;
    private final int id = taskCount++;
    private Fibonacci f;

    public FibonacciTaskWithSum() {
        f = new Fibonacci();
    }

    public Integer get() {
        return fib(count++);
    }

    private int fib(int n) {
        if (n < 2)
            return 1;
        return fib(n - 2) + fib(n - 1);
    }

    @Override
    public String call() throws Exception {
        System.out.println("#" + id + "开始生成斐波那契数序列-------------->");

        Integer sum = 0;
        for (int i = 0; i < 18; i++) {
            int x = f.get();
            sum += x;
            System.out.print("#" + id + " " + x);
        }

        System.out.println("#" + id + "结束-------------->");
        return "#" + id + ": " + sum;
    }
}
