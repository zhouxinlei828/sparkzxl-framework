package com.github.sparkzxl.micrometer.rpc.http;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

/***
 * description: http
 *
 * @author zhouxinlei
 * @since 2022-08-10 16:37:16
 */
public class HttpCallData {

    private int code;

    private boolean exception;

    public static void main(String[] args) {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        registry.config().commonTags("tag1", "a", "tag2", "b");
        Counter counter = registry.counter("simple", "tag3", "c");
        counter.increment();
        System.out.println(registry.getMetersAsString());
        System.out.println("+++++++++++++++++++++++++++++++++++");
        Counter counter1 = registry.counter("simple1");
        counter1.increment(2.0);
        Counter counter2 = Counter.builder("simple2")
                .description("A simple counter")
                .tag("tag1", "a")
                .register(registry);
        counter2.increment();

        System.out.println(registry.getMetersAsString());
    }

}
