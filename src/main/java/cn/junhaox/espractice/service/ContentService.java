package cn.junhaox.espractice.service;

/**
 * @Author WJH
 * @Description
 * @date 2020/11/10 15:46
 * @Email ibytecode2020@gmail.com
 */

import cn.junhaox.espractice.entity.Content;
import cn.junhaox.espractice.mapper.ContentMapper;
import cn.junhaox.espractice.utils.ParseHtmlUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ContentService extends ServiceImpl<ContentMapper, Content> {

    private final ParseHtmlUtils parseHtmlUtils;

    private final ContentMapper contentMapper;

    private static final String FIELD_NAME = "title";

    private static final String INDEX_NAME = "jd_goods";

    private final RestHighLevelClient restHighLevelClient;

    public Boolean parseContent(String keyword) {
        try {
            List<Content> contentList = parseHtmlUtils.parseJDHtml(keyword);
            // 添加到数据库

            return this.saveBatch(contentList);

        } catch (Exception e) {
            return false;
        }
    }

    public List<Map<String, Object>> search(String keyword, int pageNo, int pageSize) {
        try {
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder(FIELD_NAME, keyword);
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.preTags("<span style='color: red'>")
                    .postTags("</span>")
                    .requireFieldMatch(false)
                    .field(FIELD_NAME);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            // 分页
            if (pageNo < 0) {
                pageNo = 0;
            }
            if (pageSize <= 0) {
                pageSize = 10;
            }
            searchSourceBuilder.from(pageNo)
                    .size(pageSize)
                    .highlighter(highlightBuilder)
                    .query(matchQueryBuilder)
                    .timeout(new TimeValue(60, TimeUnit.SECONDS));
            SearchRequest searchRequest = new SearchRequest(INDEX_NAME).source(searchSourceBuilder);
            List<Map<String, Object>> res = new ArrayList<>();
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            for (SearchHit hit : response.getHits().getHits()) {

                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                HighlightField title = highlightFields.get(FIELD_NAME);
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                Text[] fragments = title.fragments();

                StringBuilder sb = new StringBuilder();

                for (Text fragment : fragments) {
                    sb.append(fragment);
                }

                sourceAsMap.put(FIELD_NAME, sb.toString());


                res.add(hit.getSourceAsMap());
            }
            return res;
        } catch (IOException e) {
            return null;
        }


    }


    public Boolean addContentToES(List<Content> contentList) {
        try {
            BulkRequest bulkRequest = new BulkRequest();
            bulkRequest.timeout("2s");
            for (Content content : contentList) {
                IndexRequest request = new IndexRequest(INDEX_NAME);
                log.info("json后的数据：{}", JSON.toJSONString(content));
                bulkRequest.add(request.id(String.valueOf(content.getId())).source(JSON.toJSONString(content), XContentType.JSON));
            }

            BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);

            return !bulk.hasFailures();

        } catch (Exception e) {
            return false;
        }
    }

    public List<Content> getContents() {
        return this.list();
    }



}
