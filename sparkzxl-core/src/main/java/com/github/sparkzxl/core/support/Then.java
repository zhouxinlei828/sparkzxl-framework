package com.github.sparkzxl.core.support;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.util.function.Consumer;

/**
 * <pre>
 *     在{@linkplain Then#withException(String)}等默认抛出异常的方法中,
 *     当{@linkplain ExceptionAssert}的返回值为{@linkplain Boolean#TRUE} 时,则会抛出异常
 *     如果有特殊要求 可使用{@linkplain Then#with(Consumer)}进行手动处理
 * </pre>
 *
 * @author zhouxinlei
 * @date 2021-07-29 13:29
 */
@Slf4j
@RequiredArgsConstructor
public class Then {

    private final boolean value;

    private static Class<? extends RuntimeException> defaultException = RuntimeException.class;

    /**
     * 设置全局默认异常
     * 必须实现此构造器
     */
    public static void setDefaultException(@NonNull Class<? extends RuntimeException> defaultException) {
        //检查构造器
        try {
            defaultException.getConstructor(String.class);
        } catch (Throwable e) {
            throw new RuntimeException("异常Class'" + defaultException + "'中必须要有RuntimeException#Throwable(String)构造器,如果是内部类需要声明为public static");
        }
        Then.defaultException = defaultException;
    }

    /**
     * 使用默认的exception
     *
     * @param msg
     */
    public void withDefaultException(String msg) {
        this.withCustomException(defaultException, msg);
    }


    /**
     * 将Value返回 手动处理
     */
    public void with(Consumer<Boolean> function) {
        function.accept(this.value);
    }

    /**
     * 抛出自定义Exception
     */
    public void withCustomException(Class<? extends RuntimeException> clazz, String msg) {
        if (this.value) {
            throw createInstance(clazz, msg);
        }
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    //********************************常用异常****************************************//

    /**
     * 抛出运行时异常
     */
    public void withRuntimeException(String msg) {
        if (this.value) {
            throw new RuntimeException(msg);
        }
    }

    /**
     * 抛出空指针异常
     */
    public void withNullPointException(String msg) {
        if (this.value) {
            throw new NullPointerException(msg);
        }
    }

    /**
     * 抛出文件未找到异常
     */
    public void withFileNotFoundException(String msg) throws FileNotFoundException {
        if (this.value) {
            throw new FileNotFoundException(msg);
        }
    }

    /**
     * 抛出class未找到异常
     */
    public void withClassNotFoundException(String msg) throws ClassNotFoundException {
        if (this.value) {
            throw new ClassNotFoundException(msg);
        }
    }

    /**
     * 抛出下标越界异常
     */
    public void withIndexOutOfBoundsException(String msg) {
        if (this.value) {
            throw new IndexOutOfBoundsException(msg);
        }
    }

    /**
     * 抛出算数异常
     */
    public void withArithmeticException(String msg) {
        if (this.value) {
            throw new ArithmeticException(msg);
        }
    }

    /**
     * 抛出异常
     */
    public void withException(String msg) throws Exception {
        if (this.value) {
            throw new Exception(msg);
        }
    }

    /**
     * 通常来说不建议这样使用 因为不管是true还是false都会创建这个异常对象
     */
    public void withRuntimeException(RuntimeException exception) {
        if (this.value) {
            throw exception;
        }
    }

    /**
     * 通常来说不建议这样使用 因为不管是true还是false都会创建这个异常对象
     */
    public void withException(Exception exception) throws Exception {
        if (this.value) {
            throw exception;
        }
    }


    /**
     * 必须实现此构造器
     */
    public static <T extends RuntimeException> T createInstance(Class<T> clazz, String message) {
        try {
            Constructor<? extends RuntimeException> constructor = clazz.getConstructor(String.class);
            throw constructor.newInstance(message);
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            log.error("异常Class'{}'中必须要有RuntimeException#Throwable(String)构造器,如果是内部类需要声明为public static", clazz);
            throw new RuntimeException(message);
        }
    }
}
