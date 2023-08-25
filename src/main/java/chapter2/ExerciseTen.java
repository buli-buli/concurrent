package chapter2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class ExerciseTen {
    private Fibonacci f;
    private Callable<String> c;
    private String name;
    private int n;

    public ExerciseTen(String name, int n) {
        this.name = name;
        this.n = n;
        f = new Fibonacci();
    }

    public void runTask(ExecutorService exec, List<Future> res) {
        c = new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println("#" + name + "开始生成斐波那契数序列-------------->");
                Integer sum = 0;
                for (int i = 0; i < n; i++) {
                    int x = f.get();
                    sum += x;
                    System.out.print("#" + name + " " + x);
                }
                System.out.println("------------------------");
                System.out.println("#" + name + "结束-------------->");
                return "#" + name + ": " + sum;
            }
        };

        res.add(exec.submit(c));
    }

    public static void main(String[] args) {
        ExecutorService exec = Executors.newCachedThreadPool();
        Random r = new Random();
        List<Future> res = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            try {
                new ExerciseTen("f_" + i, r.nextInt(3) + 2).runTask(exec, res);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        exec.shutdown();

        for (Future f : res) {
            try {
                System.out.println("f: " + f.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}