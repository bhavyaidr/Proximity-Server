package com.proximity.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proximity.document.RestaurantsDocument;
import com.proximity.request.SearchRequestParams;
import com.proximity.utils.Constants;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.GeoDistanceQueryBuilder;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {
    private RestHighLevelClient client;
    private ObjectMapper objectMapper;

    public SearchService(RestHighLevelClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    public List<RestaurantsDocument> inProximity(SearchRequestParams params) throws IOException {
        SearchRequest searchRequest = new SearchRequest(Constants.INDEX);
        SearchSourceBuilder searchSourceBuilder = constructQuery(new SearchSourceBuilder(), params);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return getSearchResults(searchResponse, params.isSearch());
    }

    private SearchSourceBuilder constructQuery(SearchSourceBuilder searchSourceBuilder, SearchRequestParams params) {
        if (params.getSearchType().equals("city")) {
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("city", params.getQuery());
            searchSourceBuilder.query(matchQueryBuilder);
        } else if (params.getSearchType().equals("cusine")) {
            MatchPhraseQueryBuilder matchPhraseQuery = new MatchPhraseQueryBuilder("cusine", params.getQuery());
            searchSourceBuilder.query(matchPhraseQuery);
        } else if (params.getSearchType().equals("rating")) {
            BoolQueryBuilder boolQuery = new BoolQueryBuilder();
            BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
            queryBuilder.should(QueryBuilders.rangeQuery("rating").lt(3.0));
            boolQuery.must(queryBuilder);
            searchSourceBuilder.query(boolQuery);
        } else if (params.getSearchType().equals("coordinates")) {
            GeoDistanceQueryBuilder geoDistanceQueryBuilder = new GeoDistanceQueryBuilder("coordinates");
            geoDistanceQueryBuilder.point(28.6177324058, 77.2848711535);
            geoDistanceQueryBuilder.distance("1km");
            searchSourceBuilder.query(geoDistanceQueryBuilder).size(10000);
        }
        return searchSourceBuilder;
    }

    private List<RestaurantsDocument> getSearchResults(SearchResponse searchResponse, final Boolean isSearch) {
        SearchHit[] searchHits = searchResponse.getHits().getHits();
        List<RestaurantsDocument> restaturantDocuments = new ArrayList<RestaurantsDocument>();
        for (SearchHit hit : searchHits) {
            /*if (isSearch) {
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                HighlightField highlight = highlightFields.get("city.autocomplete");
                Text[] fragments = highlight.fragments();
                String fragmentString = fragments[0].string();
                hit.getSourceAsMap().put("highlightedString", fragmentString);
            }*/
            restaturantDocuments.add(objectMapper.convertValue(hit.getSourceAsMap(), RestaurantsDocument.class));
        }
        return restaturantDocuments;
    }

}
