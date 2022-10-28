package co.empathy.academy.assigment.controller;

import co.empathy.academy.assigment.model.Movie;
import co.empathy.academy.assigment.service.ElasticClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class IndexController {
    @Autowired
    private ElasticClient elasticClient;

    // GET request that returns all indexes
    @GetMapping("/index")
    public String showAllIndexes() {
        return elasticClient.showAllIndexes();
    }

    // PUT request that creates a new index
    @PutMapping("/{indexName}")
    public void addIndex(@PathVariable String indexName) {
        elasticClient.createIndex(indexName);
    }

    // POST request that indexes a document in a given index
    @PostMapping("/{indexName}/_doc")
    public void indexDocument(@PathVariable String indexName, @RequestBody Movie movie){
        elasticClient.indexDocument(indexName, movie);
    }

    // PUT request that indexes a document in a given index with a specific id
    @PutMapping("/{indexName}/_doc/{movieId}")
    public void indexDocumentWithId(@PathVariable String indexName, @PathVariable String movieId, @RequestBody Movie movie){
        elasticClient.indexDocumentWithId(indexName, movieId, movie);
    }



}
