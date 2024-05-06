package com.github.sparkzxl.core.service;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * description: excel监听处理类
 *
 * @author zhouxinlei
 */
@Slf4j
public class ExcelListener<T, M> extends AnalysisEventListener<T> {

    public ThreadLocal<List<T>> listThreadLocal = new TransmittableThreadLocal<List<T>>() {
        @Override
        protected List<T> initialValue() {
            return Lists.newArrayList();
        }
    };

    private final ExcelBaseService<M> excelBaseService;

    public ExcelListener(ExcelBaseService<M> excelBaseService) {
        this.excelBaseService = excelBaseService;
    }
    protected AtomicInteger count;

    public Integer getCount() {
        return count.get();
    }

    public List<T> getList() {
        return listThreadLocal.get();
    }

    public void remove() {
        listThreadLocal.remove();
        setCount(0);
    }

    public void setCount(int value) {
        this.count = new AtomicInteger(value);
    }

    @Override
    public void invoke(T excelEntity, AnalysisContext analysisContext) {
        List<T> list = listThreadLocal.get();
        list.add(excelEntity);
        count.incrementAndGet();
        log.info("解析到一条数据:{}", JSON.toJSONString(excelEntity));
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.info("所有数据解析完成！共计解析:【{}】条", count.get());
        List<T> list = listThreadLocal.get();
        for (T t : list) {
            M m = convertModel(t);
            if (m != null) {
                excelBaseService.save(m);
            }
        }
    }

    public M convertModel(T t) {
        return null;
    }
}
