package com.github.sparkzxl.elasticsearch.service.base;

import org.elasticsearch.action.search.SearchRequest;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * description:
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
     * @param index     索引
     * @param id        主键
     * @param objectMap map对象
     * @return boolean
     */
    boolean saveDoc(String index, Serializable id, Map<String, Object> objectMap);

    /**
     * 更新
     *
     * @param index     索引
     * @param id        主键
     * @param objectMap map对象
     * @return boolean
     */
    boolean updateDoc(String index, Serializable id, Map<String, Object> objectMap);

    /**
     * 批量保存
     *
     * @param index         索引
     * @param objectMapList 对象list
     * @return boolean
     */
    boolean saveDocBatch(String index, List<Map<String, Object>> objectMapList);

    /**
     * 批量更新
     *
     * @param index         索引
     * @param objectMapList 对象list
     * @return boolean
     */
    boolean updateDocBatch(String index, List<Map<String, Object>> objectMapList);

    /**
     * 根据id删除文档
     *
     * @param index 索引
     * @param id    主键
     * @return boolean
     */
    boolean deleteDocById(String index, String id);

    /**
     * 根据条件查询单个文档
     *
     * @param searchRequest 搜索请求
     * @return Map<String, Object>
     */
    Map<String, Object> searchOneDoc(SearchRequest searchRequest);

    /**
     * 根据条件查询单个文档
     *
     * @param searchRequest 搜索请求
     * @param tClass        class
     * @return Map<String, Object>
     */
    <T> T searchObjectOneDoc(SearchRequest searchRequest, Class<T> tClass);

    /**
     * search all doc records
     *
     * @param index 索引
     * @return List<Map < String, Object>>
     */
    List<Map<String, Object>> searchAllDoc(String index);

    /**
     * search all doc records
     *
     * @param index  索引
     * @param tClass class
     * @return List<Map < String, Object>>
     */
    <T> List<T> searchObjectAllDoc(String index, Class<T> tClass);

    /**
     * search all doc records
     *
     * @param index 索引
     * @param id    主键
     * @return Map<String, Object>
     */
    Map<String, Object> searchDocById(String index, String id);

    /**
     * search all doc records
     *
     * @param index  索引
     * @param id     主键
     * @param tClass class
     * @return Map<String, Object>
     */
    <T> T searchObjectDocById(String index, String id, Class<T> tClass);

    /**
     * search all doc records
     *
     * @param index  索引
     * @param idList 主键列表
     * @return Map<String, Object>
     */
    List<Map<String, Object>> searchDocsByIdList(String index, List<String> idList);

    /**
     * search all doc records
     *
     * @param index  索引
     * @param idList 主键列表
     * @param tClass class
     * @return Map<String, Object>
     */
    <T> List<T> searchObjectDocsByIdList(String index, List<String> idList, Class<T> tClass);

}
