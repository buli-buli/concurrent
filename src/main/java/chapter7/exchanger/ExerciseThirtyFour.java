package chapter7.exchanger;

import java.util.List;
import java.util.concurrent.*;

import util.BasicGenerator;

/**
 * @ClassNAME ExerciseThirtyFour
 * @Description 修改ExchangerDemo，让其使用自定义的类而不是Fat
 * @Author yu
 * @Date 2023/8/30 16:15
 * @Version 1.0
 */
public class ExerciseThirtyFour {
    static int delay = 3;

    public static void main(String[] args) throws Exception {
        ExecutorService exec = Executors.newCachedThreadPool();
        Exchanger<List<MyFat>> xc = new Exchanger<>();
        List<MyFat> producerList = new CopyOnWriteArrayList<>();
        List<MyFat> consumerList = new CopyOnWriteArrayList<>();
        exec.execute(new ExchangerProducer<>(xc, BasicGenerator.create(MyFat.class), producerList));
        exec.execute(new ExchangerConsumer<>(xc, consumerList));

        TimeUnit.SECONDS.sleep(delay);
        exec.shutdownNow();
    }
}