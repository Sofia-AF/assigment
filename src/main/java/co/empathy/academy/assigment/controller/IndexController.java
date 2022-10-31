package co.empathy.academy.assigment.controller;

import co.empathy.academy.assigment.model.Movie;
import co.empathy.academy.assigment.service.ElasticClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class IndexController {
    @Autowired
    private ElasticClient elasticClient;

    /**
     * GET request that returns all created indexes
     * Form : GET /index
     * @return String showing all created indexes
     * @throws IOException : when can't perform GET request to ElasticSearch
     */
    @GetMapping("/index")
    public String showAllIndexes() throws IOException {
        return elasticClient.showAllIndexes();
    }

    /**
     * PUT request that creates a new index
     * Form: PUT /{new_index_name}
     * @param indexName : name of the index to create
     * @param body : request body with settings/mappings
     * @throws IOException : when can't perform PUT request to ElasticSearch
     */
    @PutMapping("/{indexName}")
    public void addIndex(@PathVariable String indexName, @RequestBody String body) throws IOException {
        elasticClient.createIndex(indexName, body);
    }

    /**
     * POST request that indexes a document in a given index
     * Form: POST /{given_index}/_doc
     * @param indexName : index where to index the new document
     * @param movie : request body with info of the movie to index
     * @throws IOException : when can't perform POST request to ElasticSearch
     */
    @PostMapping("/{indexName}/_doc")
    public void indexDocument(@PathVariable String indexName, @RequestBody Movie movie) throws IOException {
        elasticClient.indexDocument(indexName, null, movie);
    }

    /**
     * PUT request that indexes a document in a given index with a specific id
     * Form: PUT /{given_index}/_doc/{doc_id}
     * @param indexName : index where to index the new document
     * @param movieId  : document identifier (optional)
     * @param movie : request body with info of the movie to index
     * @throws IOException : when can't perform PUT request to ElasticSearch
     */
    @PutMapping("/{indexName}/_doc/{movieId}")
    public void indexDocumentWithId(@PathVariable String indexName, @PathVariable String movieId,
                                    @RequestBody Movie movie) throws IOException {
        elasticClient.indexDocument(indexName, movieId, movie);
    }
}
