package com.github.sparkzxl.elasticsearch.service.base;

import com.github.sparkzxl.elasticsearch.page.PageResponse;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * description: 通用es操作 服务类
 *
 * @author zhouxinlei
 * @date 2021-04-17 08:37:05
 */
public interface IBaseElasticsearchService {

    /**
     * 创建索引
     *
     * @param index 索引
     * @return boolean
     */
    boolean createIndex(String index);

    /**
     * delete Index
     *
     * @param index 索引
     * @return boolean
     */
    boolean deleteIndex(String index);

    /**
     * 保存
     *
     * @param index  索引
     * @param id     主键
     * @param object 对象
     * @return boolean
     */
    <T> boolean saveDoc(String index, Serializable id, T object);

    /**
     * 更新
     *
     * @param index  索引
     * @param id     主键
     * @param object 对象
     * @return boolean
     */
    <T> boolean updateDoc(String index, Serializable id, T object);

    /**
     * 批量保存
     *
     * @param index      索引
     * @param objectList 对象list
     * @return boolean
     */
    <T> boolean saveDocBatch(String index, List<T> objectList);

    /**
     * 批量更新
     *
     * @param index      索引
     * @param objectList 对象list
     * @return boolean
     */
    <T> boolean updateDocBatch(String index, List<T> objectList);

    /**
     * 根据id删除文档
     *
     * @param index 索引
     * @param id    主键
     * @return boolean
     */
    boolean deleteDocById(String index, String id);

    /**
     * 根据id删除文档
     *
     * @param index 索引
     * @param ids   主键列表
     * @return boolean
     */
    boolean deleteDocByIds(String index, List<String> ids);

    /**
     * 根据条件查询单个文档
     *
     * @param index               索引
     * @param searchSourceBuilder 搜索请求
     * @param tClass              class
     * @return T
     */
    <T> T searchOneDoc(String index, SearchSourceBuilder searchSourceBuilder, Class<T> tClass);

    /**
     * search all doc records
     *
     * @param index  索引
     * @param tClass class
     * @return List<T>
     */
    <T> List<T> searchAllDoc(String index, Class<T> tClass);

    /**
     * search all doc records
     *
     * @param index  索引
     * @param id     主键
     * @param tClass class
     * @return T
     */
    <T> T searchDocById(String index, String id, Class<T> tClass);

    /**
     * search all doc records
     *
     * @param index  索引
     * @param idList 主键列表
     * @param tClass class
     * @return List<T>
     */
    <T> List<T> searchDocsByIdList(String index, List<String> idList, Class<T> tClass);

    /**
     * search all doc records
     *
     * @param index  索引
     * @param idList 主键列表
     * @param tClass class
     * @return Map<String, List < T>>
     */
    <T> Map<String, T> searchDocsMapByIdList(String index, List<String> idList, Class<T> tClass);

    /**
     * search all doc records
     *
     * @param index               索引
     * @param searchSourceBuilder 资源搜索
     * @param tClass              class
     * @return List<T>
     */
    <T> List<T> searchDocList(String index, SearchSourceBuilder searchSourceBuilder, Class<T> tClass);

    /**
     * search all doc records
     *
     * @param index               索引
     * @param searchSourceBuilder 资源搜索
     * @param aggName             分组字段
     * @param tClass              class
     * @return Map<String, List < T>>
     */
    <T> Map<String, List<T>> searchDocsGroupMap(String index, SearchSourceBuilder searchSourceBuilder, String aggName, Class<T> tClass);

    /**
     * 聚合查询
     *
     * @param index               索引
     * @param aggName             分组字段
     * @param searchSourceBuilder 资源搜索
     * @return Map<String, Long>
     */
    Map<String, Long> aggregationSearchDoc(String index, SearchSourceBuilder searchSourceBuilder, String aggName);

    /**
     * 分页搜索
     *
     * @param index               索引
     * @param searchSourceBuilder 查询构造器
     * @param clazz               试题类型
     * @param pageNum             当前页
     * @param pageSize            分页大小
     * @return PageResponse<T>
     */
    <T> PageResponse<T> search(String index, SearchSourceBuilder searchSourceBuilder, Class<T> clazz,
                               Integer pageNum, Integer pageSize);
}
