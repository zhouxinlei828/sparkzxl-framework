package com.github.sparkzxl.core.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * description: excel service
 *
 * @author zhouxinlei
 * @since 2024-05-06 08:53:40
 */
public interface ExcelService {

    /**
     * 导出Excel，默认
     *
     * @param list     导出的数据
     * @param tClass   带有excel注解的实体类
     * @param response 相应
     */
    <T> void exportExcel(List<T> list, Class<T> tClass, HttpServletResponse response) throws IOException;

    /**
     * 导出Excel，增加类型转换
     *
     * @param list     导出的数据
     * @param tClass   带有excel注解的实体类
     * @param response 相应
     */
    <T, R> void exportExcel(List<T> list, Function<T, R> map, Class<R> tClass, HttpServletResponse response) throws IOException;


    /**
     * 导出Excel，按照模板导出，这里是填充模板
     *
     * @param list     导出的数据
     * @param tClass   带有excel注解的实体类
     * @param template 模板
     * @param response 相应
     */
    <T> void exportExcel(List<T> list, Class<T> tClass, String template, HttpServletResponse response) throws IOException;

    /**
     * 导入Excel
     *
     * @param file     文件
     * @param tClass   带有excel注解的实体类
     * @param function 类型转换
     * @param consumer 消费数据的操作
     */
    <T, R, M> void importExcel(MultipartFile file, ExcelListener<T, M> excelListener, Class<T> tClass, Function<T, R> function, Consumer<List<R>> consumer);

    /**
     * 导入Excel
     *
     * @param file          文件
     * @param tClass        带有excel注解的实体类
     * @param headRowNumber 表格头行数据
     * @param function      类型转换
     * @param consumer      消费数据的操作
     */
    <T, R, M> void importExcel(MultipartFile file, ExcelListener<T, M> excelListener, Class<T> tClass, Integer headRowNumber, Function<T, R> function, Consumer<List<R>> consumer);

    /**
     * 导入Excel
     *
     * @param file     文件
     * @param tClass   带有excel注解的实体类
     * @param consumer 消费数据的操作
     */
    <T, M> void importExcel(MultipartFile file, ExcelListener<T, M> excelListener, Class<T> tClass, Consumer<List<T>> consumer);


    /**
     * 导入Excel
     *
     * @param file          文件
     * @param tClass        带有excel注解的实体类
     * @param headRowNumber 表格头行数据
     * @param consumer      消费数据的操作
     */
    <T, M> void importExcel(MultipartFile file, ExcelListener<T, M> excelListener, Class<T> tClass, Integer headRowNumber, Consumer<List<T>> consumer);

}
