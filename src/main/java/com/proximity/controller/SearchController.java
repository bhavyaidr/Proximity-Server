package com.proximity.controller;

import com.proximity.request.SearchRequestParams;
import com.proximity.service.SearchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;

@Controller
public class SearchController {
    private final SearchService searchService;

    public SearchController(SearchService service) {
        this.searchService = service;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/restaurant/search")
    public ResponseEntity searchDocuments(@RequestBody SearchRequestParams document) throws IOException {
        ResponseEntity responseEntity = new ResponseEntity(searchService.inProximity(document), HttpStatus.OK);
        return responseEntity;
    }
}
