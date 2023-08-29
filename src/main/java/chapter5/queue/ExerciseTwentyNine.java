package chapter5.queue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @ClassNAME ExerciseTwentyNight
 * @Description 修改ToastOmatic，使用两个单独地组装线来创建涂有花生黄油和果冻的吐司三明治 （一个用于花生黄油，第二个用于果冻，然后把两条线合并）
 * @Author yu
 * @Date 2023/8/29 14:41
 * @Version 1.0
 */
public class ExerciseTwentyNine {
    public static void main(String[] args) throws Exception {
        ToastQueue29 dryQueue = new ToastQueue29(), jelliedQueue = new ToastQueue29(),
            peanutButteredQueue = new ToastQueue29();
        SandwichQueue sandwichQueue = new SandwichQueue();

        ExecutorService exec = Executors.newCachedThreadPool();
        exec.execute(new Toaster29(dryQueue));
        exec.execute(new Jellyer(dryQueue, jelliedQueue));
        exec.execute(new PeanutButterer(dryQueue, peanutButteredQueue));
        exec.execute(new SandWichMaker(jelliedQueue, peanutButteredQueue, sandwichQueue));
        exec.execute(new SandwichEater(sandwichQueue));

        TimeUnit.SECONDS.sleep(5);

        exec.shutdownNow();
    }
}

class ToastQueue29 extends LinkedBlockingQueue<Toast29> {}

class PeanutButterer implements Runnable {
    private ToastQueue29 dryQueue, peanutButterQueue;

    public PeanutButterer(ToastQueue29 dryQueue, ToastQueue29 peanutButterQueue) {
        this.dryQueue = dryQueue;
        this.peanutButterQueue = peanutButterQueue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                Toast29 t = dryQueue.take();
                t.peanutButter();
                System.out.println(t);
                peanutButterQueue.put(t);
            }
        } catch (InterruptedException e) {
            System.out.println("PeanutButterer interrupted");
        }

        System.out.println("PeanutButterer off");
    }
}

class Jellyer implements Runnable {
    private ToastQueue29 dryQueue, jelliedQueue;

    public Jellyer(ToastQueue29 dryQueue, ToastQueue29 jelliedQueue) {
        this.dryQueue = dryQueue;
        this.jelliedQueue = jelliedQueue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                Toast29 t = dryQueue.take();
                t.jelly();
                System.out.println(t);
                jelliedQueue.put(t);
            }
        } catch (InterruptedException e) {
            System.out.println("Jellyer interrupted");
        }

        System.out.println("Jellyer off");
    }
}

class Toast29 {
    public enum Status {
        DRY, JELLIED, PEANUTBUTTERED
    }

    private Status status = Status.DRY;
    private final int id;

    public Toast29(int id) {
        this.id = id;
    }

    public void peanutButter() {
        status = Status.PEANUTBUTTERED;
    }

    public void jelly() {
        status = Status.JELLIED;
    }

    public Status getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Toast{" + "status=" + status + ", id=" + id + '}';
    }
}

class SandWich {
    private Toast29 top, bottom;
    private final int id;

    public SandWich(Toast29 top, Toast29 bottom, int id) {
        this.top = top;
        this.bottom = bottom;
        this.id = id;
    }

    public SandWich(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Toast29 getTop() {
        return top;
    }

    public Toast29 getBottom() {
        return bottom;
    }

    @Override
    public String toString() {
        return "SandWich{" + "top=" + top + ", bottom=" + bottom + ", id=" + id + '}';
    }
}

class SandwichQueue extends LinkedBlockingQueue<SandWich> {}

class SandWichMaker implements Runnable {
    private int count = 0;
    private ToastQueue29 jelliedQueue, peanutButteredQueue;
    private SandwichQueue sandwichQueue;

    public SandWichMaker(ToastQueue29 jelliedQueue, ToastQueue29 peanutButteredQueue, SandwichQueue sandwichQueue) {
        this.jelliedQueue = jelliedQueue;
        this.peanutButteredQueue = peanutButteredQueue;
        this.sandwichQueue = sandwichQueue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                SandWich s = new SandWich(jelliedQueue.take(), peanutButteredQueue.take(), count++);
                System.out.println(s);
                sandwichQueue.put(s);
            }
        } catch (InterruptedException e) {
            System.out.println("SandWichMaker interrupted");
        }

        System.out.println("SandWichMaker off");
    }
}

class SandwichEater implements Runnable {
    private SandwichQueue sandwichQueue;
    private int counter = 0;

    public SandwichEater(SandwichQueue sandwichQueue) {
        this.sandwichQueue = sandwichQueue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                SandWich s = sandwichQueue.take();
                if (s.getId() != counter++ || s.getTop().getStatus() != Toast29.Status.JELLIED
                    || s.getBottom().getStatus() != Toast29.Status.PEANUTBUTTERED) {
                    System.out.println(">>>> Error: " + s);
                    System.exit(1);
                } else {
                    System.out.println("NumNum! " + s);
                }
            }
        } catch (InterruptedException e) {
            System.out.println("SandwichEater interrupted");
        }

        System.out.println("SandwichEater off");
    }
}

class Toaster29 implements Runnable {
    private ToastQueue29 toastQueue;
    private int count = 0;

    public Toaster29(ToastQueue29 toastQueue) {
        this.toastQueue = toastQueue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                TimeUnit.MILLISECONDS.sleep(500);
                Toast29 t = new Toast29(count++);
                System.out.println(t);
                toastQueue.put(t);
            }
        } catch (InterruptedException e) {
            System.out.println("Toaster interrupted");
        }

        System.out.println("Toast off");
    }
}