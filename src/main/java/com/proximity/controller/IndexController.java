package com.proximity.controller;

import com.proximity.document.RestaurantsDocument;
import com.proximity.service.IndexService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class IndexController {
    private final IndexService indexService;

    public IndexController(IndexService service) {
        this.indexService = service;
    }

    @PostMapping("/restaurant")
    public ResponseEntity createRestaturantDocument(
            @RequestBody RestaurantsDocument document) throws Exception {
        ResponseEntity responseEntity = new ResponseEntity(indexService.createRestaurantDocuments(document), HttpStatus.CREATED);
        return responseEntity;
    }

    @PostMapping("/restaurantBulk")
    public ResponseEntity createBulkRestaurantDocuments() throws Exception {
        ResponseEntity responseEntity = new ResponseEntity(indexService.createBulkRestaurantDocuments(), HttpStatus.CREATED);
        return responseEntity;
    }

}
