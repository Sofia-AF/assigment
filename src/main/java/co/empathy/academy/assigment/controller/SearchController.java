package co.empathy.academy.assigment.controller;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.empathy.academy.assigment.model.Movie;
import co.empathy.academy.assigment.model.SearchResponseCustom;
import co.empathy.academy.assigment.services.QueryService;
import co.empathy.academy.assigment.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
public class SearchController {
    @Autowired
    private SearchService searchService;

    @Autowired
    QueryService queryService;

    @GetMapping("/search")
    public SearchResponseCustom searchQuery(@RequestParam(value="query") String query) throws IOException {
        return searchService.searchQuery(query);
    }

    @GetMapping("{indexName}/_search")
    public ResponseEntity searchIndex(@PathVariable String indexName, @RequestBody String body) {
        List<Movie> sr = searchService.searchIndex(indexName, body);
        return ResponseEntity.ok().body(sr);
    }

}