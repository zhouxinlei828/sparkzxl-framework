package com.github.sparkzxl.database.base.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * description: excel监听处理类
 *
 * @author: zhouxinlei
 * @date: 2021-03-14 12:37:28
 */
public class ImportDataListener<ExcelEntity> extends AnalysisEventListener<ExcelEntity> {

    protected final List<ExcelEntity> list = Lists.newArrayList();
    protected final AtomicInteger count = new AtomicInteger(0);

    public Integer getCount() {
        return count.get();
    }

    @Override
    public void invoke(ExcelEntity excelEntity, AnalysisContext analysisContext) {
        list.add(excelEntity);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
