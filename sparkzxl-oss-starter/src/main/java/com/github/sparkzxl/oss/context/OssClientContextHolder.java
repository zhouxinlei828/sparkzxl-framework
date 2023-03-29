package com.github.sparkzxl.oss.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import java.util.ArrayDeque;
import java.util.Deque;
import org.apache.commons.lang3.StringUtils;

/**
 * description: 获取当前线程client
 *
 * @author zhouxinlei
 */
public class OssClientContextHolder {

    /**
     * 为什么要用链表存储(准确的是栈)
     * <pre>
     * 为了支持嵌套切换，如ABC三个service都是不同的数据源
     * 其中A的某个业务要调B的方法，B的方法需要调用C的方法。一级一级调用切换，形成了链。
     * 传统的只设置当前线程的方式不能满足此业务需求，必须使用栈，后进先出。
     * </pre>
     */
    private static final ThreadLocal<Deque<String>> LOOKUP_KEY_HOLDER = new TransmittableThreadLocal<Deque<String>>() {
        @Override
        protected Deque<String> initialValue() {
            return new ArrayDeque<>();
        }
    };

    private OssClientContextHolder() {
    }

    /**
     * 获得当前线程client
     *
     * @return 客户端名称
     */
    public static String peek() {
        return LOOKUP_KEY_HOLDER.get().peek();
    }

    /**
     * 设置当前线程client
     * <p>
     * 如非必要不要手动调用，调用后确保最终清除
     * </p>
     *
     * @param clientName 客户端名称
     */
    public static String push(String clientName) {
        String clientStr = StringUtils.isEmpty(clientName) ? "" : clientName;
        LOOKUP_KEY_HOLDER.get().push(clientStr);
        return clientStr;
    }

    /**
     * 清空当前线程客户端
     * <p>
     * 如果当前线程是连续切换客户端 只会移除掉当前线程的客户端名称
     * </p>
     */
    public static void poll() {
        Deque<String> deque = LOOKUP_KEY_HOLDER.get();
        deque.poll();
        if (deque.isEmpty()) {
            LOOKUP_KEY_HOLDER.remove();
        }
    }

    /**
     * 强制清空本地线程
     * <p>
     * 防止内存泄漏，如手动调用了push可调用此方法确保清除
     * </p>
     */
    public static void clear() {
        LOOKUP_KEY_HOLDER.remove();
    }
}
