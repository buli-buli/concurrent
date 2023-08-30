package chapter7.exchanger;

/**
 * @ClassNAME MyFat
 * @Description 自定义一个创建代价高昂的对象类型
 * @Author yu
 * @Date 2023/8/30 16:12
 * @Version 1.0
 */
public class MyFat {
    private static int counter = 0;
    private final int id = counter++;

    private int flag;

    public MyFat() {
        // 让flag自增自减100次，模拟一下高昂的创建代价
        flag = 0;
        for (int i = 0; i < 100; i++) {
            while (flag < 10000) {
                flag++;
            }
            while (flag > 0) {
                flag--;
            }
        }
    }

    @Override
    public String toString() {
        return "MyFat{" + "id=" + id + '}';
    }
}
