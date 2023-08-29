package chapter5.queue;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @ClassNAME PipedIO
 * @Description 两个任务使用一个管道进行通信
 * 
 *              管道基本上是一个阻塞队列，存在于多个引入BlockingQueue之前的Java版本中
 * @Author yu
 * @Date 2023/8/29 15:21
 * @Version 1.0
 */
public class PipedIO {
    public static void main(String[] args) throws Exception {
        Sender sender = new Sender();
        Receiver receiver = new Receiver(sender);
        ExecutorService exec = Executors.newCachedThreadPool();
        exec.execute(sender);
        exec.execute(receiver);

        TimeUnit.SECONDS.sleep(4);
        exec.shutdownNow();
    }
}

class Sender implements Runnable {
    private Random rand = new Random(47);
    private PipedWriter out = new PipedWriter();
    private PipedReader in = new PipedReader();

    public PipedWriter getPipedWriter() {
        return out;
    }

    @Override
    public void run() {
        try {
            while (true) {
                for (char c = 'A'; c <= 'z'; c++) {
                    out.write(c);
                    TimeUnit.MILLISECONDS.sleep(rand.nextInt(500));
                }
            }
        } catch (IOException e) {
            System.out.println(e + " Sender write exception");
        } catch (InterruptedException e) {
            System.out.println(e + " Sender sleep interrupted");
        }
    }
}

class Receiver implements Runnable {
    private PipedReader in;

    public Receiver(Sender sender) throws IOException {
        in = new PipedReader(sender.getPipedWriter());
    }

    @Override
    public void run() {
        try {
            while (true) {
                System.out.println("Read: " + (char)in.read() + ". ");
            }
        } catch (IOException e) {
            System.out.println(e + " Receiver read exception");
        }
    }
}
