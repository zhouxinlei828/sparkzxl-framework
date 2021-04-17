package com.github.sparkzxl.elasticsearch.service.base;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.sparkzxl.core.support.SparkZxlExceptionAssert;
import com.github.sparkzxl.elasticsearch.contants.BaseElasticsearchConstant;
import com.github.sparkzxl.elasticsearch.page.PageResponse;
import com.github.sparkzxl.elasticsearch.properties.ElasticsearchProperties;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.HttpAsyncResponseConsumerFactory;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * description: 通用es操作 服务实现类
 *
 * @author zhouxinlei
 */
@Slf4j
public class BaseElasticsearchServiceImpl implements IBaseElasticsearchService {

    @Autowired
    protected RestHighLevelClient restHighLevelClient;

    @Autowired
    private ElasticsearchProperties elasticsearchProperties;

    @Autowired
    public void setRestHighLevelClient(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    @Autowired
    public void setElasticsearchProperties(ElasticsearchProperties elasticsearchProperties) {
        this.elasticsearchProperties = elasticsearchProperties;
    }

    protected static final RequestOptions COMMON_OPTIONS;

    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        // 默认缓冲限制为100MB，此处修改为30MB。
        builder.setHttpAsyncResponseConsumerFactory(new HttpAsyncResponseConsumerFactory.HeapBufferedResponseConsumerFactory(30 * 1024 * 1024));
        COMMON_OPTIONS = builder.build();
    }

    /**
     * create elasticsearch index (asyc)
     *
     * @param index elasticsearch index
     */
    protected void createIndexRequest(String index) {
        try {
            CreateIndexRequest request = new CreateIndexRequest(index);
            // Settings for this index
            request.settings(Settings.builder().put("index.number_of_shards", elasticsearchProperties.getIndex().getNumberOfShards()).put("index.number_of_replicas", elasticsearchProperties.getIndex().getNumberOfReplicas()));
            CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(request, COMMON_OPTIONS);
            log.info(" whether all of the nodes have acknowledged the request : [{}]", createIndexResponse.isAcknowledged());
            log.info(" Indicates whether the requisite number of shard copies were started for each shard in the index before timing out " +
                    ":[{}]", createIndexResponse.isShardsAcknowledged());
        } catch (IOException e) {
            throw new ElasticsearchException("创建索引 {" + index + "} 失败");
        }
    }

    /**
     * delete elasticsearch index
     *
     * @param index elasticsearch index name
     */
    protected void deleteIndexRequest(String index) {
        DeleteIndexRequest deleteIndexRequest = buildDeleteIndexRequest(index);
        try {
            restHighLevelClient.indices().delete(deleteIndexRequest, COMMON_OPTIONS);
        } catch (IOException e) {
            throw new ElasticsearchException("删除索引 {" + index + "} 失败");
        }
    }

    /**
     * build DeleteIndexRequest
     *
     * @param index elasticsearch index name
     */
    private static DeleteIndexRequest buildDeleteIndexRequest(String index) {
        return new DeleteIndexRequest(index);
    }

    /**
     * build IndexRequest
     *
     * @param index  elasticsearch index name
     * @param id     request object id
     * @param object request object
     * @return {@link IndexRequest}
     */
    protected static IndexRequest buildIndexRequest(String index, String id, Object object) {
        return new IndexRequest(index).id(id).source(JSONObject.toJSONString(object), XContentType.JSON);
    }

    protected static SearchRequest buildSearchRequest(String index) {
        return new SearchRequest(index);
    }

    /**
     * exec updateRequest
     *
     * @param index  elasticsearch index name
     * @param id     Document id
     * @param object request object
     */
    protected void updateRequest(String index, String id, Object object) throws Exception {
        UpdateRequest updateRequest = new UpdateRequest(index, id).doc(JSONObject.toJSONString(object), XContentType.JSON);
        restHighLevelClient.update(updateRequest, COMMON_OPTIONS);
    }

    /**
     * exec deleteRequest
     *
     * @param index elasticsearch index name
     * @param id    Document id
     */
    protected void deleteRequest(String index, String id) throws Exception {
        DeleteRequest deleteRequest = new DeleteRequest(index, id);
        restHighLevelClient.delete(deleteRequest, COMMON_OPTIONS);
    }

    /**
     * search all
     *
     * @return {@link SearchResponse}
     */
    protected SearchResponse search(SearchRequest searchRequest) throws Exception {
        return restHighLevelClient.search(searchRequest, COMMON_OPTIONS);
    }

    protected SearchResponse search(String index) {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, COMMON_OPTIONS);
        } catch (IOException e) {
            log.error(e.getMessage());
            log.error(e.getMessage());
        }
        return searchResponse;
    }

    @Override
    public boolean createIndex(String index) {
        createIndexRequest(index);
        return true;
    }

    @Override
    public boolean deleteIndex(String index) {
        deleteIndexRequest(index);
        return true;
    }

    @Override
    public <T> boolean saveDoc(String index, Serializable id, T object) {
        try {
            IndexRequest request = buildIndexRequest(index, id == null ? getESId(object) : String.valueOf(id), object);
            try {
                restHighLevelClient.index(request, COMMON_OPTIONS);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        } finally {
            try {
                restHighLevelClient.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return false;
    }

    @Override
    public <T> boolean updateDoc(String index, Serializable id, T object) {
        try {
            updateRequest(index, id == null ? getESId(object) : String.valueOf(id), object);
        } catch (Exception e) {
            log.error("更新索引 [{}] 数据 [{}] 失败", index, object);
            return false;
        }
        return true;
    }

    @Override
    public <T> boolean saveDocBatch(String index, List<T> objectList) {
        return saveOrUpdateBatch(index, objectList);
    }


    private <T> boolean saveOrUpdateBatch(String index, List<T> objectList) {
        boolean result;
        try {
            BulkRequest request = new BulkRequest();
            objectList.forEach(o -> {
                IndexRequest indexRequest = new IndexRequest(index).id(getESId(o))
                        .source(JSON.toJSONString(o), XContentType.JSON);
                request.add(indexRequest);
            });
            BulkResponse bulk = restHighLevelClient.bulk(request, COMMON_OPTIONS);
            result = bulk.status().equals(RestStatus.OK);
        } catch (IOException e) {
            log.error(e.getMessage());
            result = false;
        } finally {
            try {
                restHighLevelClient.close();
            } catch (IOException e) {
                log.error(e.getMessage());
                result = false;
            }
        }
        return result;
    }

    @Override
    public <T> boolean updateDocBatch(String index, List<T> objectList) {
        return saveOrUpdateBatch(index, objectList);
    }

    @Override
    public boolean deleteDocById(String index, String id) {
        try {
            deleteRequest(index, id);
        } catch (Exception e) {
            log.error("删除索引 [{}] 数据id [{}] 失败", index, id);
            return false;
        }
        return true;
    }

    @Override
    public <T> T searchOneDoc(String index, SearchSourceBuilder searchSourceBuilder, Class<T> tClass) {
        try {
            SearchRequest searchRequest = new SearchRequest(index);
            searchRequest.source(searchSourceBuilder);
            log.info("DSL语句为：{}", searchRequest.source().toString());
            SearchResponse searchResponse = search(searchRequest);
            SearchHit[] hits = searchResponse.getHits().getHits();
            List<T> resultList = Lists.newArrayList();
            Arrays.stream(hits).forEach(hit -> {
                String source = hit.getSourceAsString();
                if (StringUtils.isNotEmpty(source)) {
                    T resultObject = JSONObject.parseObject(source, tClass);
                    resultList.add(resultObject);
                }
            });
            return resultList.size() == 0 ? null : resultList.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("搜索文档发生异常： [{}]", e.getMessage());
        }
        return null;
    }

    @Override
    public <T> List<T> searchAllDoc(String index, Class<T> tClass) {
        try {
            SearchResponse searchResponse = search(index);
            SearchHit[] hits = searchResponse.getHits().getHits();
            List<T> resultList = Lists.newArrayList();
            Arrays.stream(hits).forEach(hit -> {
                String source = hit.getSourceAsString();
                if (StringUtils.isNotEmpty(source)) {
                    T resultObject = JSONObject.parseObject(source, tClass);
                    resultList.add(resultObject);
                }
            });
            return resultList;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("搜索文档发生异常： [{}]", e.getMessage());
        }
        return null;
    }

    @Override
    public <T> T searchDocById(String index, String id, Class<T> tClass) {
        SearchRequest searchRequest = buildSearchRequest(index);
        try {
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.termQuery(BaseElasticsearchConstant.ES_ID, id));
            searchRequest.source(searchSourceBuilder);
            SearchResponse searchResponse = search(searchRequest);
            SearchHit[] hits = searchResponse.getHits().getHits();
            List<T> resultList = new ArrayList<>();
            Arrays.stream(hits).forEach(hit -> {
                String source = hit.getSourceAsString();
                if (StringUtils.isNotEmpty(source)) {
                    T resultObject = JSONObject.parseObject(source, tClass);
                    resultList.add(resultObject);
                }
            });
            return resultList.size() == 0 ? null : resultList.get(0);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }


    @Override
    public <T> List<T> searchObjectDocsByIdList(String index, List<String> idList, Class<T> tClass) {
        SearchRequest searchRequest = buildSearchRequest(index);
        try {
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.termsQuery(BaseElasticsearchConstant.ES_ID, idList));
            searchRequest.source(searchSourceBuilder);
            SearchResponse searchResponse = search(searchRequest);
            SearchHit[] hits = searchResponse.getHits().getHits();
            List<T> resultList = new ArrayList<>();
            Arrays.stream(hits).forEach(hit -> {
                String source = hit.getSourceAsString();
                if (StringUtils.isNotEmpty(source)) {
                    T resultObject = JSONObject.parseObject(source, tClass);
                    resultList.add(resultObject);
                }
            });
            return resultList;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return Lists.newArrayList();
    }

    @Override
    public <T> PageResponse<T> search(String index, SearchSourceBuilder searchSourceBuilder, Class<T> clazz, Integer pageNum, Integer pageSize) {
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.source(searchSourceBuilder);
        log.info("DSL语句为：{}", searchRequest.source().toString());
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            PageResponse<T> pageResponse = new PageResponse<>();
            pageResponse.setPageNum(pageNum);
            pageResponse.setPageSize(pageSize);
            pageResponse.setTotal(response.getHits().getTotalHits().value);
            List<T> dataList = new ArrayList<>();
            SearchHits hits = response.getHits();
            for (SearchHit hit : hits) {
                dataList.add(JSONObject.parseObject(hit.getSourceAsString(), clazz));
            }
            pageResponse.setList(dataList);
            return pageResponse;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ElasticsearchException(String.valueOf(HttpStatus.BAD_REQUEST),
                    "error to execute searching,because of " + e.getMessage());
        }
    }

    private String getESId(Object obj) {
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(obj));
        Object id = jsonObject.get("id");
        return JSON.toJSONString(id);
    }
}
