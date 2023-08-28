package chapter4.interruprt;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @ClassNAME NIOInterruption
 * @Description NIO类提供的更人性化的中断。被阻塞的NIO通道会自动地响应中断
 * @Author yu
 * @Date 2023/8/26 22:53
 * @Version 1.0
 */
public class NIOInterruption {
    public static void main(String[] args) throws Exception {
        ExecutorService exec = Executors.newCachedThreadPool();

        ServerSocket server = new ServerSocket(8080);
        InetSocketAddress isa = new InetSocketAddress("localhost", 8080);
        SocketChannel sc1 = SocketChannel.open(isa);
        SocketChannel sc2 = SocketChannel.open(isa);

        Future<?> f = exec.submit(new BIOBlocked(sc1));
        exec.execute(new BIOBlocked(sc2));
        exec.shutdown();
        TimeUnit.SECONDS.sleep(1);
        f.cancel(true);
        TimeUnit.SECONDS.sleep(1);
        sc2.close();
        server.close();
    }
}

class BIOBlocked implements Runnable {
    private final SocketChannel sc;

    public BIOBlocked(SocketChannel sc) {
        this.sc = sc;
    }

    @Override
    public void run() {
        try {
            System.out.println("Waiting for read() in " + this);
            sc.read(ByteBuffer.allocate(1));
        } catch (ClosedByInterruptException e) {
            System.out.println("ClosedByInterruptException");
        } catch (AsynchronousCloseException e) {
            System.out.println("AsynchronousCloseException");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Exiting NIOBlocked.run() " + this);
    }
}
