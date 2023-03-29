package com.github.sparkzxl.mongodb.repository;

import com.github.sparkzxl.mongodb.entity.Entity;
import java.io.Serializable;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * description: 公共仓储层
 *
 * @author zhouxinlei
 */
public interface BaseRepository<T extends Entity> extends MongoRepository<T, Serializable> {

}
