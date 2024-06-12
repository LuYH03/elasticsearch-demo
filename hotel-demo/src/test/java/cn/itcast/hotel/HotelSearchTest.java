package cn.itcast.hotel;

import cn.itcast.hotel.pojo.HotelDoc;
import com.alibaba.fastjson.JSON;
import org.apache.http.HttpHost;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.SuggestionBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Author: YHan
 * @Date: 2024/6/3 01:20
 * @Description:
 **/
@SpringBootTest
public class HotelSearchTest {
    @Autowired
    private RestHighLevelClient client;

    /**
     * 查询所有
     * @throws IOException
     */
    @Test
    void matchAllTest() throws IOException {
        SearchRequest request = new SearchRequest("hotel");
        request.source()
                .query(QueryBuilders.matchAllQuery());
        // 发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 解析响应
        handleResponse(response);
    }

    @Test
    void matchTest() throws IOException {
        // 准备request
        SearchRequest request = new SearchRequest("hotel");
        // 准备DSL
        request.source()
                .query(QueryBuilders.matchQuery("all","全季"));
        // 发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 解析响应
        handleResponse(response);
    }

    /**
     * 逻辑查询
     * @throws IOException
     */
    @Test
    void boolTest() throws IOException {
        // 准备request
        SearchRequest request = new SearchRequest("hotel");
        // 准备BoolQueryBuilder
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(QueryBuilders.termQuery("city","杭州"));
        boolQuery.filter(QueryBuilders.rangeQuery("price").lte(300));
        // 准备DSL
        request.source()
                .query(boolQuery);
        // 发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 解析响应
        handleResponse(response);
    }

    /**
     * 分页排序
     * @throws IOException
     */
    @Test
    void pageAndSortTest() throws IOException {
        SearchRequest request = new SearchRequest("hotel");
        request.source()
                .query(QueryBuilders.matchAllQuery());
        // 分页
        request.source().from(0).size(5);
        // 排序
        request.source().sort("price", SortOrder.ASC);
        // 发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 解析响应
        handleResponse(response);
    }

    /**
     * 字段高亮处理
     * @throws IOException
     */
    @Test
    void HighlightTest() throws IOException {
        // 准备request
        SearchRequest request = new SearchRequest("hotel");
        // 准备DSL
        request.source()
                .query(QueryBuilders.matchQuery("all","全季"));
        request.source().highlighter(new HighlightBuilder().field("name").requireFieldMatch(false));
        // 发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 解析响应
        handleResponse(response);
    }

    /**
     * 聚合操作
     */
    @Test
    void AggregationTest() throws IOException {
        SearchRequest request = new SearchRequest("hotel");
        request.source().size(0);
        request.source().aggregation(AggregationBuilders
                .terms("brandAgg")
                .field("brand")
                .size(10)
        );
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 解析响应结果
        Aggregations aggregations = response.getAggregations();
        Terms brandTerms = aggregations.get("brandAgg");
        List<? extends Terms.Bucket> buckets = brandTerms.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            String key = bucket.getKeyAsString();
            System.out.println(key);
        }
    }

    @Test
    void suggestionTest() throws IOException {
        SearchRequest request = new SearchRequest("hotel");
        request.source().suggest(new SuggestBuilder().addSuggestion("hotelSuggestion",
                SuggestBuilders
                        .completionSuggestion("suggestion")
                        .prefix("h")   // 搜索补全查询前缀
                        .skipDuplicates(true)
                        .size(10)
        ));
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        Suggest suggest = response.getSuggest();
        CompletionSuggestion suggestion = suggest.getSuggestion("hotelSuggestion");
        List<CompletionSuggestion.Entry.Option> options = suggestion.getOptions();

        for (CompletionSuggestion.Entry.Option option : options) {
            String key = option.getText().string();
            System.out.println(key);
        }
    }

    private static void handleResponse(SearchResponse response) {
        SearchHits searchHits = response.getHits();
        long totalHits = searchHits.getTotalHits().value;
        System.out.println("hotelDoc总条数: " + totalHits);
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            String source = hit.getSourceAsString();
            HotelDoc hotelDoc = JSON.parseObject(source, HotelDoc.class);
            // 获取高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (!CollectionUtils.isEmpty(highlightFields)){
                HighlightField highlightField = highlightFields.get("name");
                if (highlightField != null) {
                    String name = highlightField.getFragments()[0].toString();
                    // 回填文档数据
                    hotelDoc.setName(name);
                }
            }
            System.out.println("hotelDoc：" + hotelDoc);
        }
    }

    @BeforeEach
    void setUp() {
        client = new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://localhost:9200")
        ));
    }

    @AfterEach
    void tearDown() throws IOException {
        client.close();
    }

}
