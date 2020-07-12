package com.sparksys.commons.core.utils.exception;

import cn.hutool.core.exceptions.ExceptionUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * description: 异常处理工具类
 *
 * @author zhouxinlei
 * @date 2020-05-24 12:53:29
 */
public class ExceptionUtils extends ExceptionUtil {

    /**
     * 将CheckedException转换为UncheckedException
     *
     * @param e
     * @return RuntimeException
     * @throws
     * @author zhouxinlei
     * @date 2019-12-27 10:10:56
     */
    public static RuntimeException unchecked(Throwable e) {
        if (e instanceof IllegalAccessException || e instanceof IllegalArgumentException
                || e instanceof NoSuchMethodException) {
            return new IllegalArgumentException(e);
        } else if (e instanceof InvocationTargetException) {
            return new RuntimeException(((InvocationTargetException) e).getTargetException());
        } else if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        } else {
            return new RuntimeException(e);
        }
    }


    @FunctionalInterface
    public interface ConsumerExceptions<T, E extends Exception> {
        /**
         * 应用接受
         *
         * @param t
         * @throws E
         */
        void accept(T t) throws E;
    }

    @FunctionalInterface
    public interface BiConsumerExceptions<T, U, E extends Exception> {
        /**
         * 应用接受
         *
         * @param t
         * @param u
         * @throws E
         */
        void accept(T t, U u) throws E;
    }

    @FunctionalInterface
    public interface FunctionExceptions<T, R, E extends Exception> {
        /**
         * 应用接受
         *
         * @param t
         * @return R
         * @throws E
         * @author zhouxinlei
         * @date 2019-12-26 20:03:58
         */
        R apply(T t) throws E;
    }

    @FunctionalInterface
    public interface SupplierExceptions<T, E extends Exception> {
        /**
         * 获取
         *
         * @param
         * @return T
         * @throws E
         * @author zhouxinlei
         * @date 2019-12-26 20:05:45
         */
        T get() throws E;
    }

    @FunctionalInterface
    public interface RunnableExceptions<E extends Exception> {
        /**
         * 执行
         *
         * @param
         * @return void
         * @throws E
         * @author zhouxinlei
         * @date 2019-12-26 20:06:34
         */
        void run() throws E;
    }

    /**
     * .forEach(rethrowConsumer(name -> System.out.println(Class.forName(name)))); or .forEach(rethrowConsumer(ClassNameUtil::println));
     */
    public static <T, E extends Exception> Consumer<T> rethrowConsumer(ConsumerExceptions<T, E> consumer) throws E {
        return t -> {
            try {
                consumer.accept(t);
            } catch (Exception exception) {
                throwAsUnchecked(exception);
            }
        };
    }

    public static <T, U, E extends Exception> BiConsumer<T, U> rethrowBiConsumer(BiConsumerExceptions<T, U, E> biConsumer) throws E {
        return (t, u) -> {
            try {
                biConsumer.accept(t, u);
            } catch (Exception exception) {
                throwAsUnchecked(exception);
            }
        };
    }

    /**
     * .map(rethrowFunction(name -> Class.forName(name))) or .map(rethrowFunction(Class::forName))
     */
    public static <T, R, E extends Exception> Function<T, R> rethrowFunction(FunctionExceptions<T, R, E> function) throws E {
        return t -> {
            try {
                return function.apply(t);
            } catch (Exception exception) {
                throwAsUnchecked(exception);
                return null;
            }
        };
    }

    /**
     * rethrowSupplier(() -> new StringJoiner(new String(new byte[]{77, 97, 114, 107}, "UTF-8"))),
     */
    public static <T, E extends Exception> Supplier<T> rethrowSupplier(SupplierExceptions<T, E> function) throws E {
        return () -> {
            try {
                return function.get();
            } catch (Exception exception) {
                throwAsUnchecked(exception);
                return null;
            }
        };
    }

    /**
     * uncheck(() -> Class.forName("xxx"));
     */
    public static void uncheck(RunnableExceptions t) {
        try {
            t.run();
        } catch (Exception exception) {
            throwAsUnchecked(exception);
        }
    }

    /**
     * uncheck(() -> Class.forName("xxx"));
     */
    public static <R, E extends Exception> R uncheck(SupplierExceptions<R, E> supplier) {
        try {
            return supplier.get();
        } catch (Exception exception) {
            throwAsUnchecked(exception);
            return null;
        }
    }

    /**
     * uncheck(Class::forName, "xxx");
     */
    public static <T, R, E extends Exception> R uncheck(FunctionExceptions<T, R, E> function, T t) {
        try {
            return function.apply(t);
        } catch (Exception exception) {
            throwAsUnchecked(exception);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable> void throwAsUnchecked(Exception exception) throws E {
        throw (E) exception;
    }

}
