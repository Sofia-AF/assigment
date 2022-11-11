package co.empathy.academy.assigment.services;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.empathy.academy.assigment.model.Movie;
import co.empathy.academy.assigment.model.SearchResponseCustom;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

// This is the service class that will implement your search service logic
// It has a SearchEngine as a dependency
// Endpoint: /search (controller) -> SearchService -> SearchEngine
public class SearchServiceImpl implements SearchService {

    private final SearchEngine searchEngine;

    public SearchServiceImpl(SearchEngine searchEngine) {
        this.searchEngine = searchEngine;
    }

    @Override
    public int simpleSearch(String query) {
        return searchEngine.simpleSearch(query);
    }

    @Override
    public SearchResponseCustom searchQuery(String query) throws IOException {
        return searchEngine.searchQuery(query);
    }

    @Override
    public List<Movie> searchIndex(String indexName, String body) {
        return searchEngine.searchIndex(indexName, body);
    }

    @Override
    public List<Movie> search(Optional<String> genre, Optional<Integer> maxYear, Optional<Integer> minYear,
                                 Optional<Integer> maxMinutes, Optional<Integer> minMinutes,
                                 Optional<Double> maxScore, Optional<Double> minScore, Optional<String> type) {
        return searchEngine.search(genre, maxYear, minYear, maxMinutes, minMinutes, maxScore, minScore, type);
    }


}
