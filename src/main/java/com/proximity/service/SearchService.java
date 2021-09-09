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
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
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
        //TODO: need to check autocomplete results are shown or not on FE side
        return getSearchResults(searchResponse, params.isSearch());
    }

    private SearchSourceBuilder constructQuery(SearchSourceBuilder searchSourceBuilder, SearchRequestParams params) {
        String CITY = "city";
        String COORDINATES = "coordinates";
        String RATING = "rating";
        //TODO: Replace spelling of "cusine" to "cuisine" in mapping and documents
        String CUISINE = "cusine";
        if (params.getSearchType().equals(CITY)) {
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder(params.getSearchType(), params.getQuery());
            searchSourceBuilder.query(matchQueryBuilder);
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            HighlightBuilder.Field highlightCity =
                    new HighlightBuilder.Field("*");
            highlightCity.highlighterType("plain");
            highlightBuilder.field(highlightCity);
            searchSourceBuilder.highlighter(highlightBuilder);
        } else if (params.getSearchType().equals(CUISINE)) {
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder(params.getSearchType(), params.getQuery());
            searchSourceBuilder.query(matchQueryBuilder);
        } else if (params.getSearchType().equals(RATING)) {
            BoolQueryBuilder boolQuery = new BoolQueryBuilder();
            BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
            queryBuilder.should(QueryBuilders.rangeQuery(params.getSearchType()).lt(3.0));
            boolQuery.must(queryBuilder);
            searchSourceBuilder.query(boolQuery);
            //TODO: Replace the hardcoded values of lat and long, and find current coordinates using Geo location API (from FE)
        } else if (params.getSearchType().equals(COORDINATES)) {
            GeoDistanceQueryBuilder geoDistanceQueryBuilder = new GeoDistanceQueryBuilder(params.getSearchType());
            geoDistanceQueryBuilder.point(28.6177324058, 77.2848711535);
            geoDistanceQueryBuilder.distance("1km");
            searchSourceBuilder.query(geoDistanceQueryBuilder).size(10000);
        }
        return searchSourceBuilder;
    }

    private List<RestaurantsDocument> getSearchResults(SearchResponse searchResponse, final Boolean isSearch) {
        SearchHit[] searchHits = searchResponse.getHits().getHits();
        List<RestaurantsDocument> restaurantDocuments = new ArrayList<RestaurantsDocument>();
        /*for (SearchHit hit : searchHits) {
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField highlight = highlightFields.get("city");
            Text[] fragments = highlight.fragments();
            String fragmentString = fragments[0].string();
            hit.getSourceAsMap().put("highlightedString", fragmentString);
            restaurantDocuments.add(objectMapper.convertValue(hit.getSourceAsMap(), RestaurantsDocument.class));
        }*/
        return restaurantDocuments;
    }

}
