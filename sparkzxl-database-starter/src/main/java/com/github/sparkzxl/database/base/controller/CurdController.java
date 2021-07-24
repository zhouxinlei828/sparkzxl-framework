package com.github.sparkzxl.database.base.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.net.URLEncoder;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageInfo;
import com.github.sparkzxl.core.support.BizExceptionAssert;
import com.github.sparkzxl.core.utils.DateUtils;
import com.github.sparkzxl.database.base.listener.ImportDataListener;
import com.github.sparkzxl.database.dto.DeleteDTO;
import com.github.sparkzxl.database.dto.PageParams;
import com.github.sparkzxl.database.utils.PageInfoUtils;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * description: curd 接口自动生成类
 *
 * @author zhouxinlei
 */
public interface CurdController<Entity, Id extends Serializable, SaveDTO, UpdateDTO, QueryDTO, ExcelEntity> extends BaseController<Entity> {

    Logger logger = LoggerFactory.getLogger(CurdController.class);

    /**
     * 新增
     *
     * @param saveDTO 保存参数
     * @return 实体
     */
    @ApiOperation(value = "新增数据")
    @PostMapping("/save")
    default boolean save(@RequestBody @Validated SaveDTO saveDTO) {
        boolean result = handlerSave(saveDTO);
        if (result) {
            Entity model = BeanUtil.toBean(saveDTO, getEntityClass());
            return getBaseService().save(model);
        }
        return false;
    }

    /**
     * 自定义新增
     *
     * @param model 保存DTO
     * @return 返回true, 调用默认更新, 返回其他不调用默认更新
     */
    default boolean handlerSave(SaveDTO model) {
        return true;
    }


    /**
     * 修改
     *
     * @param updateDTO 更新参数
     * @return boolean
     */
    @ApiOperation(value = "修改数据")
    @PutMapping("/update")
    default boolean update(@RequestBody @Validated UpdateDTO updateDTO) {
        boolean result = handlerUpdate(updateDTO);
        if (result) {
            Entity model = BeanUtil.toBean(updateDTO, getEntityClass());
            return getBaseService().updateById(model);
        }
        return false;
    }

    /**
     * 自定义更新
     *
     * @param model 保存DTO
     * @return 返回true, 调用默认更新, 返回其他不调用默认更新
     */
    default boolean handlerUpdate(UpdateDTO model) {
        return true;
    }


    /**
     * 删除方法
     *
     * @param deleteDTO ids
     * @return boolean
     */
    @ApiOperation(value = "删除数据")
    @DeleteMapping("/delete")
    default boolean delete(@RequestBody DeleteDTO<Id> deleteDTO) {
        if (CollectionUtils.isEmpty(deleteDTO.getIds())) {
            BizExceptionAssert.businessFail("id不能为空");
        }
        boolean result = handlerDelete(deleteDTO.getIds());
        if (result) {
            return getBaseService().removeByIds(deleteDTO.getIds());
        }
        return false;
    }

    /**
     * 自定义删除
     *
     * @param ids ids
     * @return 返回true, 调用默认更新, 返回其他不调用默认更新
     */
    default boolean handlerDelete(List<Id> ids) {
        return true;
    }

    /**
     * 自定义处理返回结果
     *
     * @param pageInfo 数据返回list
     * @return List<?>
     */
    default PageInfo<?> handlerResult(PageInfo<Entity> pageInfo) {
        // 调用注入方法
        return null;
    }

    /**
     * 根据 bean字段 反射出 数据库字段
     *
     * @param beanField 实体字段
     * @param clazz     类
     * @return String
     */
    default String getDbField(String beanField, Class<?> clazz) {
        Field field = ReflectUtil.getField(clazz, beanField);
        if (field == null) {
            return StrUtil.EMPTY;
        }
        TableField tf = field.getAnnotation(TableField.class);
        if (tf != null && StringUtils.isNotEmpty(tf.value())) {
            return tf.value();
        }
        return StrUtil.EMPTY;
    }

    /**
     * 查询对象
     *
     * @param id 主键
     * @return Entity
     */
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "主键", dataType = "long", paramType = "query")})
    @ApiOperation(value = "查询数据", notes = "查询")
    @GetMapping("/get")
    default Entity get(@RequestParam(value = "id") Id id) {
        Entity entity = getBaseService().getById(id);
        handlerEntity(entity);
        return entity;
    }

    /**
     * 分页查询
     *
     * @param params 分页参数
     * @return PageInfo<?>
     */
    @ApiOperation(value = "分页列表查询")
    @PostMapping(value = "/page")
    default PageInfo<?> page(@RequestBody @Validated PageParams<QueryDTO> params) {
        return query(params);
    }

    /**
     * 执行查询
     * 可以覆盖后重写查询逻辑
     *
     * @param params 分页参数
     * @return 分页结果
     */
    default PageInfo<?> query(PageParams<QueryDTO> params) {
        handlerQueryParams(params);
        Entity model = BeanUtil.toBean(params.getModel(), getEntityClass());
        QueryWrapper<Entity> wrapper = handlerWrapper(model, params);
        params.startPage();
        List<Entity> entityList = getBaseService().list(wrapper);
        PageInfo<Entity> pageInfo = PageInfoUtils.pageInfo(entityList);
        // 处理结果
        PageInfo<?> pageInfoDto = handlerResult(pageInfo);
        if (ObjectUtils.isNotEmpty(pageInfoDto)) {
            return pageInfoDto;
        } else {
            return pageInfo;
        }
    }

    /**
     * 处理参数
     *
     * @param entity 实体对象
     */
    default void handlerEntity(Entity entity) {
    }

    /**
     * 处理参数
     *
     * @param params 分页参数
     */
    default void handlerQueryParams(PageParams<QueryDTO> params) {
    }

    /**
     * 条件构造
     *
     * @param model  对象
     * @param params 分页参数
     * @return QueryWrap<Entity>
     */
    default QueryWrapper<Entity> handlerWrapper(Entity model, PageParams<QueryDTO> params) {
        QueryWrapper<Entity> wrapper = model == null ? new QueryWrapper<>() : new QueryWrapper<>(model);
        if (CollUtil.isNotEmpty(params.getMap())) {
            Map<String, String> map = params.getMap();
            //拼装区间
            for (Map.Entry<String, String> field : map.entrySet()) {
                String key = field.getKey();
                String value = field.getValue();
                if (StrUtil.isEmpty(value)) {
                    continue;
                }
                Date date = DateUtils.parse(value);
                if (key.endsWith("_st")) {
                    String beanField = StrUtil.subBefore(key, "_st", true);
                    wrapper.ge(getDbField(beanField, getEntityClass()), DateUtils.beginOfDay(date));
                }
                if (key.endsWith("_ed")) {
                    String beanField = StrUtil.subBefore(key, "_ed", true);
                    wrapper.le(getDbField(beanField, getEntityClass()), DateUtils.endOfDay(date));
                }
            }
        }
        return wrapper;
    }

    /**
     * 批量查询
     *
     * @param data list查询
     * @return 查询结果
     */
    @ApiOperation(value = "列表查询", notes = "列表查询")
    @PostMapping("/list")
    default List<Entity> query(@RequestBody QueryDTO data) {
        Entity model = BeanUtil.toBean(data, getEntityClass());
        QueryWrapper<Entity> wrapper = new QueryWrapper<>(model);
        return getBaseService().list(wrapper);
    }

    /**
     * Excel导入数据
     *
     * @param multipartFile 文件
     * @return 导入记录数
     */
    @ApiOperation("导入数据")
    @PostMapping("/import")
    default Integer importData(@RequestParam("file") MultipartFile multipartFile) {
        ImportDataListener<?> importDataListener = getImportDataListener();
        try {
            Class<?> importExcelClass = importExcelClass();
            if (ObjectUtils.isEmpty(importExcelClass)) {
                logger.error("请先实例化导入Excel处理类：{}", importExcelClass);
            }
            importDataListener.setList(Lists.newArrayList());
            importDataListener.setCount(0);
            EasyExcel.read(multipartFile.getInputStream(), importExcelClass, importDataListener)
                    .sheet(0).doRead();
            return importDataListener.getCount();
        } catch (IOException e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            logger.error("读取Excel发生异常：{}", e.getMessage());
        }
        return 0;
    }

    /**
     * Excel处理类
     *
     * @return ImportDataListener<?>
     */
    default ImportDataListener<?> getImportDataListener() {
        return new ImportDataListener<>();
    }

    /**
     * 导入Excel处理类
     *
     * @return Class<?>
     */
    default Class<?> importExcelClass() {
        return null;
    }

    /**
     * Excel导出数据
     *
     * @param queryDTO 查询参数
     * @param response 响应
     * @throws IOException IO异常
     */
    @ApiOperation("导出数据")
    @GetMapping("/export")
    @RequestMapping(value = "/export", method = RequestMethod.POST, produces = "application/octet-stream")
    default void exportData(@RequestBody QueryDTO queryDTO, HttpServletResponse response) throws IOException {
        List<Entity> entityList = Lists.newArrayList();
        boolean result = handlerExcelQueryList(queryDTO, entityList);
        if (!result) {
            entityList = query(queryDTO);
        }
        List<ExcelEntity> excelEntities = convertExcels(entityList);
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = getFileName();
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        Class excelClass = (Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[6];
        EasyExcel.write(response.getOutputStream(), excelClass).sheet("模板").doWrite(excelEntities);
    }

    /**
     * 自定义查询Excel数据
     *
     * @param queryDTO   查询参数
     * @param entityList 数据列表
     * @return boolean
     */
    default boolean handlerExcelQueryList(QueryDTO queryDTO, List<Entity> entityList) {
        return false;
    }

    /**
     * 转换Excel表格
     *
     * @param entityList list数据
     * @return List<ExcelEntity>
     */
    default List<ExcelEntity> convertExcels(List<Entity> entityList) {
        return Lists.newArrayList();
    }

    /**
     * 获取文件名
     *
     * @return String
     */
    default String getFileName() {
        return URLEncoder.ALL.encode("data_export_".concat(String.valueOf(System.currentTimeMillis())),
                StandardCharsets.UTF_8);
    }

}
