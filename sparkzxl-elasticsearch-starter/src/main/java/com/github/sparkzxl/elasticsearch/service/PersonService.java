package com.github.sparkzxl.elasticsearch.service;

import com.github.sparkzxl.elasticsearch.model.Person;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * description:PersonService
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:18:57
 */
public interface PersonService {

    /**
     * 创建索引
     *
     * @param index 索引
     */
    void createIndex(String index);

    /**
     * delete Index
     *
     * @param index 索引
     */
    void deleteIndex(String index);

    /**
     * 保存
     *
     * @param index 索引
     * @param list  对象列表
     */
    void insert(String index, List<Person> list);

    /**
     * 更新
     *
     * @param index 索引
     * @param list  对象列表
     */
    void update(String index, List<Person> list);

    /**
     * 删除
     *
     * @param index  索引
     * @param person 对象
     */
    void delete(String index, @Nullable Person person);

    /**
     * search all doc records
     *
     * @param index 索引
     * @return List<Person>
     */
    List<Person> searchList(String index);

}
