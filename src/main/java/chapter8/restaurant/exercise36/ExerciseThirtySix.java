package chapter8.restaurant.exercise36;

import java.util.ArrayList;
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

class Table {
    private static int counter = 0;
    private final int id = counter++;
    private final int capacity;
    final WaitPerson waitPerson;
    OrderTicket ticket;
    SynchronousQueue<Plate> platesSetting = new SynchronousQueue<>();
    LinkedBlockingQueue<Customer> customers = new LinkedBlockingQueue<>();

    public Table(int capacity, WaitPerson wp, Restaurant restaurant) {
        this.capacity = capacity;
        this.waitPerson = wp;
        ticket = new OrderTicket(restaurant, this);
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean join(Customer c) {
        if (customers.size() >= capacity) {
            System.out.println(this + " is full");
            return false;
        }

        try {
            customers.put(c);
        } catch (InterruptedException e) {
            System.out.println(this + " interrupted while " + c + " is joining");
        }

        return true;
    }

    public void leave(Customer customer) {
        if (customers.remove(customer)) {
            System.out.println(customer + " has leaved");
        }
    }

    public void deliver(Plate p) throws InterruptedException {
        platesSetting.put(p);
    }

    @Override
    public String toString() {
        return "Table{" + "id=" + id + '}';
    }
}

class OrderTicket {
    private static int counter = 0;
    private final int id = counter++;
    private Table table;
    private Restaurant restaurant;

    public OrderTicket(Restaurant restaurant, Table table) {
        this.table = table;
        this.restaurant = restaurant;
    }

    public Table getTable() {
        return table;
    }

    public void addRequire(Plate plate) {
        try {
            restaurant.requiring.put(plate);
        } catch (InterruptedException e) {
            System.out.println(this + " interrupted while adding plate");
        }
    }

    @Override
    public String toString() {
        return "OrderTicket{" + "id=" + id + "}";
    }
}

class Plate {
    private final OrderTicket ticket;
    final Food food;
    Customer customer;
    WaitPerson waitPerson;

    public Plate(OrderTicket ticket, Food food, Customer customer, WaitPerson waitPerson) {
        this.ticket = ticket;
        this.food = food;
        this.customer = customer;
        this.waitPerson = waitPerson;
    }

    public OrderTicket getTicket() {
        return ticket;
    }

    public Food getFood() {
        return food;
    }

    public Customer getCustomer() {
        return customer;
    }

    @Override
    public String toString() {
        return food.toString();
    }
}

class Customer implements Runnable {
    private static int counter = 0;
    private final int id = counter++;
    private static Random rand = new Random(47);
    private Table table;
    private Restaurant restaurant;

    public Customer(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public void run() {
        Table t = restaurant.getASeat(this);
        if (null == t) {
            System.out.println("没位置了, " + this + " 气哄哄地离开了");
            Thread.interrupted();
        }
        for (Course course : Course.values()) {
            Food food = course.randomSelection();

            t.join(this);
            this.table = t;
            // 点菜
            System.out.println(this + " ordered " + food + " on " + table);
            table.ticket.addRequire(new Plate(table.ticket, food, this, table.waitPerson));
        }
    }

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
                System.out.println(this + " received " + plate + " delivering to " + plate.getCustomer());
                // 上菜
                plate.getTicket().getTable().deliver(plate);
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
                Plate plate = restaurant.requiring.take();
                TimeUnit.MILLISECONDS.sleep(rand.nextInt(5000));
                plate.waitPerson.filledOrders.put(plate);
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
    List<Table> tables = new ArrayList<>();
    ExecutorService exec;
    BlockingQueue<Plate> requiring = new LinkedBlockingQueue<>();
    private volatile int customerCount = 0;

    public Restaurant(ExecutorService exec, int nWaitPersons, int nChefs) {
        this.exec = exec;

        for (int i = 0; i < nWaitPersons; i++) {
            WaitPerson waitPerson = new WaitPerson(this);
            waitPersons.add(waitPerson);

            Table table = new Table(3, waitPerson, this);
            tables.add(table);

            Dinning dinning = new Dinning(table);

            exec.execute(waitPerson);
            exec.execute(dinning);
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
                Customer c = new Customer(this);
                exec.execute(c);
                customerCount++;
                TimeUnit.MILLISECONDS.sleep(1000);
            }
        } catch (InterruptedException e) {
            System.out.println("Restaurant interrupted");
        }

        System.out.println("Restaurant closing");
    }

    public Table getASeat(Customer customer) {
        for (Table t : tables) {
            if (t.customers.size() <= t.getCapacity()) {
                t.join(customer);
                return t;
            }
        }
        return null;
    }
}

class Dinning implements Runnable {
    private static int counter = 0;
    private final int id = counter++;
    private Table table;

    public Dinning(Table table) {
        this.table = table;
    }

    @Override
    public String toString() {
        return "Dinning{" + "id=" + id + '}';
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                Plate plate = table.platesSetting.take();
                Customer c = plate.getCustomer();
                System.out.println(c + " is having " + plate.food);
                Thread.sleep(100);
                table.leave(c);
            }
        } catch (InterruptedException e) {
            System.out.println(this + " interrupted");
        }
    }
}
