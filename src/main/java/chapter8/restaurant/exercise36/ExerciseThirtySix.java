package chapter8.restaurant.exercise36;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

import chapter8.restaurant.enums.Course;
import chapter8.restaurant.enums.Food;

/**
 * @ClassNAME RestaurantWithQueues
 * @Description 练习 36 修改 RestaurantWithQueues.java，使得每个桌子都有一个OrderTicket
 *              对象。将Order修改为OrderTickets，并添加一个Table类，每个桌子上可以有多个Customer。
 * @Author yu
 * @Date 2023/8/30 17:52
 * @Version 1.0
 */
public class ExerciseThirtySix {
    public static void main(String[] args) throws Exception {
        ExecutorService exec = Executors.newCachedThreadPool();
        Restaurant restaurant = new Restaurant(exec, 5, 2);

        exec.execute(restaurant);

        System.out.println("Press 'Enter' to quit");
        System.in.read();
        exec.shutdownNow();
    }
}

class Table implements Runnable {
    private static int counter = 0;
    private final int id = counter++;
    private static final Random rand = new Random(47);
    private final int capacity;
    final WaitPerson waitPerson;
    final Restaurant restaurant;
    OrderTicket ticket;
    LinkedBlockingQueue<Plate> platesSetting = new LinkedBlockingQueue<>();
    List<Customer> customers = Collections.synchronizedList(new ArrayList<>());

    public Table(int capacity, WaitPerson wp, Restaurant restaurant) {
        this.capacity = capacity;
        this.waitPerson = wp;
        this.restaurant = restaurant;
    }

    public int getCapacity() {
        return capacity;
    }

    public void deliver(WaitPerson wp, Plate p) throws InterruptedException {
        System.out.println(wp + " delivering " + p + " to " + shortString());
        platesSetting.put(p);
    }

    public void leave() {
        ticket.ordered = 0;
        ticket = null;
        customers.clear();
        System.out.println("Customers on " + shortString() + " leaved");
        System.out.println("Cleaning table");
        try {
            restaurant.ocuupiedTable.remove(this);
            restaurant.emptyTable.put(this);
        } catch (InterruptedException e) {
            System.out.println(shortString() + " interrupted while cleaning");
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Table " + id);
        builder.append(", Customers:{");
        for (Customer c : customers) {
            builder.append(c + ", ");
        }
        builder.append("}");

        return builder.toString();
    }

    public String shortString() {
        return "Table " + id + "";
    }

    public synchronized void waitForTicket() {
        while (null == ticket) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println(this + " interrupted while waiting for tickets");
            }
        }
    }

    public synchronized void orderTicket() {
        OrderTicket ticket = new OrderTicket(this);
        System.out.println(shortString() + "ordering...");
        try {
            for (Course course : Course.values()) {
                Food food = course.randomSelection();
                // 点菜
                System.out.println(shortString() + " ordered " + food);
                ticket.plates.put(new Plate(ticket, food, this, waitPerson));
                ticket.ordered++;
            }
            this.ticket = ticket;
            restaurant.requiring.put(ticket);
            notifyAll();
        } catch (InterruptedException e) {
            System.out.println(shortString() + " interrupted while ordering...");
        }

    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                // 等待客户点菜
                waitForTicket();

                Plate p = platesSetting.take();
                System.out.println(this + " eating " + p);
                TimeUnit.MILLISECONDS.sleep(500);
                ticket.served++;
                if (ticket.served >= ticket.ordered) {
                    // 菜都上齐了
                    TimeUnit.MILLISECONDS.sleep(rand.nextInt(200));
                    System.out.println("Customers on " + shortString() + " are talking after finished meal");
                    leave();
                }
            }
        } catch (InterruptedException e) {
            System.out.println(shortString() + " interrupted while ordering");
        }
    }
}

class OrderTicket {
    private static int counter = 0;
    private final int id = counter++;
    private Table table;
    volatile int ordered = 0;
    volatile int served = 0;
    BlockingQueue<Plate> plates = new LinkedBlockingQueue<>();

    public OrderTicket(Table table) {
        this.table = table;
    }

    public Table getTable() {
        return table;
    }

    public String shortString() {
        return "OrderTicket{" + "id=" + id + "}";
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("OrderTicket " + id + " Plates:{");
        for (Plate p : plates) {
            builder.append(p + ", ");
        }
        builder.append("}");
        return builder.toString();
    }
}

class Plate {
    private final OrderTicket ticket;
    final Food food;
    Table table;
    WaitPerson waitPerson;

    public Plate(OrderTicket ticket, Food food, Table table, WaitPerson waitPerson) {
        this.ticket = ticket;
        this.food = food;
        this.table = table;
        this.waitPerson = waitPerson;
    }

    public OrderTicket getTicket() {
        return ticket;
    }

    public Food getFood() {
        return food;
    }

    @Override
    public String toString() {
        return food.toString();
    }
}

class Customer {
    private static int counter = 0;
    private final int id = counter++;

    public Customer() {}

    @Override
    public String toString() {
        return "Customer{" + "id=" + id + '}';
    }
}

class WaitPerson implements Runnable {
    private static int counter = 0;
    private final int id = counter++;
    private Restaurant restaurant;
    BlockingQueue<Plate> filledOrders = new LinkedBlockingQueue<>();

    public WaitPerson(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                Plate plate = filledOrders.take();
                System.out.println(
                    this + " received " + plate + " delivering to " + plate.getTicket().getTable().shortString());
                // 上菜
                plate.getTicket().getTable().deliver(this, plate);
            }
        } catch (InterruptedException e) {
            System.out.println(this + " interrupted");
        }

        System.out.println(this + " off duty");
    }

    @Override
    public String toString() {
        return "WaitPerson{" + "id=" + id + '}';
    }
}

class Chef implements Runnable {
    private static int counter = 0;
    private final int id = counter++;
    private final Restaurant restaurant;
    private static Random rand = new Random(47);

    public Chef(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                OrderTicket ticket = restaurant.requiring.take();
                while (!ticket.plates.isEmpty()) {
                    Plate p = ticket.plates.take();
                    TimeUnit.MILLISECONDS.sleep(rand.nextInt(500));
                    p.waitPerson.filledOrders.put(p);
                }
            }
        } catch (InterruptedException e) {
            System.out.println(this + " interrupted");
        }

        System.out.println(this + " off duty");
    }

    @Override
    public String toString() {
        return "Chef{" + "id=" + id + '}';
    }
}

class Restaurant implements Runnable {
    private List<WaitPerson> waitPersons = new ArrayList<>();
    private List<Chef> chefs = new ArrayList<>();
    private static Random rand = new Random(47);
    ExecutorService exec;
    BlockingQueue<OrderTicket> requiring = new LinkedBlockingQueue<>();
    BlockingQueue<Table> ocuupiedTable = new LinkedBlockingQueue<>();
    BlockingQueue<Table> emptyTable = new LinkedBlockingQueue<>();

    public Restaurant(ExecutorService exec, int nWaitPersons, int nChefs) {
        this.exec = exec;

        try {
            for (int i = 0; i < nWaitPersons; i++) {
                WaitPerson waitPerson = new WaitPerson(this);
                waitPersons.add(waitPerson);
                exec.execute(waitPerson);

                Table table = new Table(3, waitPerson, this);
                emptyTable.put(table);
                exec.execute(table);
            }
        } catch (InterruptedException e) {
            System.out.println("Interrupted while initialing restaurant");
        }

        for (int i = 0; i < nChefs; i++) {
            Chef chef = new Chef(this);
            chefs.add(chef);
            exec.execute(chef);
        }
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                // 模拟随机到店的客人,人数随机生成，范围在1-4
                Table t = emptyTable.take();
                int customerNum = rand.nextInt(4) + 1;
                for (int i = 0; i < customerNum; i++) {
                    Customer c = new Customer();
                    t.customers.add(c);
                }
                ocuupiedTable.put(t);
                System.out.println("Table joined " + t);

                t.orderTicket();

            }
        } catch (InterruptedException e) {
            System.out.println("Restaurant interrupted");
        }

        System.out.println("Restaurant closing");
    }
}
