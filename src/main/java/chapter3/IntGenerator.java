package chapter3;

/**
 * @ClassNAME IntGenerator
 * @Description 整数生成器抽象类
 * @Author yu
 * @Date 2023/8/24 14:17
 * @Version 1.0
 */
public abstract class IntGenerator {
    private volatile boolean canceled = false;

    public abstract int next();

    public void cancel() {
        canceled = true;
    }

    public boolean isCanceled() {
        return canceled;
    }
}
