package com.github.sparkzxl.core.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.github.sparkzxl.core.service.convert.LocalDateTimeConverter;
import com.github.sparkzxl.core.util.ExcelUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * description: excel service impl
 *
 * @author zhouxinlei
 * @since 2024-05-06 08:54:36
 */
public class DefaultExcelServiceImpl implements ExcelService {

    @Override
    public <T> void exportExcel(List<T> list, Class<T> tClass, HttpServletResponse response) throws IOException {
        setResponse(response);
        EasyExcel.write(response.getOutputStream())
                .head(tClass)
                .excelType(ExcelTypeEnum.XLSX)
                .registerConverter(new LocalDateTimeConverter())
                .sheet("工作簿1")
                .doWrite(list);
    }

    @Override
    public <T, R> void exportExcel(List<T> list, Function<T, R> trFunction, Class<R> tClass, HttpServletResponse response) throws IOException {
        setResponse(response);
        List<R> result = list.stream().map(trFunction).collect(Collectors.toList());
        exportExcel(result, tClass, response);
    }

    @Override
    public <T> void exportExcel(List<T> list, Class<T> tClass, String template, HttpServletResponse response) throws IOException {
        setResponse(response);
        EasyExcel.write(response.getOutputStream())
                .withTemplate(template)
                .excelType(ExcelTypeEnum.XLSX)
                .useDefaultStyle(false)
                .registerConverter(new LocalDateTimeConverter())
                .sheet(0)
                .doFill(list);
    }

    @Override
    public <T, R, M> void importExcel(MultipartFile file, ExcelListener<T, M> excelListener, Class<T> tClass, Function<T, R> function, Consumer<List<R>> consumer) {
        importExcel(file, excelListener, tClass, null, function, consumer);
    }

    @Override
    public <T, R, M> void importExcel(MultipartFile file, ExcelListener<T, M> excelListener, Class<T> tClass, Integer headRowNumber, Function<T, R> function, Consumer<List<R>> consumer) {
        List<T> excelData = ExcelUtils.excelImport(file, excelListener, tClass, null, headRowNumber);
        List<R> result = null;
        if (excelData != null) {
            result = excelData.stream().map(function).collect(Collectors.toList());
        }
        consumer.accept(result);
        excelListener.remove();
    }

    @Override
    public <T, M> void importExcel(MultipartFile file, ExcelListener<T, M> excelListener, Class<T> tClass, Consumer<List<T>> consumer) {
        importExcel(file, excelListener, tClass, null, null, consumer);
    }

    @Override
    public <T, M> void importExcel(MultipartFile file, ExcelListener<T, M> excelListener, Class<T> tClass, Integer headRowNumber, Consumer<List<T>> consumer) {
        List<T> excelData = ExcelUtils.excelImport(file, excelListener, tClass, null, headRowNumber);
        consumer.accept(excelData);
        excelListener.remove();
    }

    public void setResponse(HttpServletResponse response) throws UnsupportedEncodingException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("data", StandardCharsets.UTF_8.displayName()).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xls");
    }
}
