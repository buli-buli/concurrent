package chapter3.atomicity;

/**
 * @ClassNAME SerialNumberGenerator
 * @Description 一个产生序列数字的类，每当nextSerailNumber()被调用时，它必须向调用者返回唯一的值
 * @Author yu
 * @Date 2023/8/24 17:08
 * @Version 1.0
 */
public class SerialNumberGenerator {
    private static volatile int serialNumber = 0;

    public static int nextSerialNumber() {
        return serialNumber++;
    }
}
