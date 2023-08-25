package chapter2;

import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @ClassNAME ExcerciseTwo
 * @Description 21.2.2 练习 2
 * @Author yu
 * @Date 2023/8/23 16:14
 * @Version 1.0
 */
public class ExcerciseTwo {
    public static void main(String[] args) {
        for (int i = 0; i < 20; i++) {
            new Thread(new FibonacciTask()).start();
        }
    }
}

class FibonacciTask implements Runnable {
    private int count = 0;
    private static int taskCount = 0;
    private final int id = taskCount++;

    public Integer get() {
        return fib(count++);
    }

    private int fib(int n) {
        if (n < 2)
            return 1;
        return fib(n - 2) + fib(n - 1);
    }

    @Override
    public void run() {
        System.out.println("#" + id + "开始生成斐波那契数序列-------------->");
        Stream.generate(new Fibonacci()).limit(18).map(n -> "#" + id + ": " + n + " ").forEach(System.out::print);
    }
}

class Fibonacci implements Supplier<Integer> {
    private int count = 0;

    @Override
    public Integer get() {
        return fib(count++);
    }

    private int fib(int n) {
        if (n < 2)
            return 1;
        return fib(n - 2) + fib(n - 1);
    }

    public static void main(String[] args) {
        Stream.generate(new Fibonacci()).limit(18).map(n -> n + " ").forEach(System.out::print);
    }
}
/* Output:
1 1 2 3 5 8 13 21 34 55 89 144 233 377 610 987 1597
2584
*/
