package com.proximity.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.proximity.adaptor.FloatTypeAdapter;
import com.proximity.adaptor.IntTypeAdapter;
import com.proximity.document.RestaurantsDocument;
import com.proximity.utils.Constants;
import com.proximity.utils.JsonUtil;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class IndexService {
    private final RestHighLevelClient client;
    private final ObjectMapper objectMapper;
    private static Integer id = 1;

    public IndexService(RestHighLevelClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    /**
     * @param document
     * @return
     * @throws Exception
     */
    public String createRestaurantDocuments(RestaurantsDocument document) throws Exception {
        ObjectMapper Obj = new ObjectMapper();
        final String userStr = Obj.writeValueAsString(document);
        final IndexRequest indexRequest = new IndexRequest(Constants.INDEX)
                .source(userStr, XContentType.JSON);
        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        return "added";
    }

    public String createBulkRestaurantDocuments() throws Exception {
        if (!indexExist()) {
            final Boolean indexCreationResponse = createIndexMapping();
            final BulkResponse bulkResponse = indexBulkDocuments();
            if (bulkResponse.hasFailures()) {
            }
            if (bulkResponse.hasFailures()) {
                System.out.println("Some of the bulk operations had failures");
            }
            for (BulkItemResponse bulkItemResponse : bulkResponse) {
                DocWriteResponse itemResponse = bulkItemResponse.getResponse();
                switch (bulkItemResponse.getOpType()) {
                    case INDEX:
                    case CREATE:
                        IndexResponse indexResponse = (IndexResponse) itemResponse;
                        System.out.println("index resp " + indexResponse.getResult().toString());
                        break;
                    case UPDATE:
                        UpdateResponse updateResponse = (UpdateResponse) itemResponse;
                        break;
                    case DELETE:
                        DeleteResponse deleteResponse = (DeleteResponse) itemResponse;
                }
            }
        }
        return "created";
    }

    private boolean indexExist() throws IOException {
        final GetIndexRequest request = new GetIndexRequest(Constants.INDEX);
        final boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        if (exists) {
        }
        return exists;
    }

    private BulkRequest createBulkRequest(JSONArray restaurantList) {
        BulkRequest request = new BulkRequest();
        restaurantList.forEach(restaurant -> parseResatuarntListObject((JSONObject) restaurant, request, id++));
        return request;
    }

    private void parseResatuarntListObject(JSONObject restaurantObject, BulkRequest request, int id) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(int.class, new IntTypeAdapter())
                .registerTypeAdapter(Integer.class, new IntTypeAdapter())
                .registerTypeAdapter(Float.class, new FloatTypeAdapter())
                .registerTypeAdapter(float.class, new FloatTypeAdapter())
                .create();
        RestaurantsDocument restaurant = gson.fromJson(restaurantObject.toJSONString(), RestaurantsDocument.class);
        Map<String, Object> documentMapper = objectMapper.convertValue(restaurant, Map.class);
        IndexRequest indexRequest = new IndexRequest(Constants.INDEX)
                .source(documentMapper);
        request.add(indexRequest);
    }

    /**
     * Get JSON from file in a JSON tree format.
     *
     * @param fileName file which needs to be read into JSON.
     * @return
     */
    private String getStringFromFile(String fileName) throws IOException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        InputStream in = classLoader.getResourceAsStream(fileName);
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString(StandardCharsets.UTF_8.name());
    }

    private BulkResponse indexBulkDocuments() throws ParseException, IOException, org.json.simple.parser.ParseException {
        final Object yelpDataListObj = new JSONParser().parse(JsonUtil.getStringFromFile(Constants.FILE_NAME));
        final JSONArray yelpDataList = (JSONArray) yelpDataListObj;
        final BulkRequest indexRequest = createBulkRequest(yelpDataList);
        final BulkResponse bulkResponse = client.bulk(indexRequest, RequestOptions.DEFAULT);
        return bulkResponse;

    }

    private Boolean createIndexMapping() throws Exception {
        final String mapping = JsonUtil.getStringFromFile(Constants.FILE_MAPPING);
        final CreateIndexRequest request = new CreateIndexRequest(Constants.INDEX);
        request.source(mapping, XContentType.JSON);
        final CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        return createIndexResponse.isAcknowledged();
    }
}
