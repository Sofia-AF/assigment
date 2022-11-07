package co.empathy.academy.assigment.controller;

import co.empathy.academy.assigment.model.Movie;
import co.empathy.academy.assigment.model.SimpleResponse;
import co.empathy.academy.assigment.service.ElasticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class IndexController {
    @Autowired
    private ElasticService elastic;

    /**
     * GET request that returns all created indexes
     * Form : GET /index
     * @return ResponseBody with right status and custom body
     */
    @GetMapping("/index")
    public ResponseEntity showAllIndexes() {
        SimpleResponse sr = elastic.showAllIndexes();
        return ResponseEntity.status(sr.getStatusCode()).body(sr.getBodyMessage());
    }

    /**
     * PUT request that creates a new index
     * Form: PUT /{new_index_name}
     * @param indexName : name of the index to create
     * @param body : request body with settings/mappings
     * @return ResponseBody with right status and custom body
     */
    @PutMapping("/{indexName}")
    public ResponseEntity addIndex(@PathVariable String indexName, @RequestBody String body) {
        SimpleResponse sr = elastic.createIndex(indexName, body);
        return ResponseEntity.status(sr.getStatusCode()).body(sr.getBodyMessage());
    }

    /**
     * POST request that indexes a document in a given index
     * Form: POST /{given_index}/_doc
     * @param indexName : index where to index the new document
     * @param movie : request body with info of the movie to index
     * @return ResponseBody with right status and custom body
     */
    @PostMapping("/{indexName}/_doc")
    public ResponseEntity indexDocument(@PathVariable String indexName, @RequestBody Movie movie) {
        SimpleResponse sr = elastic.indexDocument(indexName, null, movie);
        return ResponseEntity.status(sr.getStatusCode()).body(sr.getBodyMessage());
    }

    /**
     * PUT request that indexes a document in a given index with a specific id
     * Form: PUT /{given_index}/_doc/{doc_id}
     * @param indexName : index where to index the new document
     * @param movieId  : document identifier (optional)
     * @param movie : request body with info of the movie to index
     * @return ResponseBody with right status and custom body
     */
    @PutMapping("/{indexName}/_doc/{movieId}")
    public ResponseEntity indexDocument(@PathVariable String indexName, @PathVariable String movieId,
                                                      @RequestBody Movie movie) {
        SimpleResponse sr = elastic.indexDocument(indexName, movieId, movie);
        return ResponseEntity.status(sr.getStatusCode()).body(sr.getBodyMessage());
    }

    @GetMapping("{indexName}/_search")
    public ResponseEntity searchIndex(@PathVariable String indexName, @RequestBody String body){
        SimpleResponse sr = elastic.searchIndex(indexName, body);
        return ResponseEntity.status(sr.getStatusCode()).body(sr.getBodyMessage());
    }

    @PostMapping("/index/imdb")
    public ResponseEntity indexIMDB(@RequestParam("file") MultipartFile multipartFile ){
        SimpleResponse sr = elastic.bulkIndex(multipartFile);
        return ResponseEntity.status(sr.getStatusCode()).body(sr.getBodyMessage());
    }
}
