package com.github.sparkzxl.core.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.util.MapUtils;
import com.alibaba.fastjson.JSON;
import com.github.sparkzxl.core.service.ExcelBaseService;
import com.github.sparkzxl.core.service.ExcelListener;
import com.github.sparkzxl.core.support.ArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * description: excel 工具类封装
 *
 * @author zhouxinlei
 * @since 2024-05-06 09:00:42
 */
public class ExcelUtils {

    private static final Logger log = LoggerFactory.getLogger(ExcelUtils.class);

    /**
     * 将列表以 Excel 响应给前端
     *
     * @param response  响应
     * @param fileName  文件名
     * @param sheetName Excel sheet 名
     * @param head      Excel head 头
     * @param data      数据列表哦
     * @param <T>       泛型，保证 head 和 data 类型的一致性
     * @throws IOException 写入失败的情况
     */
    public static <T> void excelExport(HttpServletResponse response, String fileName, String sheetName,
                                       Class<T> head, List<T> data) throws IOException {
        write(response, fileName);
        // 这里需要设置不关闭流
        EasyExcel.write(response.getOutputStream(), head).autoCloseStream(Boolean.FALSE).sheet(sheetName)
                .doWrite(data);
    }


    /**
     * 根据模板导出
     *
     * @param response     响应
     * @param templatePath 模板名称
     * @param fileName     文件名
     * @param sheetName    Excel sheet 名
     * @param head         Excel head 头
     * @param data         数据列表哦
     * @param <T>          泛型，保证 head 和 data 类型的一致性
     * @throws IOException 写入失败的情况
     */
    public static <T> void excelExport(HttpServletResponse response, String templatePath, String fileName, String sheetName,
                                       Class<T> head, List<T> data) throws IOException {
        write(response, fileName);
        // 这里需要设置不关闭流
        EasyExcel.write(response.getOutputStream(), head).withTemplate(templatePath).autoCloseStream(Boolean.FALSE).sheet(sheetName)
                .doWrite(data);
    }

    /**
     * 根据参数，只导出指定列
     *
     * @param response                响应
     * @param fileName                文件名
     * @param sheetName               Excel sheet 名
     * @param head                    Excel head 头
     * @param data                    数据列表哦
     * @param excludeColumnFiledNames 排除的列
     * @param <T>                     泛型，保证 head 和 data 类型的一致性
     * @throws IOException 写入失败的情况
     */
    public static <T> void excelExport(HttpServletResponse response, String fileName, String sheetName,
                                       Class<T> head, List<T> data, Set<String> excludeColumnFiledNames) throws IOException {
        write(response, fileName);
        // 这里需要设置不关闭流
        EasyExcel.write(response.getOutputStream(), head).autoCloseStream(Boolean.FALSE).excludeColumnFieldNames(excludeColumnFiledNames).sheet(sheetName)
                .doWrite(data);
    }


    private static void write(HttpServletResponse response, String fileName) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        } catch (Exception e) {
            // 重置response
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            Map<String, String> map = MapUtils.newHashMap();
            map.put("status", "failure");
            map.put("message", "下载文件失败" + e.getMessage());
            try {
                response.getWriter().println(JSON.toJSONString(map));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static <T> List<T> read(MultipartFile file, Class<T> head) throws IOException {
        return EasyExcel.read(file.getInputStream(), head, null)
                // 不要自动关闭，交给 Servlet 自己处理
                .autoCloseStream(false)
                .doReadAllSync();
    }

    /**
     * 读取 Excel(多个 sheet)
     *
     * @param excel    文件
     * @param rowModel 实体类映射
     * @return Excel 数据 list
     */
    public static <T, M> List<T> excelImport(MultipartFile excel, ExcelListener<T, M> excelListener, Class<T> rowModel, Integer sheetNo, Integer headRowNumber) {
        ExcelReaderBuilder readerBuilder = getReader(excel, excelListener);
        if (readerBuilder == null) {
            return null;
        }
        if (headRowNumber == null) {
            headRowNumber = 1;
        }
        if (sheetNo == null) {
            readerBuilder.head(rowModel).headRowNumber(headRowNumber).doReadAll();
        } else {
            readerBuilder.sheet(sheetNo).head(rowModel).headRowNumber(headRowNumber).doRead();
        }
        return excelListener.getList();
    }


    /**
     * 读取 Excel(多个 sheet)
     *
     * @param excel    文件
     * @param rowModel 实体类映射
     * @return Excel 数据 list
     */
    public static <T, M> List<T> excelImport(MultipartFile excel, ExcelListener<T, M> excelListener, Class<T> rowModel) {
        return excelImport(excel, excelListener, rowModel, null, null);
    }

    /**
     * 读取某个 sheet 的 Excel
     *
     * @param excel         文件
     * @param rowModel      实体类映射
     * @param sheetNo       sheet 的序号 从1开始
     * @param headRowNumber 表头行数，默认为1
     * @return Excel 数据 list
     */
    public static <T, M> List<T> excelImport(MultipartFile excel, ExcelListener<T, M> excelListener, Class<T> rowModel, int sheetNo,
                                             Integer headRowNumber) {
        return excelImport(excel, excelListener, rowModel, sheetNo, headRowNumber);
    }

    /**
     * 返回 ExcelReader
     *
     * @param excel         需要解析的 Excel 文件
     * @param excelListener 监听器
     */
    private static <T, M> ExcelReaderBuilder getReader(MultipartFile excel,
                                                       ExcelListener<T, M> excelListener) {
        String filename = excel.getOriginalFilename();
        if (filename == null || (!filename.toLowerCase().endsWith(".xls") && !filename.toLowerCase().endsWith(".xlsx"))) {
            throw new ArgumentException("文件格式错误！");
        }
        InputStream inputStream;
        try {
            inputStream = new BufferedInputStream(excel.getInputStream());
            return EasyExcel.read(inputStream, excelListener);
        } catch (IOException e) {
            log.error("读取Excel失败：", e);
        }
        return null;
    }
}
