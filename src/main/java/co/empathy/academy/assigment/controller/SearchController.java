package co.empathy.academy.assigment.controller;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.empathy.academy.assigment.model.Movie;
import co.empathy.academy.assigment.model.SearchResponseCustom;
import co.empathy.academy.assigment.services.QueryService;
import co.empathy.academy.assigment.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
public class SearchController {
    @Autowired
    private SearchService searchService;

    /**
     * Returns simple text with the query value and the elastic version
     * @param query : simple query we're searching
     * @return : SearchResponseCustom with the correct values
     * @throws IOException
     */
    @GetMapping("/search/simple")
    public SearchResponseCustom searchQuery(@RequestParam(value="query") String query) throws IOException {
        return searchService.searchQuery(query);
    }

    /**
     * Searches query in given index
     * @param indexName : name of the index we're searching
     * @param body : body of the request with settings and mappings
     * @return
     */
    @GetMapping("{indexName}/_search")
    public ResponseEntity searchIndex(@PathVariable String indexName, @RequestBody String body) {
        List<Movie> sr = searchService.searchIndex(indexName, body);
        return ResponseEntity.ok().body(sr);
    }

    @GetMapping(value="/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity search(@RequestParam(value = "genre", required = false) Optional<String> genre,
                                 @RequestParam(value = "maxYear", required = false) Optional<Integer> maxYear,
                                 @RequestParam(value = "minYear", required = false) Optional<Integer> minYear,
                                 @RequestParam(value = "maxMinutes", required = false) Optional<Integer> maxMinutes,
                                 @RequestParam(value = "minMinutes", required = false) Optional<Integer> minMinutes,
                                 @RequestParam(value = "maxScore", required = false) Optional<Double> maxScore,
                                 @RequestParam(value = "minScore", required = false) Optional<Double> minScore,
                                 @RequestParam(value = "type", required = false) Optional<String> type
    ) {
        List<Movie> sr = searchService.search(genre, maxYear, minYear, maxMinutes, minMinutes, maxScore,minScore, type);
        return ResponseEntity.ok().body(sr);
    }

}