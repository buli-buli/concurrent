package chapter7.semaphore;

/**
 * @ClassNAME Fat
 * @Description 为了创建一个示例，我们可以使用Fat，这是一种创建代价高昂的对象类型，因为它的构造器运行起来很耗时
 * @Author yu
 * @Date 2023/8/30 15:06
 * @Version 1.0
 */
public class Fat {
    private volatile double d;
    private static int counter = 0;
    private final int id = counter++;

    public Fat() {
        for (int i = 0; i < 10000; i++) {
            d += (Math.PI + Math.E) / (double)i;
        }
    }

    public void operation() {
        System.out.println(this);
    }

    @Override
    public String toString() {
        return "Fat{" + "id=" + id + '}';
    }
}
