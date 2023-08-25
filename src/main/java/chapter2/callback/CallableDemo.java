package chapter2.callback;

import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * @ClassNAME TaskWithResult
 * @Description 包含返回值的任务，必须使用ExecutorService.submit()方法调用它
 *              ，submit方法会产生Future对象，可以用isDone()方法查询future是否已完成，用get()方法获取执行结果
 * @Author yu
 * @Date 2023/8/23 16:54
 * @Version 1.0
 */
public class CallableDemo {
    public static void main(String[] args) {
        ExecutorService exec = Executors.newSingleThreadExecutor();

        ArrayList<Future<String>> results = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            results.add(exec.submit(new TaskWithResult(i)));
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

class TaskWithResult implements Callable<String> {
    private int id;

    public TaskWithResult(int id) {
        this.id = id;
    }

    @Override
    public String call() throws Exception {
        return "result of TaskResult " + id;
    }
}
